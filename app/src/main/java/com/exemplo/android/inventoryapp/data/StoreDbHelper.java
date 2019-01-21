package com.exemplo.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.exemplo.android.inventoryapp.data.StoreContract.StoreEntry;

public class StoreDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "inventory.db";
    public static final int DATABASE_VERSION = 4;
    public static final String CREATE_STORE_TABLE = "CREATE TABLE " + StoreEntry.TABLE_NAME + " ("
            + StoreEntry.PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + StoreEntry.PRODUCT_NAME + " TEXT NOT NULL, "
            + StoreEntry.PRODUCT_PRICE + " INTEGER NOT NULL DEFAULT 0, "
            + StoreEntry.PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
            + StoreEntry.PRODUCT_SUPPLIER_NAME + " TEXT, "
            + StoreEntry.PRODUCT_SUPPLIER_PHONE_NUMBER + " INTEGER NOT NULL);";

    public StoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_STORE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}