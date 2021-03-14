package com.example.wanderwand.travel;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.wanderwand.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import objects.MapItem;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.GPSTracker;
import utils.TravelmateSnackbars;

import static utils.Constants.API_LINK_V2;
import static utils.Constants.HERE_API_MODES;
import static utils.Constants.USER_TOKEN;

/**
 * Show markers on map around user's current location
 */
public class MapViewRealTimeActivity extends AppCompatActivity implements
        TravelmateSnackbars, Marker.OnMarkerClickListener {

    private final List<MapItem> mMapItems = new ArrayList<>();
    @BindView(R.id.data)
    ScrollView scrollView;
    @BindView(R.id.animation)
    LottieAnimationView animationView;
    @BindView(R.id.layout)
    LinearLayout layout;
    private int mIndex = 0;
    private Handler mHandler;
    private String mToken;
    private String mCurlat;
    private String mCurlon;
    private MapView mMap;
    private IMapController mController;
    private static final int REQUEST_LOCATION = 199;
    GPSTracker tracker;
    public ArrayList<Integer> mSelectedIndices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_realtime);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        mHandler = new Handler(Looper.getMainLooper());
        mMap = findViewById(R.id.map);
        scrollView.setVisibility(View.GONE);
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(USER_TOKEN, null);

        initMap();

        setTitle("Map View");

        // Get user's current location
        tracker = new GPSTracker(this);
        if (!tracker.canGetLocation()) {
            tracker.displayLocationRequest(this, mMap);
        } else {
            mCurlat = Double.toString(tracker.getLatitude());
            mCurlon = Double.toString(tracker.getLongitude());
            showDialogToSelectitems();
        }
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * On open street map initialize
     */
    private void initMap() {
        mMap.setBuiltInZoomControls(false);
        mMap.setMultiTouchControls(true);
        mMap.setTilesScaledToDpi(true);
        mController = mMap.getController();

        MyLocationNewOverlay mLocationoverlay = new MyLocationNewOverlay(mMap);
        mLocationoverlay.enableFollowLocation();
        mLocationoverlay.enableMyLocation();
        mMap.getOverlays().add(mLocationoverlay);
        mController.setZoom(14.0);
    }

    /**
     * Calls API to get nearby places
     *
     * @param mode mode; type of places;
     * @param icon marker icon
     */
    private void getMarkers(String mode, final int icon) {

        String uri = API_LINK_V2 + "get-places/" + mCurlat + "/" + mCurlon
                + "/" + mode;

        Log.v("EXECUTING", uri);

        //Set up client
        OkHttpClient client = new OkHttpClient();
        //Execute request
        Request request = new Request.Builder()
                .header("Authorization", "Token " + mToken)
                .url(uri)
                .build();
        //Setup callback
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Request Failed", "Message : " + e.getMessage());
                mHandler.post(() -> networkError());
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {

                final String res = Objects.requireNonNull(response.body()).string();
                mHandler.post(() -> {
                    try {
                        JSONArray routeArray = new JSONArray(res);

                        for (int i = 0; i < routeArray.length(); i++) {
                            String name = routeArray.getJSONObject(i).getString("title");
                            String web = routeArray.getJSONObject(i).getString("icon");
                            String nums = routeArray.getJSONObject(i).getString("distance");
                            String addr = routeArray.getJSONObject(i).getString("address");

                            Double latitude =
                                    routeArray.getJSONObject(i).getDouble("latitude");
                            Double longitude =
                                    routeArray.getJSONObject(i).getDouble("longitude");

                            showMarker(latitude, longitude, name, icon);
                            mMapItems.add(new MapItem(name, nums, web, addr));
                        }
                        animationView.setVisibility(View.GONE);
                        layout.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        networkError();
                        Log.e("ERROR : ", e.getMessage() + " ");
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_sort:
                showDialogToSelectitems();
                return true;
            case R.id.action_list_view :
                finish();
                Intent intent = ListViewRealTimeActivity.getStartIntent(MapViewRealTimeActivity.this);
                startActivity(intent);
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Shows a dialog to allow user to select items to be displayed
     * on map
     */
    private void showDialogToSelectitems() {
        ArrayList<Integer> selectedItems = new ArrayList(mSelectedIndices.size());

        for (int i = 0; i < mSelectedIndices.size(); i++) {
            selectedItems.set(i, mSelectedIndices.get(i));
        }
        mSelectedIndices.clear();
        new AlertDialog.Builder(this)
                .setTitle(R.string.title)
                .setMultiChoiceItems(R.array.items,null,new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which, boolean b) {
                        if (b) {
                            // If the user checked the item, add it to the selected items
                            selectedItems.add(which);
                        } else if (selectedItems.contains(which)) {
                            // Else, if the item is already in the array, remove it
                            selectedItems.remove(Integer.valueOf(which));
                        }
                    }
                })
                .setPositiveButton(R.string.choose, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //mGoogleMap.clear();
                        mMapItems.clear();
                        for (int i = 0; i < selectedItems.size(); i++) {
                        Log.v("selected", selectedItems.get(i) + " " + id);
                        mSelectedIndices.add(selectedItems.get(i));
                        Integer icon;
                        switch (selectedItems.get(i)) {
                            case 0:
                                icon = R.drawable.ic_local_pizza_black;
                                break;
                            case 1:
                                icon = R.drawable.ic_local_bar_black;
                                break;
                            case 2:
                                icon = R.drawable.ic_camera_alt_black;
                                break;
                            case 3:
                                icon = R.drawable.ic_directions_bus_black;
                                break;
                            case 4:
                                icon = R.drawable.ic_local_mall_black;
                                break;
                            case 5:
                                icon = R.drawable.ic_local_gas_station_black;
                                break;
                            case 6:
                                icon = R.drawable.ic_local_atm_black;
                                break;
                            case 7:
                                icon = R.drawable.ic_local_hospital_black;
                                break;
                            default:
                                icon = R.drawable.ic_attach_money_black;
                                break;
                        }
                        MapViewRealTimeActivity.this.getMarkers(HERE_API_MODES.get(selectedItems.get(i)), icon);
                    }
                    }
                })
                .show();
    }

    /**
     * Sets marker at given location on map
     *
     * @param locationLat  latitude
     * @param locationLong longitude
     * @param locationName name of location
     * @param locationIcon icon
     */
    private void showMarker(Double locationLat, Double locationLong, String locationName, Integer locationIcon) {
        GeoPoint coord = new GeoPoint(locationLat, locationLong);
        Marker marker = new Marker(mMap);

        if (ContextCompat.checkSelfPermission(MapViewRealTimeActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            marker.setPosition(coord);
            marker.setIcon(this.getDrawable(locationIcon));
            marker.setTitle(locationName);
            marker.setOnMarkerClickListener(this);

            mMap.getOverlays().add(marker);
            mMap.invalidate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, MapViewRealTimeActivity.class);
        return intent;
    }

    /**
     * On marker selected
     *
     * @param marker  marker
     */
    private void onMarkerSelected(Marker marker) {
        // Zoom to current location
        GeoPoint coordinate = new GeoPoint(Double.parseDouble(mCurlat), Double.parseDouble(mCurlon));
        mController.setZoom(14.0);
        mController.animateTo(coordinate);

        marker.showInfoWindow();

        scrollView.setVisibility(View.VISIBLE);
        for (int i = 0; i < mMapItems.size(); i++) {
            if (mMapItems.get(i).getName().equals(marker.getTitle())) {
                mIndex = i;
                break;
            }
        }

        TextView title = MapViewRealTimeActivity.this.findViewById(R.id.item_title);
        TextView description = MapViewRealTimeActivity.this.findViewById(R.id.item_description);
        final Button calls, book;
        calls = MapViewRealTimeActivity.this.findViewById(R.id.call);
        book = MapViewRealTimeActivity.this.findViewById(R.id.book);

        title.setText(mMapItems.get(mIndex).getName());

        description.setText(android.text.Html.fromHtml(mMapItems.get(mIndex).getAddress()).toString());
        calls.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + mMapItems.get(mIndex).getNumber()));
            MapViewRealTimeActivity.this.startActivity(intent);

        });
        book.setOnClickListener(view -> {
            Intent browserIntent;
            try {
                browserIntent = new Intent(
                        Intent.ACTION_VIEW, Uri.parse(mMapItems.get(mIndex).getAddress()));
                MapViewRealTimeActivity.this.startActivity(browserIntent);
            } catch (Exception e) {
                TravelmateSnackbars.createSnackBar(findViewById(R.id.map_real_time),
                        R.string.no_activity_for_browser, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case AppCompatActivity.RESULT_OK:
                        //User agreed to make required location settings changes
                        //startLocationUpdates();
                        TravelmateSnackbars.createSnackBar(findViewById(R.id.map_real_time),
                                R.string.location_enabled, Snackbar.LENGTH_LONG).show();
                        mCurlat = Double.toString(tracker.getLatitude());
                        mCurlon = Double.toString(tracker.getLongitude());
                        getMarkers("eat-drink", R.drawable.ic_local_pizza_black);

                        break;
                    case AppCompatActivity.RESULT_CANCELED:
                        //User chose not to make required location settings changes
                        TravelmateSnackbars.createSnackBar(findViewById(R.id.map_real_time),
                                R.string.location_not_enabled, Snackbar.LENGTH_LONG).show();
                        break;
                }
                break;
        }
    }

    /**
     * On marker item click
     *
     * @param marker  marker
     * @param mapView mapView
     */
    @Override
    public boolean onMarkerClick(Marker marker, MapView mapView) {
        onMarkerSelected(marker);
        return false;
    }

    /**
     * Plays the network lost animation in the view
     */
    private void networkError() {
        layout.setVisibility(View.GONE);
        animationView.setVisibility(View.VISIBLE);
        animationView.setAnimation(R.raw.network_lost);
        animationView.playAnimation();
    }
}