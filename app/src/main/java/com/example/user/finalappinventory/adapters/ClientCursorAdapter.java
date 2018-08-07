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

public class ClientCursorAdapter extends CursorAdapter {

    final ItemClickListener mCallback;

    public ClientCursorAdapter(@NonNull Context context, Cursor cursor, ItemClickListener listener) {
        super(context, cursor, 0);
        mCallback = listener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_client, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView supplierName_tv = view.findViewById(R.id.list_item_client_name);
        TextView contact_person_tv = view.findViewById(R.id.list_item_client_contact_person);
        ImageButton phone_btn = view.findViewById(R.id.client_item_phone_button);
        View container = view.findViewById(R.id.client_item_container);

        int clientNameColumnIndex = cursor.getColumnIndex(InventoryContract.ClientEntry.CLIENT_NAME);
        int contactPersonColumnIndex = cursor.getColumnIndex(InventoryContract.ClientEntry.CLIENT_CONTACT_PERSON);
        int phoneColumnIndex = cursor.getColumnIndex(InventoryContract.ClientEntry.CLIENT_PHONE);

        String clientName = cursor.getString(clientNameColumnIndex);
        String contactPerson = cursor.getString(contactPersonColumnIndex);
        final String phone = cursor.getString(phoneColumnIndex);

        supplierName_tv.setText(clientName);
        contact_person_tv.setText(contactPerson);
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
                long id = mCursor.getLong(mCursor.getColumnIndex(InventoryContract.ClientEntry._ID));
                mCallback.onItemClicked(id);
            }
        });
    }

    public interface ItemClickListener{
        void onItemClicked(long id);
    }
}

