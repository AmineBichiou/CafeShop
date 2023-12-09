package com.example.myapplication.userActs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class MainActivity extends AppCompatActivity/* implements OnMapReadyCallback */{
    Button login;
    Button signup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landingpage);

        login = findViewById(R.id.btnLogin);
        signup  = findViewById(R.id.btnSignUp);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);

            }
        });


        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng newLocation = new LatLng(36.397428619691446, 10.62400408571229); // New coordinates

        googleMap.addMarker(new MarkerOptions().position(newLocation).title("New Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15));
    }*/
    }
}
