package com.example.user.finalappinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InventoryDBHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    private static final String DATABASE_NAME = "inventory.db";

    private static final int DATABASE_VERSION = 1;

    public InventoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + InventoryContract.ProductEntry.TABLE_NAME + " ("
                + InventoryContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryContract.ProductEntry.PRODUCT_NAME + " TEXT NOT NULL, "
                + InventoryContract.ProductEntry.SALE_PRICE + " REAL DEFAULT 0, "
                + InventoryContract.ProductEntry.QUANTITY_IN_STOCK + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryContract.ProductEntry.SUPPLIER_NAME + " TEXT);";

        String SQL_CREATE_SUPPLIERS_TABLE = "CREATE TABLE " + InventoryContract.SupplierEntry.TABLE_NAME + " ("
                + InventoryContract.SupplierEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryContract.SupplierEntry.SUPPLIER_NAME + " TEXT NOT NULL, "
                + InventoryContract.SupplierEntry.SUPPLIER_PHONE + " TEXT, "
                + InventoryContract.SupplierEntry.SUPPLIER_ADDRESS + " TEXT, "
                + InventoryContract.SupplierEntry.SUPPLIER_EMAIL + " TEXT, "
                + InventoryContract.SupplierEntry.SUPPLIER_CONTACT_PERSON + " TEXT)";

        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
        db.execSQL(SQL_CREATE_SUPPLIERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
