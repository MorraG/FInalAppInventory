package com.example.user.finalappinventory;

import android.content.ContentUris;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.finalappinventory.R;
import com.example.user.finalappinventory.adapters.ProductCursorAdapter;
import com.example.user.finalappinventory.data.InventoryContract;
import com.example.user.finalappinventory.utils.Costants;

public class ProductListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private ProductCursorAdapter mCursorAdapter;
    private Context mContext;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public ProductListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        getActivity().setTitle(getString(R.string.all_products));

        mCursorAdapter = new ProductCursorAdapter(getActivity(), null);
        // FIND VIEWS
        ListView listView = rootView.findViewById(R.id.list);
        ConstraintLayout empty_screen = rootView.findViewById(R.id.empty_view);
        TextView empty_tv = rootView.findViewById(R.id.empty_text);
        Button delete_product_btn = rootView.findViewById(R.id.delete_btn);

        //Set adapter , text , view, listener
        listView.setAdapter(mCursorAdapter);
        empty_tv.setText(R.string.no_products_found);
        listView.setEmptyView(empty_screen);
        //delete_product_btn.setOnClickListener();


        getLoaderManager().initLoader(Costants.PRODUCT_LOADER_ID, null, this);
        return rootView;
    }
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                InventoryContract.ProductEntry._ID, InventoryContract.ProductEntry.PRODUCT_NAME, InventoryContract.ProductEntry.QUANTITY_IN_STOCK, InventoryContract.ProductEntry.SALE_PRICE};
        return new CursorLoader(mContext, InventoryContract.ProductEntry.CONTENT_URI, projection, null, null, null);
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
}
