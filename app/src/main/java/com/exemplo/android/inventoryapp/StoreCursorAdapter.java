package com.exemplo.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.exemplo.android.inventoryapp.data.StoreContract.StoreEntry;

public class StoreCursorAdapter extends CursorAdapter {
    public StoreCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    Integer quantity;

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.product_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView productNameView = view.findViewById(R.id.productNameId);
        TextView priceView = view.findViewById(R.id.priceId);
        final TextView quantityView = view.findViewById(R.id.quantityId);
        Button sellButton = view.findViewById(R.id.sellButton);

        String name = cursor.getString(cursor.getColumnIndex(StoreEntry.PRODUCT_NAME));
        Float price = cursor.getFloat(cursor.getColumnIndex(StoreEntry.PRODUCT_PRICE));
        quantity = cursor.getInt(cursor.getColumnIndex(StoreEntry.PRODUCT_QUANTITY));
        long id = cursor.getLong(cursor.getColumnIndex(StoreEntry.PRODUCT_ID));
        final Uri productUri = ContentUris.withAppendedId(StoreEntry.CONTENT_URI, id);

        productNameView.setText(name);
        priceView.setText("R$ " + price.toString());
        quantityView.setText(quantity.toString());

        //TODO: fix bug
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity > 0) {
                    quantity--;
                    quantityView.setText(quantity.toString());
                    ContentValues values = new ContentValues();
                    values.put(StoreEntry.PRODUCT_QUANTITY, quantity);
                    context.getContentResolver().update(productUri, values, null, null);
                }
            }
        });
    }
}