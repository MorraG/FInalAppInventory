package com.example.user.finalappinventory;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.user.finalappinventory.utils.Costants;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {

    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private ConstraintLayout mConstraintLayout;
    private ConstraintSet mConstraintSet2;
    private ConstraintSet mConstraintSet1;
    private FloatingActionButton fab_main, fab_add_product, fab_transaction;
    private boolean fabsInClickedState = false;
    private TextView hint_main_tv, hint_add_item_tv, hint_transaction_tv;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set navigation drawer
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Define constraint sets for a constraint set animation between idle and clicked states of fab buttons
        mConstraintSet1 = new ConstraintSet();
        mConstraintSet2 = new ConstraintSet();
        mConstraintLayout = findViewById(R.id.fab_default_state);
        mConstraintSet1.clone(mConstraintLayout);
        mConstraintSet2.clone(this, R.layout.fab_clicked_state);

        //Find floating actions buttons and hint textviews
        fab_main =  findViewById(R.id.fab_main);
        fab_add_product = findViewById(R.id.fab_add_item);
        fab_transaction = findViewById(R.id.fab_transaction);
        hint_main_tv = findViewById(R.id.hint_cancel);
        hint_transaction_tv = findViewById(R.id.hint_purchase);
        hint_add_item_tv = findViewById(R.id.hint_add_product);

        //Set click listeners on fabs
        fab_main.setOnClickListener(this);
        fab_add_product.setOnClickListener(this);
        fab_transaction.setOnClickListener(this);

        //Add product list fragment as the default fragment
        if(savedInstanceState == null) {
            ProductListFragment productListFrag = new ProductListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, productListFrag)
                    .addToBackStack(null)
                    .commit();
        } else {
            fabsInClickedState = savedInstanceState.getBoolean(Costants.IS_FAB_CLICKED);
            if(fabsInClickedState) showFABs();
        }

        preferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = preferences.edit();
        //This is for QuickStart. It will be shown only once at the first launch.
        if((preferences.getInt(Costants.FIRST_TAPPROMPT_IS_SHOWN, 0) == 0)){
            new MaterialTapTargetPrompt.Builder(MainActivity.this)
                    .setTarget(findViewById(R.id.fab_main))
                    .setPrimaryText("Welcome to your mobile inventory. Let's get started!")
                    .setSecondaryText("Tap the see more options")
                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                    {
                        @Override
                        public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                        {
                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                            {
                                //showFABs();
                            }
                        }
                    })
                    .show();
            editor.putInt(Costants.FIRST_TAPPROMPT_IS_SHOWN, 1);
            editor.apply();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Costants.IS_FAB_CLICKED, fabsInClickedState);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.fab_main: {
                if (!fabsInClickedState) {
                    showFABs();
                } else {
                    showSingleFAB();
                }
                break;
            }
            case R.id.fab_add_item: {
                AddProductFragment addProductFrag = new AddProductFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, addProductFrag)
                        .addToBackStack(null)
                        .commit();
                showSingleFAB();
                break;
            }
//            case R.id.fab_transaction: {
//                openAddTransactionFragment(Costants.ACQUISITION);
//                showSingleFAB();
//                break;
//
//            }
        }
        fabsInClickedState ^= true;
    }

    private void showFABs(){
        TransitionManager.beginDelayedTransition(mConstraintLayout, new MainActivity.MyTransition());
        hint_add_item_tv.setVisibility(View.VISIBLE);
        hint_transaction_tv.setVisibility(View.VISIBLE);
        hint_main_tv.setVisibility(View.VISIBLE);
        mConstraintSet2.applyTo(mConstraintLayout);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab_main.setImageResource(R.drawable.ic_refresh);

            }
        }, 500);

        //This is for QuickStart. It will be shown only once at the first launch.
        if(preferences.getInt(Costants.SECOND_TAPPROMPT_IS_SHOWN, 0) == 0){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new MaterialTapTargetPrompt.Builder(MainActivity.this)
                            .setTarget(findViewById(R.id.fab_add_item))
                            .setPrimaryText("Add your first product")
                            .setSecondaryText("Tap to enter your first product")
                            .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                                @Override
                                public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {

                                    }
                                }
                            })
                            .show();
                    editor.putInt(Costants.SECOND_TAPPROMPT_IS_SHOWN, 1);
                    editor.apply();
                }
            }, 3000);
        }
    }



    private void showSingleFAB(){
        TransitionManager.beginDelayedTransition(mConstraintLayout, new MainActivity.MyTransition());
        hint_add_item_tv.setVisibility(View.GONE);
        hint_transaction_tv.setVisibility(View.GONE);
        hint_main_tv.setVisibility(View.GONE);
        mConstraintSet1.applyTo(mConstraintLayout);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab_main.setImageResource(R.drawable.ic_add_circle);

            }
        }, 500);
        //This is for QuickStart. It will be shown only once at the first launch.
        if(preferences.getInt(Costants.SECOND_TAPPROMPT_IS_SHOWN, 0) == 0){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new MaterialTapTargetPrompt.Builder(MainActivity.this)
                            .setTarget(findViewById(R.id.fab_add_item))
                            .setPrimaryText("Add your first product")
                            .setSecondaryText("Tap to enter your first product")
                            .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                                @Override
                                public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {

                                    }
                                }
                            })
                            .show();
                    editor.putInt(Costants.SECOND_TAPPROMPT_IS_SHOWN, 1);
                    editor.apply();
                }
            }, 3000);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.products:{
                ProductListFragment productListFrag = new ProductListFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, productListFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.clients:{
                openClientListFragment(Costants.CLIENT);
                break;
            }
            case R.id.suppliers:{
                openSupplierListFragment(Costants.SUPPLIER);
                break;
            }
            /*
            case R.id.transaction_list:{
                openTransactionListFragment(Costants.TRANSACTION);
                break;
            }*/
            case R.id.add_product:{
                AddProductFragment addProductFrag = new AddProductFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, addProductFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.add_supplier:{
                AddSupplierFragment addSupplierFrag = new AddSupplierFragment();
                getSupportFragmentManager ().beginTransaction()
                        .replace(R.id.container, addSupplierFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void openSupplierListFragment(String relationshipType){
        SupplierListFragment supplierListFrag = new SupplierListFragment();
        Bundle args = new Bundle();
        args.putString(Costants.RELATION_TYPE, relationshipType);
        supplierListFrag.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, supplierListFrag)
                .addToBackStack(null)
                .commit();
    }

    private void openClientListFragment(String relationshipType){
       /* ClientListFragment clientListFrag = new CientListFragment();
        Bundle args = new Bundle();
        clientListFrag.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, supplierListFrag)
                .addToBackStack(null)
                .commit();*/
    }


//    private void openTransactionListFragment(String transaction){
//        AddTransactionFragment transactionFrag = new AddTransactionFragment();
//        Bundle args = new Bundle();
//        args.putString(Costants.TRANSACTION, transaction);
//        transactionFrag.setArguments(args);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.container, transactionFrag)
//                .addToBackStack(null)
//                .commit();
//    }

    //Custom transition used during the transition of constraint sets
    static private class MyTransition extends TransitionSet {
        {
            setDuration(1000);
            setOrdering(ORDERING_SEQUENTIAL);
            addTransition(new android.support.transition.ChangeBounds());
            addTransition(new Fade(Fade.IN));
        }
    }
}
