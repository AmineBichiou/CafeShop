package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Module.CafeModule;
import com.example.myapplication.adapter.SupplementAdapter;
import com.example.myapplication.listeneur.SupplementListeneur;
import com.google.firebase.database.FirebaseDatabase;

public class addSupplement  extends AppCompatActivity {

    EditText name,price,img;
    Button add;

    SupplementAdapter adapter;
    SupplementListeneur suppListeneur;
    ImageView back;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addsupplement);
        add = findViewById(R.id.btnAdd);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        img = findViewById(R.id.img);
        back = findViewById(R.id.goback);

        add.setOnClickListener(v ->  {
            String name1 = name.getText().toString();
            double price1 = Double.parseDouble(price.getText().toString());
            String img1 = img.getText().toString();
            insertData(name1,price1,img1);
            Intent intent = new Intent(addSupplement.this, Supplement.class);
            startActivity(intent);

        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(addSupplement.this, Supplement.class);
                startActivity(intent1);
            }
        });
    }


    private void insertData(String name,double price,String img){
        FirebaseDatabase.getInstance().getReference().child("supplement").push()
                .setValue(new CafeModule(name,price,img))
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if(suppListeneur != null && adapter != null)
                        {
                            suppListeneur.onSuppLoadFailed("Cafe added successfully");
                            adapter.notifyDataSetChanged();
                        }
                        Toast.makeText(addSupplement.this,"Supplement added successfully",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(addSupplement.this,"Failed to add Supplement",Toast.LENGTH_LONG).show();
                    }
                });
    }
}
