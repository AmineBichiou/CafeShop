package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.SearchView;

import com.example.myapplication.Module.CafeModule;
import com.example.myapplication.Module.CartModule;
import com.example.myapplication.adapter.CafeAdapter;
import com.example.myapplication.eventbus.MyUpdateCartEvent;
import com.example.myapplication.listeneur.CafeListeneur;
import com.example.myapplication.listeneur.CartListeneur;
import com.example.myapplication.userActs.Login;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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


public class Cafe extends AppCompatActivity implements CartListeneur, CafeListeneur {
    FirebaseAuth auth;
    ImageView logout;
    TextView text;
    FirebaseUser user;
    RecyclerView recyclerView;
    CafeAdapter adapter;
    Toolbar toolbar;
    ImageView btnAddCart;
    CafeListeneur cafeListeneur;
    CartListeneur cartListeneur;
    List<CafeModule> cafeModuleList = new ArrayList<>();

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
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.logout);
        btnAddCart = findViewById(R.id.addCart);
        recyclerView = findViewById(R.id.recyclerView);

        cartListeneur = this;
        cafeListeneur = this;
        loadCafeFromFireBase();


        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(Cafe.this, Login.class);
            startActivity(intent);

        } else {
            String[] emailParts = user.getEmail().split("@");
            String username = emailParts[0];

            getSupportActionBar().setTitle("        Welcome " + username);
        }

        logout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(Cafe.this, Login.class);
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        /*FirebaseRecyclerOptions<CafeModule> options =
                new FirebaseRecyclerOptions.Builder<CafeModule>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("cafes"), CafeModule.class)
                        .build();
        adapter = new CafeAdapter( this, options, user, cartListeneur);
        recyclerView.setAdapter(adapter);*/
        countCartItems();
        btnAddCart.setOnClickListener(v -> startActivity(new Intent(Cafe.this, Cart.class))
        );


    }
    private void loadCafeFromFireBase() {
        cafeModuleList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("cafes")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot cafeSnapshot : snapshot.getChildren()) {
                                try {
                                    CafeModule cafeModule = cafeSnapshot.getValue(CafeModule.class);
                                    if (cafeModule != null) {
                                        cafeModule.setKey(cafeSnapshot.getKey());
                                        cafeModuleList.add(cafeModule);
                                    } else {
                                        Log.e("DataConversion", "Failed to convert CafeModule at key: " + cafeSnapshot.getKey());
                                    }
                                } catch (DatabaseException e) {
                                    Log.e("DataConversion", "Error converting data at key: " + cafeSnapshot.getKey(), e);
                                }
                            }
                            cafeListeneur.onCafeLoadSuccess(cafeModuleList);
                        } else {
                            cafeListeneur.onCafeLoadFailed("Can't find cafe");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void searchCafe(String query) {
        List<CafeModule> filteredCafes = new ArrayList<>();
        for (CafeModule cafe : cafeModuleList) {
            if (cafe.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredCafes.add(cafe);
            }
        }

        if (filteredCafes.isEmpty()) {
            cafeListeneur.onCafeLoadFailed("No cafes found");
        } else {
            cafeListeneur.onCafeLoadSuccess(filteredCafes);
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

    @Override
    public void onCafeLoadSuccess(List<CafeModule> cafeModuleList) {
        adapter = new CafeAdapter(this, cafeModuleList, cartListeneur);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Override
    public void onCafeLoadFailed(String message) {

    }
}
