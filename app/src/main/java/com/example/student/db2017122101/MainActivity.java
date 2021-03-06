package com.example.student.db2017122101;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.Permission;
import java.util.List;
import java.util.Locale;

// https://maps.googleapis.com/maps/api/place/textsearch/json?query=%E9%A4%90%E5%BB%B3+%E5%BA%9C%E4%B8%AD%E7%AB%99&key=AIzaSyB03rADXr_cx1UI3qvEe6BVR6Vda4XxQs0


public class MainActivity extends AppCompatActivity {
    LocationManager lm;
    MyLocationListener listener;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        tv = (TextView) findViewById(R.id.textView);
        listener = new MyLocationListener(this, tv);

    }

    public void click1(View v) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    123
            );
            return;
        }
        setOnLocListener();
    }

    private void setOnLocListener()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, listener);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setOnLocListener();
            }
        }

    }
}

class MyLocationListener implements LocationListener {
    Context context;
    TextView tv;
    public MyLocationListener(Context context, TextView tv)
    {
        this.context = context;
        queue = Volley.newRequestQueue(context);
        this.tv = tv;
    }
    RequestQueue queue;

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Log.d("LOC", "Change!! " + String.valueOf(lat) + "," + String.valueOf(lng));
        Location loc101 = new Location("MYLOC");
        loc101.setLatitude(25.0340661);
        loc101.setLongitude(121.5643117);

        double distance = loc101.distanceTo(location);
        Log.d("LOC", "Distance:" + distance);

        Geocoder geocoder = new Geocoder(context, Locale.TRADITIONAL_CHINESE);
        try {
            List<Address> addr = geocoder.getFromLocationName("板橋區中山路一段10號", 1);
            Address a0 = addr.get(0);
            Log.d("LOC", "" + a0.getLatitude() + "," + a0.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringRequest request = new StringRequest("https://maps.googleapis.com/maps/api/elevation/json?locations=25.0080382,121.4599224&key=AIzaSyB03rADXr_cx1UI3qvEe6BVR6Vda4XxQs0"
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray array = obj.getJSONArray("results");
                    double alt = array.getJSONObject(0).getDouble("elevation");
                    Log.d("ALT:", String.valueOf(alt));
                    tv.setText(String.valueOf(alt));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("NET", "Error:" + error.toString());
                    }
                }
        );
        queue.add(request);
        queue.start();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}