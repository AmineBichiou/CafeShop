package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Module.CafeModule;
import com.example.myapplication.adapter.CafeAdapter;
import com.example.myapplication.listeneur.CafeListeneur;
import com.example.myapplication.userActs.Login;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class addCafe extends AppCompatActivity {
    EditText name,price,img;
    Button add;
    CafeAdapter adapter;
    CafeListeneur cafeListeneur;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addarticle);
        add = findViewById(R.id.btnAdd);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        img = findViewById(R.id.img);

        add.setOnClickListener(v ->  {
            String name1 = name.getText().toString();
            double price1 = Double.parseDouble(price.getText().toString());
            String img1 = img.getText().toString();
            insertData(name1,price1,img1);
            Intent intent = new Intent(addCafe.this, Cafe.class);
            startActivity(intent);
        });

        }
    private void insertData(String name,double price,String img){
        FirebaseDatabase.getInstance().getReference().child("cafes").push()
                .setValue(new CafeModule(name,price,img))
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){

                        if(cafeListeneur != null && adapter != null)
                        {
                            cafeListeneur.onCafeLoadFailed("Cafe added successfully");
                            adapter.notifyDataSetChanged();
                        }

                    }
                    else{
                        if(cafeListeneur != null)
                        {
                            cafeListeneur.onCafeLoadFailed("Failed to add Cafe");

                        }
                    }
                });


    }
}


