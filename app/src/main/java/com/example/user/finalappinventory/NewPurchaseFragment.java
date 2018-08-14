package com.example.user.finalappinventory;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.finalappinventory.data.InventoryContract;
import com.example.user.finalappinventory.utils.Costants;
import com.example.user.finalappinventory.utils.DatabaseUtils;

import java.util.ArrayList;

public class NewPurchaseFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    //elem needed declaration
    private Spinner mProductSpin;
    private Spinner mClientSpin;
    private EditText quantity_et;
    private EditText date_et;
    private EditText price_et;
    private ArrayList<String> clientNames;
    private ArrayList<String> productNames;
    private String chosenClientName = null;
    private String chosenProductName = null;
    private ArrayAdapter<String> mSpinAdapterClt;
    private ArrayAdapter<String> mSpinAdapterPrt;

    private Uri mCurrentPurchaseUri;

    private Button mButtonDash;
    private Button mButtonPlus;
    private Button buy_btn;

    private int quantityAddProduct;

    public NewPurchaseFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_purchase, container, false);
        setHasOptionsMenu(true);

        //Find views
        mClientSpin = rootView.findViewById(R.id.clientSpinner);
        mProductSpin = rootView.findViewById(R.id.productSpinner);
        quantity_et = rootView.findViewById(R.id.editQuantity);
        date_et = rootView.findViewById(R.id.editDate);
        price_et = rootView.findViewById(R.id.editPrice);

        mButtonDash = rootView.findViewById(R.id.meno);
        mButtonPlus = rootView.findViewById(R.id.piu);
        buy_btn = rootView.findViewById(R.id.buy_btn);

        //Set click listeners on buttons
        buy_btn.setOnClickListener(this);

        //Set the spinner which shows existing client names
        mClientSpin.setOnItemSelectedListener(this);
        clientNames = DatabaseUtils.getClientsNames(getActivity(), Costants.CLIENT);
        mSpinAdapterClt = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, clientNames);
        mSpinAdapterClt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mClientSpin.setAdapter(mSpinAdapterClt);

        //Set the spinner which shows existing product names
        mProductSpin.setOnItemSelectedListener(this);
        productNames = DatabaseUtils.getProductsNames(getActivity(), Costants.PRODUCT);
        mSpinAdapterPrt = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, clientNames);
        mSpinAdapterPrt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mProductSpin.setAdapter(mSpinAdapterPrt);

        Bundle bundle = getArguments();
        String uriString = null;

        if (bundle != null) uriString = bundle.getString(Costants.PURCHASE_URI);
        if (uriString != null) mCurrentPurchaseUri = Uri.parse(uriString);


        //controllo quantità inserita con TextWatcher and Editable class.
        quantity_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                quantityAddProduct = 0;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    quantityAddProduct = 0;
                } else {
                    quantityAddProduct = Integer.parseInt(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    quantityAddProduct = 0;
                } else {
                    quantityAddProduct = Integer.parseInt(quantity_et.getText().toString());
                }

            }
        });


        //Set the title that corresponds to the fragment
        if (mCurrentPurchaseUri == null) {

            getActivity().setTitle(getString(R.string.new_product));
            getActivity().invalidateOptionsMenu();
            mButtonDash.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    if (quantityAddProduct > 0) {

                        quantityAddProduct = quantityAddProduct - 1;
                        quantity_et.setText(String.valueOf(quantityAddProduct));

                    } else {

                        Toast.makeText(getActivity(), getString(R.string.quantity_cannot_be_negative), Toast.LENGTH_SHORT).show();

                    }

                }
            });

            mButtonPlus.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    quantityAddProduct = quantityAddProduct + 1;
                    quantity_et.setText(String.valueOf(quantityAddProduct));

                }
            });
        } else {
            getActivity().setTitle(getString(R.string.edit_purchase));
            getLoaderManager().initLoader(Costants.PURCHASE_LOADER, null, this);
        }
        //TODO Find shared preferences into Add Product, bisogna capire che fa.

        return rootView;

    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentPurchaseUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_with_delete, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            openAlertDialogForDelete();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAlertDialogForDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setMessage("Do you want to delete this item from the database?");
        builder.setPositiveButton("Yes, delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
                getActivity().onBackPressed();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.create();
        builder.show();
    }

    private void deleteProduct() {
        if (mCurrentPurchaseUri != null) {
            int rowsDeleted = getActivity().getContentResolver().delete(mCurrentPurchaseUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(getActivity(), R.string.Error_during_delete,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(getActivity(), R.string.Successful_deletingProd,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void onClick(View v) {

        if (savePurchase()) {
            PurchaseListFragment purchaseFrag = new PurchaseListFragment();
            Bundle args = new Bundle();
            purchaseFrag.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, purchaseFrag)
                    .addToBackStack(null)
                    .commit();
        }
    }


    private boolean savePurchase() {

        //Make sure quantity is a positive integer

        int quantityPurchased;

        try {

            quantityPurchased = Integer.valueOf(quantity_et.getText().toString().trim());
            if (quantityPurchased < 1) {
                Toast.makeText(getActivity(), R.string.quantity_should_be_positive, Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException nfe) {
            Toast.makeText(getActivity(), R.string.quantity_should_be_positive, Toast.LENGTH_SHORT).show();
            return false;
        }


        /// TODO TEMPORARY VALIDATOR FOR DATE AND PRICE - FOR DATE I WOULD LIKE INSERT DATA PICKER, FOR PRICE It would fill in automatic when the product is chosen
        String purchaseDate = date_et.getText().toString().trim();
        if (TextUtils.isEmpty(purchaseDate)) {
            Toast.makeText(getActivity(), R.string.purchase_date_empty, Toast.LENGTH_SHORT).show();
            return false;
        }
        String purchasePirce = price_et.getText().toString().trim();
        if (TextUtils.isEmpty(purchasePirce)) {
            Toast.makeText(getActivity(), R.string.purchase_price_empty, Toast.LENGTH_SHORT).show();
            return false;
        }
        /////////

        ContentValues values = new ContentValues();
        values.put(InventoryContract.PurchaseEntry.CLIENT_NAME, chosenClientName);
        values.put(InventoryContract.PurchaseEntry.PRODUCT_NAME, chosenProductName);
        values.put(InventoryContract.PurchaseEntry.QUANTITY_PURCHASED, quantityPurchased);
        values.put(InventoryContract.PurchaseEntry.PURCHASE_DATE, purchaseDate);
        values.put(InventoryContract.PurchaseEntry.SALE_PRICE, purchasePirce);

        if (mCurrentPurchaseUri == null) {
            //This is a new purchase entry
            Uri newUri = getActivity().getContentResolver().insert(InventoryContract.PurchaseEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(getActivity(), R.string.error_saving, Toast.LENGTH_SHORT).show();
                return false;
            } else {
                Toast.makeText(getActivity(), R.string.enterprise_successfully_saved, Toast.LENGTH_SHORT).show();
                return true;
            }
        } else {
            // Otherwise this is an existing purchase, so update the entry
            int rowsAffected = getActivity().getContentResolver().update(mCurrentPurchaseUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(getActivity(), R.string.error_updating, Toast.LENGTH_SHORT).show();
                return false;
            } else {
                Toast.makeText(getActivity(), R.string.successfully_updated, Toast.LENGTH_SHORT).show();
                return true;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenClientName = clientNames.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getActivity(), R.string.no_client_chosen, Toast.LENGTH_SHORT).show();
    }

    //TODO insert The OnItemSelected for Products
    /*@Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenProductName = productNames.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getActivity(), R.string.no_product_chosen, Toast.LENGTH_SHORT).show();
    }*/

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
       String[] projection = {
                InventoryContract.PurchaseEntry._ID,
                InventoryContract.PurchaseEntry.CLIENT_NAME,
                InventoryContract.PurchaseEntry.PRODUCT_NAME,
                InventoryContract.PurchaseEntry.QUANTITY_PURCHASED,
                InventoryContract.PurchaseEntry.SALE_PRICE,
                InventoryContract.PurchaseEntry.PURCHASE_DATE};
        return new CursorLoader(getActivity(), mCurrentPurchaseUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int productNameColumnIndex = cursor.getColumnIndex(InventoryContract.PurchaseEntry.PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.PurchaseEntry.QUANTITY_PURCHASED);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.PurchaseEntry.SALE_PRICE);
            int clientColumnIndex = cursor.getColumnIndex(InventoryContract.PurchaseEntry.CLIENT_NAME);
            int dateColumnIndex = cursor.getColumnIndex(InventoryContract.PurchaseEntry.PURCHASE_DATE);

            String productName = cursor.getString(productNameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String clientName = cursor.getString(clientColumnIndex);
            // Temporary TODO
            float price = cursor.getFloat(priceColumnIndex);
            String date = cursor.getString(dateColumnIndex);

            price_et.setText(String.valueOf(price));
            quantity_et.setText(String.valueOf(quantity));
            mClientSpin.setSelection(mSpinAdapterClt.getPosition(clientName));
            mProductSpin.setSelection(mSpinAdapterPrt.getPosition(productName));
            date_et.setText(String.valueOf(date));

            quantityAddProduct = cursor.getInt(quantityColumnIndex);

            mButtonDash.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    if (quantityAddProduct > 0) {

                        quantityAddProduct = quantityAddProduct - 1;
                        quantity_et.setText(String.valueOf(quantityAddProduct));

                    } else {

                        Toast.makeText(getActivity(), getString(R.string.quantity_cannot_be_negative), Toast.LENGTH_SHORT).show();

                    }

                }
            });

            mButtonPlus.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    quantityAddProduct = quantityAddProduct + 1;
                    quantity_et.setText(String.valueOf(quantityAddProduct));

                }
            });
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
