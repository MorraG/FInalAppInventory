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

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;


public class AddProductFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private EditText productName_et;
    private EditText salePrice_et;
    private EditText quantity_et;
    private ArrayList<String> supplierNames;
    private String chosenSupplierName = null;
    private Uri mCurrentProductUri;
    private Spinner mSupplierSpin;
    private ArrayAdapter<String> mSpinAdapter;

    private Button mButtonDash;
    private Button mButtonPlus;

    private int quantityAddProduct;

    public AddProductFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_add_product, container, false);
        setHasOptionsMenu(true);
        //Find views
        productName_et = rootView.findViewById(R.id.editProductName);
        salePrice_et = rootView.findViewById(R.id.editSalePrice);
        quantity_et = rootView.findViewById(R.id.editQuantity);
        mSupplierSpin = rootView.findViewById(R.id.supplierSpinner);
        Button save_product_btn = rootView.findViewById(R.id.save_btn);
        TextView add_supplier_btn = rootView.findViewById(R.id.add_supplier_btn);
        mButtonDash = rootView.findViewById(R.id.meno);
        mButtonPlus = rootView.findViewById(R.id.piu);

        //Set click listeners on buttons
        save_product_btn.setOnClickListener(this);
        add_supplier_btn.setOnClickListener(this);

        //Set the spinner which shows existing supplier names
        mSupplierSpin.setOnItemSelectedListener(this);
        supplierNames = DatabaseUtils.getSuppliersNames(getActivity(), Costants.SUPPLIER);
        mSpinAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, supplierNames);
        mSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSupplierSpin.setAdapter(mSpinAdapter);

        Bundle bundle = getArguments();
        String uriString = null;
        if (bundle != null) uriString = bundle.getString(Costants.PRODUCT_URI);
        if (uriString != null) mCurrentProductUri = Uri.parse(uriString);

        //controllo quantità inserita con TextWatcher and Editable class.
        quantity_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                quantityAddProduct = 0;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() ==0){
                    quantityAddProduct = 0;
                } else{
                    quantityAddProduct = Integer.parseInt(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() ==0){
                    quantityAddProduct = 0;
                } else{
                    quantityAddProduct = Integer.parseInt(quantity_et.getText().toString());
                }

            }
        });


        //Set the title that corresponds to the fragment
        if (mCurrentProductUri == null) {

            getActivity().setTitle(getString(R.string.add_product));
            getActivity().invalidateOptionsMenu();
//            quantityAddProduct = 0;
            mButtonDash.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    if (quantityAddProduct > 0){

                        quantityAddProduct = quantityAddProduct - 1;
                        quantity_et.setText(String.valueOf(quantityAddProduct));

                    }else{

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
            getActivity().setTitle(getString(R.string.edit_product));
            getLoaderManager().initLoader(Costants.SINGLE_PRODUCT_LOADER, null, this);
        }

        SharedPreferences preferences = getActivity().getSharedPreferences("MyPref", 0);
        final SharedPreferences.Editor editor = preferences.edit();

        //This is for QuickStart. It will be shown only once at the first launch.
        if(preferences.getInt(Costants.THIRD_TAPPROMPT_IS_SHOWN, 0) == 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new MaterialTapTargetPrompt.Builder(AddProductFragment.this)
                            .setTarget(rootView.findViewById(R.id.add_supplier_btn))
                            .setPrimaryText(getString(R.string.hint_addSupplier_primary))
                            .setSecondaryText(getString(R.string.hint_addSupplier_secondary))
                            .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                                @Override
                                public void onPromptStateChanged(@NonNull MaterialTapTargetPrompt prompt, int state) {
                                    //that button already has a click listener
                                }
                            })
                            .show();
                    editor.putInt(Costants.THIRD_TAPPROMPT_IS_SHOWN, 1);
                    editor.apply();
                }
            }, 2500);
        }

        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentProductUri == null) {
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
        if(item.getItemId() == R.id.action_delete){
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

    private void deleteProduct(){
        if(mCurrentProductUri != null){
            int rowsDeleted = getActivity().getContentResolver().delete(mCurrentProductUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(getActivity(), "Error during delete",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(getActivity(), "product successfully deleted",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.save_btn: {
                //This button saves the new product to the database
                saveProduct();
                ProductListFragment prodFrag = new ProductListFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, prodFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.add_supplier_btn: {
                //This button opens a new fragment for adding a new supplier
                AddSupplierFragment addSupplierFrag = new AddSupplierFragment();
                Bundle args = new Bundle();
                args.putString(Costants.RELATION_TYPE, Costants.SUPPLIER);
                args.putString(Costants.REQUEST_CODE, Costants.ADD_PRODUCT_FRAGMENT);
                addSupplierFrag.setArguments(args);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, addSupplierFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }

        }
    }


    private void saveProduct() {
        //Make sure that product name is not null
        String productName = productName_et.getText().toString().trim();
        if (TextUtils.isEmpty(productName)) {
            Toast.makeText(getActivity(), R.string.product_name_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        //Make sure quantity is a positive integer
        int quantityInStock;
        try {
            quantityInStock = Integer.valueOf(quantity_et.getText().toString().trim());
            if (quantityInStock < 1) {
                Toast.makeText(getActivity(), R.string.quantity_should_be_positive, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException nfe) {
            Toast.makeText(getActivity(), R.string.quantity_should_be_positive, Toast.LENGTH_SHORT).show();
            return;
        }

        float salePrice;
        try{
            salePrice = Float.valueOf(salePrice_et.getText().toString().trim());
            if(salePrice < 0){
                Toast.makeText(getActivity(), R.string.price_should_be_number, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException nfe) {
            Toast.makeText(getActivity(), R.string.price_should_be_number, Toast.LENGTH_SHORT).show();
            return;
        }


        ContentValues values = new ContentValues();
        values.put(InventoryContract.ProductEntry.PRODUCT_NAME, productName);
        values.put(InventoryContract.ProductEntry.SALE_PRICE, salePrice);
        values.put(InventoryContract.ProductEntry.QUANTITY_IN_STOCK, quantityInStock);
        values.put(InventoryContract.ProductEntry.SUPPLIER_NAME, chosenSupplierName);

        if (mCurrentProductUri == null) {
            //This is new product entry
            Uri newUri = getActivity().getContentResolver().insert(InventoryContract.ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(getActivity(), R.string.error_saving_product, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.product_saved_successfully), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an existing product, so update the product
            int rowsAffected = getActivity().getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(getActivity(), R.string.error_saving_product, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.product_saved_successfully), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenSupplierName = supplierNames.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getActivity(), R.string.no_supplier_chosen, Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(getActivity(), mCurrentProductUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int productNameColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.QUANTITY_IN_STOCK);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.SALE_PRICE);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.SUPPLIER_NAME);

            String productName = cursor.getString(productNameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);
            String supplierName = cursor.getString(supplierColumnIndex);

            productName_et.setText(productName);
            salePrice_et.setText(String.valueOf(price));
            quantity_et.setText(String.valueOf(quantity));
            mSupplierSpin.setSelection(mSpinAdapter.getPosition(supplierName));

            quantityAddProduct = cursor.getInt(quantityColumnIndex);

            mButtonDash.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    if (quantityAddProduct > 0){

                        quantityAddProduct = quantityAddProduct - 1;
                        quantity_et.setText(String.valueOf(quantityAddProduct));

                    }else{

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