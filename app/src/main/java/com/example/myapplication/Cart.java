package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Module.CartModule;
import com.example.myapplication.adapter.MyCartAdapter;
import com.example.myapplication.eventbus.MyUpdateCartEvent;
import com.example.myapplication.listeneur.CartListeneur;
import com.example.myapplication.userActs.Login;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.widget.Toolbar;

public class Cart extends AppCompatActivity implements CartListeneur {
    ImageView exit;
    FirebaseAuth auth;
    RecyclerView recyclerCart;
    RelativeLayout cartlayout;

    TextView total;
    MyCartAdapter adapter;

    CartListeneur cartListeneur;

    Toolbar toolbar;
    FirebaseUser user;

    DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

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
        loadCartFromFirebase();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(null);


        //exit = findViewById(R.id.goback);
        recyclerCart = findViewById(R.id.recyclerCart);
        cartlayout = findViewById(R.id.mainAct);
        total = findViewById(R.id.total);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        cartListeneur = this;
        auth = FirebaseAuth.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerCart.setLayoutManager(layoutManager);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(Cart.this, Login.class);
            startActivity(intent);

        }

        /*exit.setOnClickListener(v -> {
            Intent intent = new Intent(this, Cafe.class);
            startActivity(intent);
        });*/

        loadCartFromFirebase();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d("TAG", "onNavigationItemSelected: "+item.getTitle());
                if(item.getTitle().equals("Home")){
                    Intent intent = new Intent(Cart.this, Mapctivity.class);
                    startActivity(intent);

                }
                if(item.getTitle().equals("Cart")){
                    Intent intent = new Intent(Cart.this, Cart.class);
                    startActivity(intent);
                }
                if(item.getTitle().equals("Cafe")){
                    Intent intent = new Intent(Cart.this, Cafe.class);
                    startActivity(intent);
                }
                if(item.getTitle().equals("Supplements")){
                    Intent intent = new Intent(Cart.this, Supplement.class);
                    startActivity(intent);
                }
                if(item.getTitle().equals("Logout")){
                    auth.signOut();
                    Intent intent = new Intent(Cart.this, Login.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadCartFromFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        List<CartModule> cartModuleList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Cart").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for(DataSnapshot cartSnapshot:snapshot.getChildren()){

                        CartModule cartModule = cartSnapshot.getValue(CartModule.class);
                        cartModule.setKey(cartSnapshot.getKey());
                        cartModuleList.add(cartModule);
                    }
                    cartListeneur.onCartLoadSuccess(cartModuleList);
                }
                else{
                    cartListeneur.onCartLoadFailed("Cart Empty");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                cartListeneur.onCartLoadFailed(databaseError.getMessage());
            }
        });
    }

    @Override
    public void onCartLoadSuccess(List<CartModule> cartModuleList) {
        double sum = 0;
        for(CartModule cartModule:cartModuleList){
            sum+=cartModule.getTotalPrice();
        }
        total.setText(new StringBuilder("Total: ").append(sum).append("DT"));
        adapter = new MyCartAdapter(this,cartModuleList);
        recyclerCart.setAdapter(adapter);
    }

    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(cartlayout,message,Snackbar.LENGTH_LONG).show();

    }

}
