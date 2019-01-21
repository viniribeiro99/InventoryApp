package com.exemplo.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.exemplo.android.inventoryapp.data.StoreContract.StoreEntry;

public class ProductDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    Long id;
    Intent intentToEdit;
    Uri productUri;
    Button callSupplier;
    Integer supplierPhone;
    ImageView addButton, removeButton;
    Integer quantity;
    TextView quantityView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        intentToEdit = new Intent(ProductDetailsActivity.this, SaveProductActivity.class);
        id = getIntent().getLongExtra("id", 0);
        productUri = getIntent().getData();
        addButton = findViewById(R.id.addButtonId);
        removeButton = findViewById(R.id.removeButtonId);
        getLoaderManager().initLoader(2, null, this);
        callSupplier = findViewById((R.id.callSupplierButton));
        quantityView = findViewById(R.id.quantityViewId);
        callSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri telUri = Uri.parse("tel:" + (supplierPhone.toString()));
                Intent intent = new Intent(Intent.ACTION_DIAL, telUri);
                startActivity(intent);
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity++;
                quantityView.setText(quantity.toString());

            }
        });
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity > 1) {
                    quantity--;
                    quantityView.setText(quantity.toString());
                }
            }
        });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = {
                StoreEntry.PRODUCT_NAME,
                StoreEntry.PRODUCT_PRICE,
                StoreEntry.PRODUCT_QUANTITY,
                StoreEntry.PRODUCT_ID,
                StoreEntry.PRODUCT_SUPPLIER_NAME,
                StoreEntry.PRODUCT_SUPPLIER_PHONE_NUMBER
        };
        String selection = StoreEntry.PRODUCT_ID + "=?";
        String[] selectionArgs = {id.toString()};

        return new CursorLoader(this,
                StoreEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        TextView productNameView = findViewById(R.id.nameViewId);
        TextView priceView = findViewById(R.id.priceViewId);
        TextView quantityView = findViewById(R.id.quantityViewId);
        TextView supplierView = findViewById(R.id.supplierViewId);
        TextView supplierPhoneView = findViewById(R.id.supplierPhoneViewId);

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(StoreEntry.PRODUCT_NAME));
            Float price = cursor.getFloat(cursor.getColumnIndex(StoreEntry.PRODUCT_PRICE));
            quantity = cursor.getInt(cursor.getColumnIndex(StoreEntry.PRODUCT_QUANTITY));
            String supplier = cursor.getString(cursor.getColumnIndex(StoreEntry.PRODUCT_SUPPLIER_NAME));
            supplierPhone = cursor.getInt(cursor.getColumnIndex(StoreEntry.PRODUCT_SUPPLIER_PHONE_NUMBER));

            productNameView.setText(name);
            priceView.setText(getString(R.string.product_price) + price.toString());
            quantityView.setText(quantity.toString());
            supplierView.setText(getString(R.string.product_supplier) + supplier);
            supplierPhoneView.setText(getString(R.string.supplier_phone) + supplierPhone.toString());

            intentToEdit.putExtra("id", id);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editId:
                intentToEdit.setData(productUri);
                startActivity(intentToEdit);
                break;
            case R.id.homeAsUp:
                NavUtils.navigateUpFromSameTask(ProductDetailsActivity.this);
                break;
            case R.id.deleteId:
                final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Tem certeza que deseja deletar o produto do banco de dados?");
                dialog.setPositiveButton("Deletar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getContentResolver().delete(productUri, null, null);
                        Intent intent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ContentValues values = new ContentValues();
        values.put(StoreEntry.PRODUCT_QUANTITY, quantity);
        getContentResolver().update(productUri, values, null, null);
    }
}