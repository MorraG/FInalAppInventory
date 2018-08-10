package com.example.user.finalappinventory.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.user.finalappinventory.data.InventoryContract;
import com.example.user.finalappinventory.data.InventoryDBHelper;

import java.util.ArrayList;

public final class DatabaseUtils {

    public static ArrayList<String> getSuppliersNames(Context context, String relationshipType){
        ArrayList<String> supplierList = new ArrayList<>();
        String[] projection = {InventoryContract.SupplierEntry.SUPPLIER_NAME};
        String[] selectionArgs = {relationshipType};
        Cursor cursor = context.getContentResolver().query(InventoryContract.SupplierEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
        while (cursor.moveToNext()) {
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryContract.SupplierEntry.SUPPLIER_NAME);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            supplierList.add(supplierName);
        }
        cursor.close();
        return supplierList;
    }

    public static ArrayList<String> getClientsNames(Context context, String relationshipType){
        ArrayList<String> clientList = new ArrayList<>();
        String[] projection = {InventoryContract.ClientEntry.CLIENT_NAME};
        String[] selectionArgs = {relationshipType};
        Cursor cursor = context.getContentResolver().query(InventoryContract.ClientEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
        while (cursor.moveToNext()) {
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryContract.ClientEntry.CLIENT_NAME);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            clientList.add(supplierName);
        }
        cursor.close();
        return clientList;
    }

    public static Cursor mergeTables(Context context, long id){
        InventoryDBHelper dbHelper = new InventoryDBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] selectionArgs = {String.valueOf(id)};
        return db.rawQuery("SELECT products.productName, products.clientName, clients.clientPhone FROM products INNER JOIN clients ON (products.supplierName = clients.clientName AND products._ID=?)", selectionArgs);
    }
}
