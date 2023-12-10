package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myapplication.userActs.Login;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import java.util.ArrayList;
import java.util.List;

public class Mapctivity extends AppCompatActivity {

    MapView map;

    Toolbar toolbar;
    FirebaseAuth auth;

    DrawerLayout drawerLayout;

    public ActionBarDrawerToggle actionBarDrawerToggle;

    FirebaseUser user ;

    RelativeLayout cafelayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d("TAG", "onNavigationItemSelected: "+item.getTitle());
                if(item.getTitle().equals("Home")){
                    Intent intent = new Intent(Mapctivity.this, Mapctivity.class);
                    startActivity(intent);

                }
                if(item.getTitle().equals("Cart")){
                    Intent intent = new Intent(Mapctivity.this, Cart.class);
                    startActivity(intent);
                }
                if(item.getTitle().equals("Cafe")){
                    Intent intent = new Intent(Mapctivity.this, Cafe.class);
                    startActivity(intent);
                }
                if(item.getTitle().equals("Supplements")){
                    Intent intent = new Intent(Mapctivity.this, Supplement.class);
                    startActivity(intent);
                }
                if(item.getTitle().equals("Logout")){
                    auth.signOut();
                    Intent intent = new Intent(Mapctivity.this, Login.class);
                    startActivity(intent);
                }
                return false;
            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(Mapctivity.this, Login.class);
            startActivity(intent);

        } else {
            String[] emailParts = user.getEmail().split("@");
            String username = emailParts[0];

            getSupportActionBar().setTitle("        Welcome " + username);
        }

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map = findViewById(R.id.map);
        map.getTileProvider().clearTileCache();
        Configuration.getInstance().setCacheMapTileCount((short) 12);
        Configuration.getInstance().setCacheMapTileOvershoot((short) 12);

        // Create a custom tile source
        map.setTileSource(new OnlineTileSourceBase("", 1, 20, 512, ".png",
                new String[]{"https://a.tile.openstreetmap.org/"}) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                return getBaseUrl()
                        + MapTileIndex.getZoom(pMapTileIndex)
                        + "/" + MapTileIndex.getX(pMapTileIndex)
                        + "/" + MapTileIndex.getY(pMapTileIndex)
                        + mImageFilenameEnding;
            }
        });

        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        GeoPoint startPoint = new GeoPoint(36.7199677492622, 9.189881833415644);
        mapController.setZoom(11.0);
        mapController.setCenter(startPoint);
        final Context context = this;
        map.invalidate();
        createmarker();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void createmarker() {
        if (map == null) {
            return;
        }

        // Create a list to hold GeoPoints for additional markers
        List<GeoPoint> additionalPoints = new ArrayList<>();

        // Add three additional points close to the starting point
        additionalPoints.add(new GeoPoint(36.73180353740167, 9.20513480977097));
        additionalPoints.add(new GeoPoint(36.7231013027054, 9.188097417567235));

        // Create markers for each additional point
        for (GeoPoint point : additionalPoints) {
            Marker additionalMarker = new Marker(map);
            additionalMarker.setPosition(point);
            additionalMarker.setIcon(ContextCompat.getDrawable(this, R.drawable.loc)); // Custom red marker image
            additionalMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            map.getOverlays().add(additionalMarker);
        }

        // Create the initial marker at the starting point
        Marker myMarker = new Marker(map);
        myMarker.setPosition(new GeoPoint(36.7199677492622, 9.189881833415644));
        myMarker.setIcon(ContextCompat.getDrawable(this, R.drawable.loc)); // Custom red marker image
        myMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        map.getOverlays().add(myMarker);

        map.invalidate();
    }
}

