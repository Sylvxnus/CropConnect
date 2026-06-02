package com.example.cropconnect.activities.guest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.example.cropconnect.R;
import com.example.cropconnect.models.FoodBank;
import com.example.cropconnect.network.ApiClient;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Maps extends AppCompatActivity {

    private MapView mapView;

    private static final double LADYWOOD_LATITUDE = 52.4862;
    private static final double LADYWOOD_LONGITUDE = -1.9003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(this, androidx.preference.PreferenceManager.getDefaultSharedPreferences(this));

        setContentView(R.layout.guest_map);
        mapView = findViewById(R.id.mapView);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        mapView.getController().setZoom(14.0);
        mapView.getController().setCenter(
                new GeoPoint(LADYWOOD_LATITUDE, LADYWOOD_LONGITUDE)
        );

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        loadFoodBanksFromBackend();
    }

    private void loadFoodBanksFromBackend() {
        ApiClient.getApiService().getFoodBanks().enqueue(new Callback<List<FoodBank>>() {
            @Override
            public void onResponse(Call<List<FoodBank>> call, Response<List<FoodBank>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (FoodBank fb : response.body()) {
                        addPin(fb.getLatitude(), fb.getLongitude(), fb.getName(), fb.getPhone());
                    }
                } else {
                    Log.e("Maps", "Response unsuccessful: " + response.code());
                    addTestPin();
                }
            }

            @Override
            public void onFailure(Call<List<FoodBank>> call, Throwable t) {
                Log.e("Maps", "Failed to load food banks: " + t.getMessage());
                addTestPin();
            }
        });
    }

    private void addPin(double lat, double lon, String title, String snippet) {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(lat, lon));
        marker.setTitle(title);
        marker.setSnippet(snippet);
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }

    private void addTestPin() {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(LADYWOOD_LATITUDE, LADYWOOD_LONGITUDE));
        marker.setTitle("Birmingham Central Food Bank");
        marker.setSnippet("Open Mon-Fri 9am-5pm");
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}