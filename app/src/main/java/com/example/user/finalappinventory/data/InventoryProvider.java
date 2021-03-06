package com.example.user.finalappinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.user.finalappinventory.data.InventoryContract.ProductEntry;
import com.example.user.finalappinventory.data.InventoryContract.SupplierEntry;
import com.example.user.finalappinventory.data.InventoryContract.ClientEntry;
import com.example.user.finalappinventory.data.InventoryContract.PurchaseEntry;

public class InventoryProvider extends ContentProvider {

    private static final int PRODUCTS = 100;
    private static final int PRODUCT_WITH_ID = 101;

    private static final int SUPPLIERS = 200;
    private static final int SUPPLIERS_ID = 201;

    private static final int CLIENTS = 300;
    private static final int CLIENTS_ID = 301;

    private static final int PURCHASES = 400;
    private static final int PURCHASES_WITH_ID = 401;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCTS + "/#", PRODUCT_WITH_ID);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_SUPPLIERS, SUPPLIERS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_SUPPLIERS + "/#", SUPPLIERS_ID);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_CLIENTS, CLIENTS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_CLIENTS + "/#", CLIENTS_ID);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PURCHASES, PURCHASES);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PURCHASES + "/#", PURCHASES_WITH_ID);
    }

    private InventoryDBHelper mDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDbHelper = new InventoryDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS: {
                cursor = database.query(
                        ProductEntry.TABLE_NAME,   // The table to query
                        projection,            // The columns to return
                        null, // The columns for the WHERE clause
                        null,                  // The values for the WHERE clause
                        null,                  // Don't group the rows
                        null,                  // Don't filter by row groups
                        null);                   // The sort order
                break;
            }
            case PRODUCT_WITH_ID: {
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);
                break;
            }
            case SUPPLIERS: {
                cursor = database.query(
                        SupplierEntry.TABLE_NAME,   // The table to query
                        projection,            // The columns to return
                        null, // The columns for the WHERE clause
                        null,                  // The values for the WHERE clause
                        null,                  // Don't group the rows
                        null,                  // Don't filter by row groups
                        null);                   // The sort order
                break;
            }
            case SUPPLIERS_ID: {
                selection = SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(SupplierEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);
                break;
            }
            case CLIENTS: {
                cursor = database.query(
                        ClientEntry.TABLE_NAME,   // The table to query
                        projection,            // The columns to return
                        null, // The columns for the WHERE clause
                        null,                  // The values for the WHERE clause
                        null,                  // Don't group the rows
                        null,                  // Don't filter by row groups
                        null);                   // The sort order
                break;
            }
            case CLIENTS_ID: {
                selection = ClientEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(ClientEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);
                break;
            }
            case PURCHASES: {
                cursor = database.query(
                        PurchaseEntry.TABLE_NAME,   // The table to query
                        projection,            // The columns to return
                        null, // The columns for the WHERE clause
                        null,                  // The values for the WHERE clause
                        null,                  // Don't group the rows
                        null,                  // Don't filter by row groups
                        null);                   // The sort order
                break;
            }
            case PURCHASES_WITH_ID: {
                selection = PurchaseEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(PurchaseEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);
                break;
            }
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        if (cursor != null) cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_WITH_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            case SUPPLIERS:
                return SupplierEntry.CONTENT_LIST_TYPE;
            case SUPPLIERS_ID:
                return SupplierEntry.CONTENT_ITEM_TYPE;
            case CLIENTS:
                return ClientEntry.CONTENT_LIST_TYPE;
            case CLIENTS_ID:
                return ClientEntry.CONTENT_ITEM_TYPE;
            case PURCHASES:
                return PurchaseEntry.CONTENT_LIST_TYPE;
            case PURCHASES_WITH_ID:
                return PurchaseEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS: {
                id = database.insert(ProductEntry.TABLE_NAME, null, values);
                break;
            }
            case SUPPLIERS: {
                id = database.insert(SupplierEntry.TABLE_NAME, null, values);
                break;
            }
            case CLIENTS: {
                id = database.insert(ClientEntry.TABLE_NAME, null, values);
                break;
            }
            case PURCHASES: {
                id = database.insert(PurchaseEntry.TABLE_NAME, null, values);
                break;
            }
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
        if (id == -1) {
            Log.d(LOG_TAG, "Failed to insert row for " + uri);
        }
        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT_WITH_ID: {
                // Delete a single row given by the ID in the URI
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case SUPPLIERS_ID: {
                // Delete a single row given by the ID in the URI
                selection = SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(SupplierEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case CLIENTS_ID: {
                // Delete a single row given by the ID in the URI
                selection = ClientEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ClientEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case PURCHASES_WITH_ID: {
                // Delete a single row given by the ID in the URI
                selection = PurchaseEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(PurchaseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case PRODUCTS: {
                rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case PRODUCT_WITH_ID: {
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case SUPPLIERS: {
                rowsUpdated = database.update(SupplierEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case SUPPLIERS_ID: {
                selection = SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = database.update(SupplierEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case CLIENTS: {
                rowsUpdated = database.update(ClientEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case CLIENTS_ID: {
                selection = ClientEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = database.update(ClientEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case PURCHASES: {
                rowsUpdated = database.update(PurchaseEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case PURCHASES_WITH_ID: {
                selection = PurchaseEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = database.update(PurchaseEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}