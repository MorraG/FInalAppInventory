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

import com.example.user.finalappinventory.adapters.ClientCursorAdapter;
import com.example.user.finalappinventory.data.InventoryContract;
import com.example.user.finalappinventory.utils.Costants;

public class ClientListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, ClientCursorAdapter.ItemClickListener {

    private String mTypeOfRelationship;
    private ClientCursorAdapter mCursorAdapter;
    private Context mContext;
    private Uri mCurrentClientUri;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public ClientListFragment() {
    }

    @SuppressLint("StringFormatInvalid")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        Bundle bundle = getArguments();
        getActivity().setTitle(getString(R.string.all_clients));
        //get the list of clients from the database

        mCursorAdapter = new ClientCursorAdapter(getActivity(), null, this);
        ListView listView = rootView.findViewById(R.id.list);
        listView.setAdapter(mCursorAdapter);
        ConstraintLayout empty_screen = rootView.findViewById(R.id.empty_view);
        TextView empty_tv = rootView.findViewById(R.id.empty_text);
        empty_tv.setText(getString(R.string.no_clients_found, mTypeOfRelationship));
        listView.setEmptyView(empty_screen);
        getLoaderManager().initLoader(Costants.CLIENT_LOADER, null, this);
        return rootView;
    }

    @Override
    public void onItemClicked(long id) {
        AddClientFragment addClientFrag = new AddClientFragment();
        Bundle args = new Bundle();
        Uri currentClientUri =
                ContentUris.withAppendedId(InventoryContract.ClientEntry.CONTENT_URI, id);
        args.putString(Costants.CLIENT_URI, currentClientUri.toString());
        addClientFrag.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, addClientFrag)
                .addToBackStack(null)
                .commit();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {InventoryContract.ClientEntry._ID,
                InventoryContract.ClientEntry.CLIENT_NAME,
                InventoryContract.ClientEntry.CLIENT_CONTACT_PERSON,
                InventoryContract.ClientEntry.CLIENT_PHONE};
        return new CursorLoader(mContext, InventoryContract.ClientEntry.CONTENT_URI,
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

    // DA QUI tutto quello che riguarda l'operazione di cancellazione CLT
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
        builder.setMessage(R.string.AlertDialogForDelete_Question);
        builder.setPositiveButton(R.string.AlertDialogForDelete_Confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteClient();
                getActivity().onBackPressed();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.create();
        builder.show();
    }

    private void deleteClient() {
        if (mCurrentClientUri != null) {
            int rowsDeleted = getActivity().getContentResolver().delete(mCurrentClientUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(getActivity(), R.string.Error_during_delete,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(getActivity(), R.string.Successful_deletingClt,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
