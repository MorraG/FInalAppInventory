package com.example.user.finalappinventory.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.user.finalappinventory.R;
import com.example.user.finalappinventory.data.InventoryContract;

public class PurchaseCursorAdapter extends CursorAdapter {

    final PurchaseCursorAdapter.ItemClickListener mCallback;

    public PurchaseCursorAdapter(@NonNull Context context, Cursor cursor, PurchaseCursorAdapter.ItemClickListener listener) {
        super(context, cursor, 0);
        mCallback = listener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_purchase, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView clientName_tv = view.findViewById(R.id.list_item_client_name);
        TextView productPurchased_tv = view.findViewById(R.id.list_item_product_purchased);
        TextView datePurchase_tv = view.findViewById(R.id.list_item_date_purchase);
        TextView quantityPurchased_tv = view.findViewById(R.id.list_item_quantity_purchased);
        ImageButton phone_btn = view.findViewById(R.id.client_item_phone_button);
        View container = view.findViewById(R.id.purchase_item_container);

        int clientNameColumnIndex = cursor.getColumnIndex(InventoryContract.PurchaseEntry.CLIENT_NAME);
        int productPurchasedColumnIndex = cursor.getColumnIndex(InventoryContract.PurchaseEntry.PRODUCT_NAME);
        int datePurchaseColumnIndex = cursor.getColumnIndex(InventoryContract.PurchaseEntry.PURCHASE_DATE);
        int quantityPurchasedColumnIndex = cursor.getColumnIndex(InventoryContract.PurchaseEntry.QUANTITY_PURCHASED);
        int phoneColumnIndex = cursor.getColumnIndex(InventoryContract.ClientEntry.CLIENT_PHONE);

        String clientName = cursor.getString(clientNameColumnIndex);
        String productPurchased = cursor.getString(productPurchasedColumnIndex);
        String datePurchase = cursor.getString(datePurchaseColumnIndex);
        String quantityPurchased = cursor.getString(quantityPurchasedColumnIndex);
        final String phone = cursor.getString(phoneColumnIndex);

        clientName_tv.setText(clientName);
        productPurchased_tv.setText(productPurchased);
        datePurchase_tv.setText(datePurchase);
        quantityPurchased_tv.setText(quantityPurchased);
        phone_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                mContext.startActivity(intent);
            }
        });

        final int position = cursor.getPosition();
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                long id = mCursor.getLong(mCursor.getColumnIndex(InventoryContract.PurchaseEntry._ID));
                mCallback.onItemClicked(id);
            }
        });
    }

    public interface ItemClickListener {
        void onItemClicked(long id);
    }
}
