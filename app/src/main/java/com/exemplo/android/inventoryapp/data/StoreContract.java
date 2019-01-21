package com.exemplo.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class StoreContract {
    private StoreContract() {
    }
    public final static String CONTENT_AUTHORITY = "com.exemplo.android.inventoryapp";
    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public final static String PATH_INVENTORY = "products";

    public static final class StoreEntry implements BaseColumns{
        public final static Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, StoreContract.PATH_INVENTORY);

        public final static String TABLE_NAME = "products";

        public final static String PRODUCT_ID = BaseColumns._ID;
        public final static String PRODUCT_NAME = "name";
        public final static String PRODUCT_PRICE = "price";
        public final static String PRODUCT_QUANTITY = "quantity";
        public final static String PRODUCT_SUPPLIER_NAME = "supplier";
        public final static String PRODUCT_SUPPLIER_PHONE_NUMBER = "supplierphonenumber";

        public final static String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        public final static String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
    }
}