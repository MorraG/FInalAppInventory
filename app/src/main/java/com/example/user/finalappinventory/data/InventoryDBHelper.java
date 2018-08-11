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

        String SQL_CREATE_CLIENTS_TABLE = "CREATE TABLE " + InventoryContract.ClientEntry.TABLE_NAME + " ("
                + InventoryContract.ClientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryContract.ClientEntry.CLIENT_NAME + " TEXT NOT NULL, "
                + InventoryContract.ClientEntry.CLIENT_PHONE + " TEXT, "
                + InventoryContract.ClientEntry.CLIENT_ADDRESS + " TEXT, "
                + InventoryContract.ClientEntry.CLIENT_EMAIL + " TEXT, "
                + InventoryContract.ClientEntry.CLIENT_CONTACT_PERSON + " TEXT)";

        String SQL_CREATE_PURCHASE_TABLE = "CREATE TABLE " + InventoryContract.ClientEntry.TABLE_NAME + " ("
                + InventoryContract.PurchaseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryContract.PurchaseEntry.CLIENT_NAME + " TEXT, "
                + InventoryContract.PurchaseEntry.SUPPLIER_NAME + " TEXT, "
                + InventoryContract.PurchaseEntry.PURCHASE_DATE + " TEXT, "
                + InventoryContract.PurchaseEntry.QUANTITY_PURCHASED + "  INTEGER NOT NULL DEFAULT 0,) ";

        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
        db.execSQL(SQL_CREATE_SUPPLIERS_TABLE);
        db.execSQL(SQL_CREATE_CLIENTS_TABLE);
        db.execSQL(SQL_CREATE_PURCHASE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
