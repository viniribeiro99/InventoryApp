package com.exemplo.android.inventoryapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.exemplo.android.inventoryapp.data.StoreContract;
import com.exemplo.android.inventoryapp.data.StoreContract.StoreEntry;
import com.exemplo.android.inventoryapp.data.StoreDbHelper;

public class StoreProvider extends ContentProvider {
    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int ALL_PRODUCTS = 100;
    private static final int PRODUCT_SELECTED = 101;

    static {
        matcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.PATH_INVENTORY, ALL_PRODUCTS);
        matcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.PATH_INVENTORY + "/#", PRODUCT_SELECTED);
    }

    private StoreDbHelper helper;

    @Override
    public boolean onCreate() {
        helper = new StoreDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = helper.getReadableDatabase();
        int matchCode = matcher.match(uri);
        Cursor cursor;

        switch (matchCode) {
            case ALL_PRODUCTS:
                cursor = database.query(StoreEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCT_SELECTED:
                selection = StoreEntry.PRODUCT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(StoreEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = matcher.match(uri);
        switch (match) {
            case ALL_PRODUCTS:
                return StoreEntry.CONTENT_LIST_TYPE;
            case PRODUCT_SELECTED:
                return StoreEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        String name = contentValues.getAsString(StoreEntry.PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("O produto dever ter um nome");
        }
        Integer quantity = contentValues.getAsInteger(StoreEntry.PRODUCT_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("A quantidade de unidades do produto deve ser inserida");
        }
        SQLiteDatabase database = helper.getWritableDatabase();
        long productId = database.insert(StoreEntry.TABLE_NAME, null, contentValues);
        if (productId == -1) {
            Toast.makeText(getContext(), "Falha ao inserir produto em " + uri, Toast.LENGTH_SHORT).show();
        }
        return ContentUris.withAppendedId(uri, productId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase database = helper.getWritableDatabase();
        int rowsDeleted;
        final int match = matcher.match(uri);
        switch (match) {
            case ALL_PRODUCTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(StoreEntry.TABLE_NAME, s, strings);
                break;
            case PRODUCT_SELECTED:
                // Delete a single row given by the ID in the URI
                s = StoreEntry._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(StoreEntry.TABLE_NAME, s, strings);
                break;
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
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        final int match = matcher.match(uri);
        switch (match) {
            case ALL_PRODUCTS:
                return updateProduct(uri, contentValues, s, strings);
            case PRODUCT_SELECTED:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                s = StoreEntry._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, s, strings);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.containsKey(StoreEntry.PRODUCT_NAME)) {
            String name = contentValues.getAsString(StoreEntry.PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }
        if (contentValues.containsKey(StoreEntry.PRODUCT_QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(StoreEntry.PRODUCT_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Product requires quantity");
            }
        }
        if (contentValues.containsKey(StoreEntry.PRODUCT_PRICE)) {
            Float price = contentValues.getAsFloat(StoreEntry.PRODUCT_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Product requires a price");
            }
        }
        if (contentValues.containsKey(StoreEntry.PRODUCT_SUPPLIER_NAME)) {
            String supplier = contentValues.getAsString(StoreEntry.PRODUCT_SUPPLIER_NAME);
            if (supplier == null) {
                throw new IllegalArgumentException("Product requires supplier");
            }
        }
        if (contentValues.containsKey(StoreEntry.PRODUCT_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhone = contentValues.getAsString(StoreEntry.PRODUCT_SUPPLIER_PHONE_NUMBER);
            if (supplierPhone == null) {
                throw new IllegalArgumentException("Product requires supplier phone");
            }
        }
        if (contentValues.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = helper.getWritableDatabase();
        int rowsUpdated = database.update(StoreEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}