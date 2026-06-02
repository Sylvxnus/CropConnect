package com.example.cropconnect.activities.guest;


//These are the imports that we have for the map feature
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import com.example.cropconnect.R;
import com.example.cropconnect.models.FoodBank;
import com.example.cropconnect.network.ApiClient;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Maps extends AppCompatActivity {

    //This is the mao vew that displays the OSMDroid map
    private MapView mapView;

    //THis stores all the food banks that have been fetched from the backend
    private List<FoodBank> allFoodBanks = new ArrayList<>();

    //Tracks which filters are currently active, whether the foodbank requires a referral for the food
    // Whether the food bank is open now or not, and if they have fresh produce
    private boolean filterNoReferral = false;
    private boolean filterOpenNow = false;
    private boolean filterFreshProduce = false;


    //Default variables for the map centre, centers the map to Ladywood, Birmingham
    private static final double LADYWOOD_LATITUDE = 52.4862;
    private static final double LADYWOOD_LONGITUDE = -1.9003;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Loading the OSMDroid config, which is required before setting the view for the map
        Configuration.getInstance().load(this, androidx.preference.PreferenceManager.getDefaultSharedPreferences(this));
        setContentView(R.layout.guest_map);
        mapView = findViewById(R.id.mapView);

        //Set the map tile style and enabling the pinch to zoom
        // On phones, this would mean that we use 2 fingers to pinch and zoom but for computers
        // we have to use the CTRL key + MouseClick and Drag
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        //Setting the initial zoom level and centre the map on Ladywood
        mapView.getController().setZoom(14.0);
        mapView.getController().setCenter(
                new GeoPoint(LADYWOOD_LATITUDE, LADYWOOD_LONGITUDE)
        );

        //requesting location permissions from the user if they haven't been granted before
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        //sets up the filter buttons and load food banks from the backend
        setupFilterButtons();
        loadFoodBanksFromBackend();
    }


    //This function sets up click listeners for the three filter buttons to filter food bank results
    // Each button toggles its filter on/off and refreshes the map pins based on tags in the postgreSQL database
    private void setupFilterButtons() {
        Button btnNoReferral = findViewById(R.id.btnNoReferral);
        Button btnOpenNow = findViewById(R.id.btnOpenNow);
        Button btnFreshProduce = findViewById(R.id.btnFreshProduce);


        //Toggling the no referral filter - a light green color when active, a darker green when inactive
        btnNoReferral.setOnClickListener(v -> {
            filterNoReferral = !filterNoReferral;
            btnNoReferral.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    filterNoReferral ? android.graphics.Color.parseColor("#4CAF50")
                            : android.graphics.Color.parseColor("#2E7D32")));
            refreshPins();
        });


        //Toggling the food bank open now feature
        btnOpenNow.setOnClickListener(v -> {
            filterOpenNow = !filterOpenNow;
            btnOpenNow.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    filterOpenNow ? android.graphics.Color.parseColor("#4CAF50")
                            : android.graphics.Color.parseColor("#2E7D32")));
            refreshPins();
        });


        //Togglign the fresh produce filter
        btnFreshProduce.setOnClickListener(v -> {
            filterFreshProduce = !filterFreshProduce;
            btnFreshProduce.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    filterFreshProduce ? android.graphics.Color.parseColor("#4CAF50")
                            : android.graphics.Color.parseColor("#2E7D32")));
            refreshPins();
        });
    }


    //This function clears all the pins from the map and redraws only the ones that match the active filters
    // Shows pins where the results match the filters toggled on by the user
    private void refreshPins() {
        mapView.getOverlays().clear();
        for (FoodBank fb : allFoodBanks) {
            //Skip any foodbank that doesnt match those active filters
            if (filterNoReferral && !fb.isNoReferral()) continue;
            if (filterOpenNow && !fb.isOpen()) continue;
            if (filterFreshProduce && !fb.isHasFreshProduce()) continue;
            addPin(fb.getLatitude(), fb.getLongitude(), fb.getName(), fb.getPhone());
        }
        mapView.invalidate();
    }


    //This fetches all food banks from the spring boot backend bia Retrofit library
    // On success stores them and then draws the pins, if failure, falls back to a test pin
    private void loadFoodBanksFromBackend() {
        ApiClient.getApiService().getFoodBanks().enqueue(new Callback<List<FoodBank>>() {
            @Override
            public void onResponse(Call<List<FoodBank>> call, Response<List<FoodBank>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allFoodBanks = response.body();
                    refreshPins();
                } else {
                    Log.e("Maps", "Response unsuccessful: " + response.code());
                    addTestPin();
                }
            }

            @Override
            public void onFailure(Call<List<FoodBank>> call, Throwable t) {
                // If the backend is unreachable for whatever reason, then we fall back to the hardcoded test pins from before
                Log.e("Maps", "Failed to load food banks: " + t.getMessage());
                addTestPin();
            }
        });
    }

    // Adds a single purple pin to the map at the given coordinates
    //I chose to make use of purple pins here as it is the same color theme as the Birmingham city council
    private void addPin(double lat, double lon, String title, String snippet) {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(lat, lon));
        marker.setTitle(title);
        marker.setSnippet(snippet);
        marker.setIcon(getResources().getDrawable(R.drawable.marker_purple));
        marker.setImage(null);
        mapView.getOverlays().add(marker);
    }


    //This is the fallback pin when the backend is unavailable

    //TEST: Tested to see if we can see pins on the map based on a given Geopoint
    //Expected Result: We want to see a green point pointing to the correct location on the map
    //Actual Result: Pin displays in the correct location and we can see it clearly, clicking on the pin
    // also gives us information such as the phone number
    private void addTestPin() {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(LADYWOOD_LATITUDE, LADYWOOD_LONGITUDE));
        marker.setTitle("Birmingham Central Food Bank");
        marker.setSnippet("Open Mon-Fri 9am-5pm");
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }


    //Resume and pause the map with the activity lifecycle
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