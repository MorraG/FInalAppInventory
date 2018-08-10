package com.example.user.finalappinventory;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.finalappinventory.data.InventoryContract;
import com.example.user.finalappinventory.utils.Costants;

public class AddSupplierFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private EditText supplierName_et;
    private EditText supplierAddress_et;
    private EditText supplierEmail_et;
    private EditText supplierPhone_et;
    private EditText supplierContactPerson_et;
    private Uri mCurrentSupplierUri;

    public AddSupplierFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_supplier, container, false);
        setHasOptionsMenu(true);

        supplierName_et = rootView.findViewById(R.id.editSupplierName);
        supplierAddress_et = rootView.findViewById(R.id.editSupplierAddress);
        supplierEmail_et = rootView.findViewById(R.id.editSupplierEMail);
        supplierPhone_et = rootView.findViewById(R.id.editSupplierPhone);
        supplierContactPerson_et = rootView.findViewById(R.id.editContactPerson);
        Button save_supplier_btn = rootView.findViewById(R.id.save_supplier_btn);
        save_supplier_btn.setOnClickListener(this);

        Bundle bundle = getArguments();
        String uriString = null;

        if (bundle != null) uriString = bundle.getString(Costants.SUPPLIER_URI);
        if (uriString != null) mCurrentSupplierUri = Uri.parse(uriString);

        //Set the title that corresponds to the fragment
        if (mCurrentSupplierUri == null) {

            getActivity().setTitle(getString(R.string.add_supplier));
            getActivity().invalidateOptionsMenu();
        } else {
            getActivity().setTitle(getString(R.string.edit_supplier));
            getLoaderManager().initLoader(Costants.SUPPLIER_LOADER, null, this);
        }

        return rootView;

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentSupplierUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_with_delete, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            openAlertDialogForDelete();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAlertDialogForDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setMessage("Do you want to delete this item from the database?");
        builder.setPositiveButton("Yes, delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletesupplier();
                getActivity().onBackPressed();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.create();
        builder.show();
    }

    private void deletesupplier() {
        if (mCurrentSupplierUri != null) {
            int rowsDeleted = getActivity().getContentResolver().delete(mCurrentSupplierUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(getActivity(), R.string.Error_during_delete,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(getActivity(), R.string.Successful_deletingSupl,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void onClick(View v) {

        if (saveSupplier()) {
            SupplierListFragment supFrag = new SupplierListFragment();
            Bundle args = new Bundle();
            supFrag.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, supFrag)
                    .addToBackStack(null)
                    .commit();
        }
    }


    private boolean saveSupplier() {

        String supplierName = supplierName_et.getText().toString().trim();
        if (TextUtils.isEmpty(supplierName)) {
            Toast.makeText(getActivity(), R.string.supplier_name_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        String supplierAddress = supplierAddress_et.getText().toString().trim();
        if (TextUtils.isEmpty(supplierAddress)) {
            Toast.makeText(getActivity(), R.string.supplier_address_empty, Toast.LENGTH_SHORT).show();
            return false;
        }
        String supplierEmail = supplierEmail_et.getText().toString().trim();
        String supplierPhone = supplierPhone_et.getText().toString().trim();
        if (TextUtils.isEmpty(supplierPhone)) {
            Toast.makeText(getActivity(), R.string.supplier_phone_empty, Toast.LENGTH_SHORT).show();
            return false;
        }
        String contactPerson = supplierContactPerson_et.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(InventoryContract.SupplierEntry.SUPPLIER_NAME, supplierName);
        values.put(InventoryContract.SupplierEntry.SUPPLIER_ADDRESS, supplierAddress);
        values.put(InventoryContract.SupplierEntry.SUPPLIER_EMAIL, supplierEmail);
        values.put(InventoryContract.SupplierEntry.SUPPLIER_PHONE, supplierPhone);
        values.put(InventoryContract.SupplierEntry.SUPPLIER_CONTACT_PERSON, contactPerson);

        if (mCurrentSupplierUri == null) {
            //This is a new supplier entry
            Uri newUri = getActivity().getContentResolver().insert(InventoryContract.SupplierEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(getActivity(), R.string.error_saving, Toast.LENGTH_SHORT).show();
                return false;
            } else {
                Toast.makeText(getActivity(), R.string.enterprise_successfully_saved, Toast.LENGTH_SHORT).show();
                return true;
            }
        } else {
            // Otherwise this is an existing supplier, so update the entry
            int rowsAffected = getActivity().getContentResolver().update(mCurrentSupplierUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(getActivity(), R.string.error_updating, Toast.LENGTH_SHORT).show();
                return false;
            } else {
                Toast.makeText(getActivity(), R.string.successfully_updated, Toast.LENGTH_SHORT).show();
                return true;
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                InventoryContract.SupplierEntry._ID,
                InventoryContract.SupplierEntry.SUPPLIER_NAME,
                InventoryContract.SupplierEntry.SUPPLIER_CONTACT_PERSON,
                InventoryContract.SupplierEntry.SUPPLIER_PHONE,
                InventoryContract.SupplierEntry.SUPPLIER_ADDRESS,
                InventoryContract.SupplierEntry.SUPPLIER_EMAIL};
        return new CursorLoader(getActivity(), mCurrentSupplierUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int suplNameColumnIndex = cursor.getColumnIndex(InventoryContract.SupplierEntry.SUPPLIER_NAME);
            int contactPersonColumnIndex = cursor.getColumnIndex(InventoryContract.SupplierEntry.SUPPLIER_CONTACT_PERSON);
            int phoneColumnIndex = cursor.getColumnIndex(InventoryContract.SupplierEntry.SUPPLIER_PHONE);
            int addressColumnIndex = cursor.getColumnIndex(InventoryContract.SupplierEntry.SUPPLIER_ADDRESS);
            int eMailColumnIndex = cursor.getColumnIndex(InventoryContract.SupplierEntry.SUPPLIER_EMAIL);

            String suplName = cursor.getString(suplNameColumnIndex);
            String contactPerson = cursor.getString(contactPersonColumnIndex);
            final String phone = cursor.getString(phoneColumnIndex);
            String address = cursor.getString(addressColumnIndex);
            String eMail = cursor.getString(eMailColumnIndex);

            supplierName_et.setText(suplName);
            supplierAddress_et.setText(address);
            supplierEmail_et.setText(eMail);
            supplierPhone_et.setText(phone);
            supplierContactPerson_et.setText(contactPerson);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
