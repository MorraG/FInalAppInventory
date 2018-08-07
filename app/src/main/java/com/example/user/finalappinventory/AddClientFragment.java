package com.example.user.finalappinventory;

import android.content.ContentValues;
import android.content.DialogInterface;
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

public class AddClientFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private EditText clientName_et;
    private EditText clientAddress_et;
    private EditText clientEmail_et;
    private EditText clientPhone_et;
    private EditText clientContactPerson_et;
    private String mTypeOfRelationship;
    private boolean mUserIsAddingAProduct;
    private Uri mCurrentClientUri;

    public AddClientFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_client, container, false);
        setHasOptionsMenu(true);
        Bundle bundle = getArguments();
        mTypeOfRelationship = bundle.getString(Costants.RELATION_TYPE);
        if(bundle.containsKey(Costants.REQUEST_CODE)){
            mUserIsAddingAProduct = true;
        }
        String uriString = null;
        if(bundle.containsKey(Costants.ENTERPRISE_URI)){
            uriString = bundle.getString(Costants.ENTERPRISE_URI);
        }
        if(uriString != null){
            mCurrentClientUri = Uri.parse(uriString);
        }

        //Set the title that corresponds to the fragment
        if(mCurrentClientUri == null){
            getActivity().setTitle(getString(R.string.frag_add_client, mTypeOfRelationship));
            getActivity().invalidateOptionsMenu();
        } else {
            getActivity().setTitle(getString(R.string.edit_client, mTypeOfRelationship));
            getLoaderManager().initLoader(Costants.SINGLE_ENTERPRISE_LOADER, null, this);
        }

        clientName_et = rootView.findViewById(R.id.editSupplierName);
        clientAddress_et = rootView.findViewById(R.id.editSupplierAddress);
        clientEmail_et = rootView.findViewById(R.id.editSupplierEMail);
        clientPhone_et = rootView.findViewById(R.id.editSupplierPhone);
        clientContactPerson_et = rootView.findViewById(R.id.editContactPerson);
        Button save_supplier_btn = rootView.findViewById(R.id.save_client_btn);
        save_supplier_btn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        saveSupplier();
        if(mUserIsAddingAProduct){
            getActivity().onBackPressed();
        }
    }

    private void saveSupplier(){

        String enterpriseName = clientName_et.getText().toString().trim();
        String enterpriseAddress = clientAddress_et.getText().toString().trim();
        String enterpriseEmail = clientEmail_et.getText().toString().trim();
        String enterprisePhone = clientPhone_et.getText().toString().trim();
        String contactPerson = clientContactPerson_et.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(InventoryContract.ClientEntry.CLIENT_NAME, enterpriseName);
        values.put(InventoryContract.ClientEntry.CLIENT_ADDRESS, enterpriseAddress);
        values.put(InventoryContract.ClientEntry.CLIENT_EMAIL, enterpriseEmail);
        values.put(InventoryContract.ClientEntry.CLIENT_PHONE, enterprisePhone);
        values.put(InventoryContract.ClientEntry.CLIENT_CONTACT_PERSON, contactPerson);
        values.put(InventoryContract.ClientEntry.RELATION_TYPE, mTypeOfRelationship);

        if (mCurrentClientUri == null) {
            //This is a new supplier or client
            Uri newUri = getActivity().getContentResolver().insert(InventoryContract.ClientEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(getActivity(), R.string.error_saving, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), R.string.enterprise_successfully_saved, Toast.LENGTH_SHORT).show();
            }
        } else{
            // Otherwise this is an existing enterprise, so update the entry
            int rowsAffected = getActivity().getContentResolver().update(mCurrentClientUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(getActivity(), R.string.error_updating, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), R.string.successfully_updated, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentClientUri == null) {
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
        if(item.getItemId() == R.id.action_delete){
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
                deleteEnterprise();
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

    private void deleteEnterprise(){
        if(mCurrentClientUri != null){
            int rowsDeleted = getActivity().getContentResolver().delete(mCurrentClientUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(getActivity(), "Error during delete",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(getActivity(), "product successfully deleted",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {InventoryContract.ClientEntry._ID, InventoryContract.ClientEntry.CLIENT_NAME, InventoryContract.ClientEntry.CLIENT_CONTACT_PERSON,
                InventoryContract.ClientEntry.CLIENT_PHONE, InventoryContract.ClientEntry.CLIENT_ADDRESS, InventoryContract.ClientEntry.CLIENT_EMAIL};
        return new CursorLoader(getActivity(), mCurrentClientUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int enterpriseNameColumnIndex = cursor.getColumnIndex(InventoryContract.ClientEntry.CLIENT_NAME);
            int contactPersonColumnIndex = cursor.getColumnIndex(InventoryContract.ClientEntry.CLIENT_CONTACT_PERSON);
            int phoneColumnIndex = cursor.getColumnIndex(InventoryContract.ClientEntry.CLIENT_PHONE);
            int addressColumnIndex = cursor.getColumnIndex(InventoryContract.ClientEntry.CLIENT_ADDRESS);
            int eMailColumnIndex = cursor.getColumnIndex(InventoryContract.ClientEntry.CLIENT_EMAIL);

            String enterpriseName = cursor.getString(enterpriseNameColumnIndex);
            String contactPerson = cursor.getString(contactPersonColumnIndex);
            final String phone = cursor.getString(phoneColumnIndex);
            String address = cursor.getString(addressColumnIndex);
            String eMail = cursor.getString(eMailColumnIndex);

            clientName_et.setText(enterpriseName);
            clientAddress_et.setText(address);
            clientEmail_et.setText(eMail);
            clientPhone_et.setText(phone);
            clientContactPerson_et.setText(contactPerson);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
