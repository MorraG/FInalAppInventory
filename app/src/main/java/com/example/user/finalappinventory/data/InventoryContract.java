package com.example.user.finalappinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {


        public static final String CONTENT_AUTHORITY = "com.example.user.finalappinventory.data";

        private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

        public static final String PATH_PRODUCTS = "products";

        static final String PATH_CLIENTS = "clients";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {
    }

    public static final class ProductEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        //Type: TEXT
        public final static String TABLE_NAME = "products";

        //Type: INTEGER
        public final static String _ID = BaseColumns._ID;

        //Type: TEXT
        public final static String PRODUCT_NAME = "productName";

        //Type: REAL
        public final static String SALE_PRICE = "productSalePrice";

        //Type: INTEGER
        public final static String QUANTITY_IN_STOCK = "quantityInStock";

        //Type: TEXT
        public final static String CLIENT_NAME = "clientName";

    }

    public static final class ClientEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLIENTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLIENTS;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CLIENTS);

        //Type: INTEGER
        public final static String _ID = BaseColumns._ID;

        //Type: TEXT
        public final static String TABLE_NAME = "clients";

        //Type: TEXT
        public final static String CLIENT_NAME = "clientName";

        //Type: TEXT
        public final static String CLIENT_ADDRESS = "clientAddress";

        //Type: TEXT
        public final static String CLIENT_EMAIL = "clientEmail";

        //Type: TEXT
        public final static String CLIENT_PHONE = "clientPhone";

        //Type: TEXT
        public final static String CLIENT_CONTACT_PERSON = "clientContactPerson";

        //Type: TEXT
        public final static String RELATION_TYPE = "relationType";
    }
}
