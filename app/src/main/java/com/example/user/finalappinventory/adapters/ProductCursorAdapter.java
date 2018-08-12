package com.example.user.finalappinventory.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.finalappinventory.R;
import com.example.user.finalappinventory.data.InventoryContract;

import java.text.NumberFormat;


public class ProductCursorAdapter extends CursorAdapter {

    final ItemClickListener mCallback;

    public ProductCursorAdapter(@NonNull Context context, Cursor cursor, ItemClickListener listener) {
        super(context, cursor, 0);
        mCallback = listener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_product, parent, false);
        ViewHolder holder = new ViewHolder(v);

        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder = (ViewHolder) view.getTag();
        View container = view.findViewById(R.id.product_item_container);

        final String productName = cursor.getString(cursor.getColumnIndex(InventoryContract.ProductEntry.PRODUCT_NAME));
        int quantity = cursor.getInt(cursor.getColumnIndex(InventoryContract.ProductEntry.QUANTITY_IN_STOCK));
        float price = cursor.getFloat(cursor.getColumnIndex(InventoryContract.ProductEntry.SALE_PRICE));

        holder.productName_tv.setText(productName);
        holder.quantity_tv.setText(mContext.getString(R.string.in_stock, quantity));
        holder.price_tv.setText(NumberFormat.getCurrencyInstance().format(price));

        final int position = cursor.getPosition();
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                long id = mCursor.getLong(mCursor.getColumnIndex(InventoryContract.SupplierEntry._ID));
                mCallback.onItemClicked(id);
            }
        });

    }

    private class ViewHolder{

        final TextView productName_tv;
        final TextView price_tv;
        final TextView quantity_tv;

        ViewHolder(View view){
            this.productName_tv = view.findViewById(R.id.product_item_product_name);
            this.price_tv = view.findViewById(R.id.product_item_price);
            this.quantity_tv = view.findViewById(R.id.product_item_quantity);
        }
    }
    public interface ItemClickListener{
        void onItemClicked(long id);
    }
}
