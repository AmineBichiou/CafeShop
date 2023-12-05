package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Module.CafeModule;
import com.example.myapplication.Module.CartModule;
import com.example.myapplication.Module.SupplementModule;
import com.example.myapplication.adapter.CafeAdapter;
import com.example.myapplication.adapter.SupplementAdapter;
import com.example.myapplication.eventbus.MyUpdateCartEvent;
import com.example.myapplication.listeneur.CafeListeneur;
import com.example.myapplication.listeneur.CartListeneur;
import com.example.myapplication.listeneur.SupplementListeneur;
import com.example.myapplication.userActs.Login;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class Supplement extends AppCompatActivity implements CartListeneur, SupplementListeneur {
    FirebaseAuth auth;
    ImageView logout;
    TextView text;
    FirebaseUser user;
    RecyclerView recyclerView;
    SupplementAdapter adapter;
    Toolbar toolbar;
    ImageView btnAddCart;
    CartListeneur cartListeneur;
    SupplementListeneur supplementListeneur;
    List<SupplementModule> supplementModuleList = new ArrayList<>();


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if(EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class))
            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        EventBus.getDefault().unregister(this);
        super.onStop();

    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onUpdateCart(MyUpdateCartEvent event){
        countCartItems();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.supplement);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.logout);
        btnAddCart = findViewById(R.id.addCart);
        recyclerView = findViewById(R.id.recyclerView);
        supplementListeneur = this;
        cartListeneur = this;

        loadCafeFromFireBase();
        countCartItems();
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(Supplement.this, Login.class);
            startActivity(intent);

        } else {
            String[] emailParts = user.getEmail().split("@");
            String username = emailParts[0];

            getSupportActionBar().setTitle("        Welcome " + username);
        }

        logout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(Supplement.this, Login.class);
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        btnAddCart.setOnClickListener(v -> startActivity(new Intent(Supplement.this, Cart.class)));

    }

    private void loadCafeFromFireBase() {
        supplementModuleList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("supplement")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                      @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                         if (snapshot.exists()) {
                         for (DataSnapshot supplementSnapshot : snapshot.getChildren()) {
                        try {
                        SupplementModule cafeModule = supplementSnapshot.getValue(SupplementModule.class);
                        if (cafeModule != null) {
                        cafeModule.setKey(supplementSnapshot.getKey());
                        supplementModuleList.add(cafeModule);
                        } else {
                        Log.e("DataConversion", "Failed to convert CafeModule at key: " + supplementSnapshot.getKey());
                        }
                        } catch (DatabaseException e) {
                         Log.e("DataConversion", "Error converting data at key: " + supplementSnapshot.getKey(), e);
                       }
                       }
                          supplementListeneur.onSuppLoadSuccess(supplementModuleList);
                        } else {
                            supplementListeneur.onSuppLoadFailed("Can't find cafe");
                          }

                         }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                       }
                      }
                );
    }

    @Override
    public void onCartLoadSuccess(List<CartModule> cartModuleList) {
        int sum = 0;
        for (CartModule cartModule : cartModuleList) {
            sum += cartModule.getQuantity();
        }
    }

    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_LONG).show();

    }
    @Override
    public void onResume() {
        super.onResume();
        countCartItems();
    }
    private void countCartItems() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        List<CartModule> cartModules = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Cart").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        cartModules.clear();
                        for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                            CartModule cartModule = cartSnapshot.getValue(CartModule.class);
                            cartModule.setKey(cartSnapshot.getKey());
                            cartModules.add(cartModule);
                        }
                        cartListeneur.onCartLoadSuccess(cartModules);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartListeneur.onCartLoadFailed(error.getMessage());
                    }
                });
    }
    private void searchCafe(String query) {
        List<SupplementModule> filteredSupps = new ArrayList<>();
        for (SupplementModule supp : supplementModuleList) {
            if (supp.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredSupps.add(supp);
            }
        }

        if (filteredSupps.isEmpty()) {
            supplementListeneur.onSuppLoadFailed("No Supplements found");
        } else {
            supplementListeneur.onSuppLoadSuccess(filteredSupps);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchCafe(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public void onSuppLoadSuccess(List<SupplementModule> supplementModuleList) {
        adapter = new SupplementAdapter(this, supplementModuleList, cartListeneur);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

    }

    @Override
    public void onSuppLoadFailed(String message) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_LONG).show();

    }


}

