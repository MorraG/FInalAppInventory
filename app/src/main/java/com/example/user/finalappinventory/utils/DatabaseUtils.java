package com.example.user.finalappinventory.utils;

import android.content.Context;
import android.database.Cursor;

import com.example.user.finalappinventory.data.InventoryContract;

import java.util.ArrayList;

public final class DatabaseUtils {

    public static ArrayList<String> getSuppliersNames(Context context, String relationshipType) {
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

    public static ArrayList<String> getClientsNames(Context context, String relationshipType) {
        ArrayList<String> clientList = new ArrayList<>();
        String[] projection = {InventoryContract.ClientEntry.CLIENT_NAME};
        String[] selectionArgs = {relationshipType};
        Cursor cursor = context.getContentResolver().query(InventoryContract.ClientEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
        while (cursor.moveToNext()) {
            int clientNameColumnIndex = cursor.getColumnIndex(InventoryContract.ClientEntry.CLIENT_NAME);
            String clientName = cursor.getString(clientNameColumnIndex);
            clientList.add(clientName);
        }
        cursor.close();
        return clientList;
    }


    public static ArrayList<String> getProductsNames(Context context, String product) {
        ArrayList<String> productList = new ArrayList<>();
        String[] projection = {InventoryContract.ProductEntry.PRODUCT_NAME};
        Cursor cursor = context.getContentResolver().query(InventoryContract.ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
        while (cursor.moveToNext()) {
            int clientNameColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.PRODUCT_NAME);
            String productName = cursor.getString(clientNameColumnIndex);
            productList.add(productName);
        }
        cursor.close();
        return productList;
    }
}
