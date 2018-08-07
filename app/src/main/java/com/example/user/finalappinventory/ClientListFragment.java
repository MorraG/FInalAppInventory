package com.example.user.finalappinventory;

import android.content.ContentUris;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.finalappinventory.adapters.ClientCursorAdapter;
import com.example.user.finalappinventory.data.InventoryContract;
import com.example.user.finalappinventory.utils.Costants;

public class ClientListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, ClientCursorAdapter.ItemClickListener{

    private String mTypeOfRelationship;
    private ClientCursorAdapter mCursorAdapter;

    public ClientListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        Bundle bundle = getArguments();
        mTypeOfRelationship = bundle.getString(Costants.RELATION_TYPE);
        getActivity().setTitle(getString(R.string.all_clients, mTypeOfRelationship));
        //get the list of clients from the database

        mCursorAdapter = new ClientCursorAdapter(getActivity(), null, this);
        ListView listView = rootView.findViewById(R.id.list);
        listView.setAdapter(mCursorAdapter);
        ConstraintLayout empty_screen = rootView.findViewById(R.id.empty_view);
        TextView empty_tv = rootView.findViewById(R.id.empty_text);
        empty_tv.setText(getString(R.string.no_clients_found, mTypeOfRelationship));
        listView.setEmptyView(empty_screen);
        getLoaderManager().initLoader(Costants.ENTERPRISE_LOADER_ID, null, this);
        return rootView;
    }

    @Override
    public void onItemClicked(long id) {
        AddClientFragment addSupplierFrag = new AddClientFragment();
        Bundle args = new Bundle();
        args.putString(Costants.RELATION_TYPE, mTypeOfRelationship);
        Uri currentEnterpriseUri = ContentUris.withAppendedId(InventoryContract.ClientEntry.CONTENT_URI, id);
        args.putString(Costants.ENTERPRISE_URI, currentEnterpriseUri.toString());
        addSupplierFrag.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, addSupplierFrag)
                .addToBackStack(null)
                .commit();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {InventoryContract.ClientEntry._ID, InventoryContract.ClientEntry.CLIENT_NAME, InventoryContract.ClientEntry.CLIENT_CONTACT_PERSON, InventoryContract.ClientEntry.CLIENT_PHONE};
        String[] selectionArgs = {mTypeOfRelationship};
        return new CursorLoader(getActivity(), InventoryContract.ClientEntry.CONTENT_URI,
                projection,
                InventoryContract.ClientEntry.RELATION_TYPE + "=?",
                selectionArgs, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

}