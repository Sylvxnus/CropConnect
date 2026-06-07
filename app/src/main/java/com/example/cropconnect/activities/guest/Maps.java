package com.example.cropconnect.activities.guest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.text.Editable;
import android.text.TextWatcher;
import com.example.cropconnect.R;
import com.example.cropconnect.models.FoodBank;
import com.example.cropconnect.models.FoodBankSearchResult;
import com.example.cropconnect.network.ApiClient;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff;

public class Maps extends AppCompatActivity {

    private MapView mapView;
    private List<FoodBank> allFoodBanks = new ArrayList<>();
    private boolean filterNoReferral = false;
    private boolean filterOpenNow = false;
    private boolean filterFreshProduce = false;
    private double currentDestLat, currentDestLon;

    private static final double LADYWOOD_LATITUDE = 52.4907;
    private static final double LADYWOOD_LONGITUDE = -1.8816;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(this, androidx.preference.PreferenceManager.getDefaultSharedPreferences(this));
        setContentView(R.layout.guest_map);
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(14.0);
        mapView.getController().setCenter(new GeoPoint(LADYWOOD_LATITUDE, LADYWOOD_LONGITUDE));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        Button btnDirections = findViewById(R.id.btnDirections);
        btnDirections.setVisibility(View.GONE);
        btnDirections.setOnClickListener(v -> fetchAndDrawRoute(currentDestLat, currentDestLon));
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.allotment_moreinfo).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.birmingham.gov.uk/allotments"));
            startActivity(intent);
        });
        setupFilterButtons();
        loadFoodBanksFromBackend();
        setupSearch();
    }

    private void setupFilterButtons() {
        Button btnNoReferral = findViewById(R.id.btnNoReferral);
        Button btnOpenNow = findViewById(R.id.btnOpenNow);
        Button btnFreshProduce = findViewById(R.id.btnFreshProduce);

        btnNoReferral.setOnClickListener(v -> {
            filterNoReferral = !filterNoReferral;
            btnNoReferral.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    filterNoReferral ? android.graphics.Color.parseColor("#4CAF50")
                            : android.graphics.Color.parseColor("#2E7D32")));
            refreshPins();
        });

        btnOpenNow.setOnClickListener(v -> {
            filterOpenNow = !filterOpenNow;
            btnOpenNow.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    filterOpenNow ? android.graphics.Color.parseColor("#4CAF50")
                            : android.graphics.Color.parseColor("#2E7D32")));
            refreshPins();
        });

        btnFreshProduce.setOnClickListener(v -> {
            filterFreshProduce = !filterFreshProduce;
            btnFreshProduce.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    filterFreshProduce ? android.graphics.Color.parseColor("#4CAF50")
                            : android.graphics.Color.parseColor("#2E7D32")));
            refreshPins();
        });
    }

    private void refreshPins() {
        mapView.getOverlays().clear();
        for (FoodBank fb : allFoodBanks) {
            if (filterNoReferral && !fb.isNoReferral()) continue;
            if (filterOpenNow && !fb.isOpen()) continue;
            if (filterFreshProduce && !fb.isHasFreshProduce()) continue;
            addPin(fb.getLatitude(), fb.getLongitude(), fb.getFbName(), fb.getPhone());
        }
        mapView.invalidate();
    }

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
        marker.setIcon(getResources().getDrawable(R.drawable.marker_purple));
        marker.setImage(null);
        marker.setOnMarkerClickListener((m, map) -> {
            m.showInfoWindow();
            currentDestLat = lat;
            currentDestLon = lon;
            findViewById(R.id.btnDirections).setVisibility(View.VISIBLE);
            return true;
        });
        mapView.getOverlays().add(marker);
    }

    private void addTestPin() {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(LADYWOOD_LATITUDE, LADYWOOD_LONGITUDE));
        marker.setTitle("Birmingham Central Food Bank");
        marker.setSnippet("Open Mon-Fri 9am-5pm");
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }

    private void setupSearch() {
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                findViewById(R.id.btnDirections).setVisibility(View.GONE);
                if (query.isEmpty()) {
                    refreshPins();
                } else {
                    ApiClient.getApiService().searchFoodBanksAndProducts(query).enqueue(new Callback<List<FoodBankSearchResult>>() {
                        @Override
                        public void onResponse(Call<List<FoodBankSearchResult>> call, Response<List<FoodBankSearchResult>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                mapView.getOverlays().clear();
                                for (FoodBankSearchResult result : response.body()) {
                                    FoodBank fb = result.getFoodBank();
                                    String snippet = fb.getPhone();
                                    if (result.getMatchedProduct() != null) {
                                        snippet = result.getMatchedProduct() + ": "
                                                + result.getMatchedQuantity() + " "
                                                + result.getMatchedUnit()
                                                + " | " + fb.getPhone();
                                    }
                                    addPin(fb.getLatitude(), fb.getLongitude(), fb.getFbName(), snippet);
                                }
                                mapView.invalidate();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<FoodBankSearchResult>> call, Throwable t) {
                            Log.e("Maps", "Search failed: " + t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchAndDrawRoute(double destLat, double destLon) {
        double userLat = LADYWOOD_LATITUDE;
        double userLon = LADYWOOD_LONGITUDE;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            for (String provider : lm.getAllProviders()) {
                android.location.Location loc = lm.getLastKnownLocation(provider);
                if (loc != null) {
                    userLat = loc.getLatitude();
                    userLon = loc.getLongitude();
                    break;
                }
            }
        }

        callOsrm(userLat, userLon, destLat, destLon);
    }

    private void callOsrm(double userLat, double userLon, double destLat, double destLon) {
        String url = "https://router.project-osrm.org/route/v1/driving/"
                + userLon + "," + userLat + ";"
                + destLon + "," + destLat
                + "?overview=full&geometries=geojson";

        new Thread(() -> {
            try {
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection)
                        new java.net.URL(url).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Android)");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.connect();

                java.io.InputStream stream = conn.getResponseCode() >= 400
                        ? conn.getErrorStream() : conn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);

                JSONObject json = new JSONObject(sb.toString());
                JSONArray coords = json.getJSONArray("routes")
                        .getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONArray("coordinates");

                List<GeoPoint> points = new ArrayList<>();
                for (int i = 0; i < coords.length(); i++) {
                    JSONArray c = coords.getJSONArray(i);
                    points.add(new GeoPoint(c.getDouble(1), c.getDouble(0)));
                }

                runOnUiThread(() -> {
                    mapView.getOverlays().removeIf(o -> o instanceof Polyline);

                    Polyline polyline = new Polyline(mapView);
                    polyline.setPoints(points);
                    polyline.getOutlinePaint().setColor(android.graphics.Color.BLUE);
                    polyline.getOutlinePaint().setStrokeWidth(8f);
                    mapView.getOverlays().add(0, polyline);

                    Marker userMarker = new Marker(mapView);
                    userMarker.setPosition(new GeoPoint(userLat, userLon));
                    userMarker.setTitle("Your Location");
                    Drawable icon = getResources().getDrawable(R.drawable.marker_purple).mutate();
                    icon.setColorFilter(new android.graphics.PorterDuffColorFilter(
                            android.graphics.Color.RED, android.graphics.PorterDuff.Mode.SRC_IN));
                    userMarker.setIcon(icon);
                    userMarker.setImage(null);
                    mapView.getOverlays().add(userMarker);

                    mapView.postInvalidate();
                });
            } catch (Exception e) {
                Log.e("Maps", "OSRM error: " + e.getMessage());
            }
        }).start();
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