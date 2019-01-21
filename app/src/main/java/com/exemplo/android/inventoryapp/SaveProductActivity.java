package com.exemplo.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exemplo.android.inventoryapp.data.StoreContract.StoreEntry;

public class SaveProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    TextView activityTitle;
    TextView quantityView;
    EditText nameView;
    EditText priceView;
    EditText supplierNameView;
    EditText supplierPhoneView;
    Button saveButton;
    ImageView addButton;
    ImageView removeButton;
    Integer quantity;
    Uri productUri;
    Long id;
    private boolean productHasChanged = false;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_product);
        id = getIntent().getLongExtra("id", 0);
        productUri = getIntent().getData();
        activityTitle = findViewById(R.id.activityTitleTextId);
        nameView = findViewById(R.id.nameEditId);
        priceView = findViewById(R.id.priceEditId);
        quantityView = findViewById(R.id.quantityTextId);
        supplierNameView = findViewById(R.id.supplierEditId);
        supplierPhoneView = findViewById(R.id.supplierPhoneEditId);
        saveButton = findViewById(R.id.saveButtonId);
        addButton = findViewById(R.id.addButtonId);
        removeButton = findViewById(R.id.removeButtonId);

        nameView.setOnTouchListener(touchListener);
        priceView.setOnTouchListener(touchListener);
        quantityView.setOnTouchListener(touchListener);
        supplierNameView.setOnTouchListener(touchListener);
        supplierPhoneView.setOnTouchListener(touchListener);
        addButton.setOnTouchListener(touchListener);
        removeButton.setOnTouchListener(touchListener);

        if (productUri == null) {
            activityTitle.setText(getString(R.string.add_product));
            quantity = 1;
            quantityView.setText(Integer.toString(quantity));
        } else {
            getLoaderManager().initLoader(2, null, this);
            activityTitle.setText(getString(R.string.edit_product));
        }
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
                if (quantity > 0) {
                    quantity--;
                    quantityView.setText(quantity.toString());
                }
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameView.getText().toString();
                String price = priceView.getText().toString();
                String quantity = quantityView.getText().toString();
                String supplierName = supplierNameView.getText().toString();
                String supplierPhone = supplierPhoneView.getText().toString();

                if (name.isEmpty() || price.isEmpty() || quantity.isEmpty() || supplierName.isEmpty()
                        || supplierPhone.isEmpty()) {
                    Toast.makeText(SaveProductActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                ContentValues contentValues = new ContentValues();
                contentValues.put("name", name);
                contentValues.put("price", price);
                contentValues.put("quantity", quantity);
                contentValues.put("supplier", supplierName);
                contentValues.put("supplierphonenumber", supplierPhone);
                if (productUri == null) {
                    saveProduct(contentValues);
                } else {
                    editProduct(contentValues);
                }
                Intent intent = new Intent(SaveProductActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void editProduct(ContentValues contentValues) {
        int rowsAffected = getContentResolver().update(productUri, contentValues, null, null);
        if (rowsAffected == 0) {
            Toast.makeText(this, getString(R.string.error_updating),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.product_updated),
                    Toast.LENGTH_SHORT).show();

        }
    }

    private void saveProduct(ContentValues contentValues) {
        Uri uriReturned = getContentResolver().insert(StoreEntry.CONTENT_URI, contentValues);
        if (uriReturned != null) {
            long id = ContentUris.parseId(uriReturned);
            Toast.makeText(SaveProductActivity.this, getString(R.string.product_saved) + id, Toast.LENGTH_SHORT).show();
            Log.i("uriReturned", uriReturned.toString());
        } else {
            Toast.makeText(SaveProductActivity.this, getString(R.string.error_saving), Toast.LENGTH_SHORT).show();
        }
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
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(StoreEntry.PRODUCT_NAME));
            Float price = cursor.getFloat(cursor.getColumnIndex(StoreEntry.PRODUCT_PRICE));
            quantity = cursor.getInt(cursor.getColumnIndex(StoreEntry.PRODUCT_QUANTITY));
            String supplier = cursor.getString(cursor.getColumnIndex(StoreEntry.PRODUCT_SUPPLIER_NAME));
            Integer supplierPhone = cursor.getInt(cursor.getColumnIndex(StoreEntry.PRODUCT_SUPPLIER_PHONE_NUMBER));

            nameView.setText(name);
            priceView.setText(price.toString());
            quantityView.setText(quantity.toString());
            supplierNameView.setText(supplier);
            supplierPhoneView.setText(supplierPhone.toString());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onBackPressed() {
        if(productHasChanged) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(SaveProductActivity.this);
            dialog.setTitle("Os dados não foram salvos. Tem certeza que deseja sair?");
            dialog.setPositiveButton("Sair", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(SaveProductActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            dialog.show();
        } else {
            super.onBackPressed();
        }
    }
}