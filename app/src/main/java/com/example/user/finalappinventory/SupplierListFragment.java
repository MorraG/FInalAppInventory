package com.example.user.finalappinventory;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.finalappinventory.adapters.SupplierCursorAdapter;
import com.example.user.finalappinventory.data.InventoryContract;
import com.example.user.finalappinventory.utils.Costants;

public class SupplierListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, SupplierCursorAdapter.ItemClickListener {

    private String mTypeOfRelationship;
    private SupplierCursorAdapter mCursorAdapter;
    private Context mContext;
    private Uri mCurrentSupplierUri;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public SupplierListFragment() {
    }

    @SuppressLint("StringFormatInvalid")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        Bundle bundle = getArguments();
        getActivity().setTitle(getString(R.string.all_supplier));
        //get the list of suppliers from the database

        mCursorAdapter = new SupplierCursorAdapter(getActivity(), null, this);
        ListView listView = rootView.findViewById(R.id.list);
        listView.setAdapter(mCursorAdapter);
        ConstraintLayout empty_screen = rootView.findViewById(R.id.empty_view);
        TextView empty_tv = rootView.findViewById(R.id.empty_text);
        empty_tv.setText(getString(R.string.no_suppliers_found, mTypeOfRelationship));
        listView.setEmptyView(empty_screen);
        getLoaderManager().initLoader(Costants.SUPPLIER_LOADER, null, this);
        return rootView;
    }

    @Override
    public void onItemClicked(long id) {
        AddSupplierFragment addSupplierFrag = new AddSupplierFragment();
        Bundle args = new Bundle();
        Uri currentSupplierUri =
                ContentUris.withAppendedId(InventoryContract.SupplierEntry.CONTENT_URI, id);
        args.putString(Costants.SUPPLIER_URI, currentSupplierUri.toString());
        addSupplierFrag.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, addSupplierFrag)
                .addToBackStack(null)
                .commit();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {InventoryContract.SupplierEntry._ID,
                InventoryContract.SupplierEntry.SUPPLIER_NAME,
                InventoryContract.SupplierEntry.SUPPLIER_CONTACT_PERSON,
                InventoryContract.SupplierEntry.SUPPLIER_PHONE};
        return new CursorLoader(mContext, InventoryContract.SupplierEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    // DA QUI tutto quello che riguarda l'operazione di cancellazione SUPL
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
                deleteProduct();
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
    private void deleteProduct(){
        if(mCurrentSupplierUri != null){
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

}