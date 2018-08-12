package com.example.user.finalappinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {


        public static final String CONTENT_AUTHORITY = "com.example.user.finalappinventory";

        private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

        public static final String PATH_PRODUCTS = "products";

        static final String PATH_SUPPLIERS = "suppliers";

        static final String PATH_CLIENTS = "clients";

        static final String PATH_PURCHASES = "purchases";

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
        public final static String SUPPLIER_NAME = "supplierName";

    }

    public static final class SupplierEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIERS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIERS;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SUPPLIERS);

        //Type: INTEGER
        public final static String _ID = BaseColumns._ID;

        //Type: TEXT
        public final static String TABLE_NAME = "suppliers";

        //Type: TEXT
        public final static String SUPPLIER_NAME = "supplierName";

        //Type: TEXT
        public final static String SUPPLIER_ADDRESS = "supplierAddress";

        //Type: TEXT
        public final static String SUPPLIER_EMAIL = "supplierEmail";

        //Type: TEXT
        public final static String SUPPLIER_PHONE = "supplierPhone";

        //Type: TEXT
        public final static String SUPPLIER_CONTACT_PERSON = "supplierContactPerson";

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

    }

    public static final class PurchaseEntry implements BaseColumns {
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PURCHASES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PURCHASES;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PURCHASES);

        //Type: INTEGER
        public final static String _ID = BaseColumns._ID;

        //Type: TEXT
        public final static String TABLE_NAME = "purchases";

        //Type: TEXT
        public final static String CLIENT_NAME = "clientName";

        //Type: TEXT
        public final static String PRODUCT_NAME = "productName";

        //Type: TEXT
        public final static String PURCHASE_DATE = "purchaseDate";

        //Type: INTEGER
        public final static String QUANTITY_PURCHASED = "quantityPurchased";

        //Type: REAL
        public final static String SALE_PRICE = "purchaseSalePrice";
    }
}
