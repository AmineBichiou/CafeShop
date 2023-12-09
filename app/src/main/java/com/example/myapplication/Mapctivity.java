package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.core.content.ContextCompat;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
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

    public void createmarker() {
        if (map == null) {
            return;
        }

        // Create a list to hold GeoPoints for additional markers
        List<GeoPoint> additionalPoints = new ArrayList<>();

        // Add three additional points close to the starting point
        additionalPoints.add(new GeoPoint(36.72007420718662, 9.18959945521804));
        additionalPoints.add(new GeoPoint(36.73180353740167, 9.20513480977097));
        additionalPoints.add(new GeoPoint(36.7231013027054, 9.188097417567235));

        // Create markers for each additional point
        for (GeoPoint point : additionalPoints) {
            Marker additionalMarker = new Marker(map);
            additionalMarker.setPosition(point);
            additionalMarker.setIcon(ContextCompat.getDrawable(this, R.drawable.mapp)); // Custom red marker image
            additionalMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            map.getOverlays().add(additionalMarker);
        }

        // Create the initial marker at the starting point
        Marker myMarker = new Marker(map);
        myMarker.setPosition(new GeoPoint(36.7199677492622, 9.189881833415644));
        myMarker.setIcon(ContextCompat.getDrawable(this, R.drawable.mapp)); // Custom red marker image
        myMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        map.getOverlays().add(myMarker);

        map.invalidate();
    }
}

