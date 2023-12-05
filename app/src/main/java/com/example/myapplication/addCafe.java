package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Module.CafeModule;
import com.google.firebase.database.FirebaseDatabase;


public class addCafe extends AppCompatActivity {
    EditText name,price,img;
    Button add;


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
        });

        }
    private void insertData(String name,double price,String img){
        FirebaseDatabase.getInstance().getReference().child("cafes").push()
                .setValue(new CafeModule(name,price,img))
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(addCafe.this,"Cafe added successfully",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(addCafe.this,"Failed to add Cafe",Toast.LENGTH_LONG).show();
                    }
                });
    }
}


