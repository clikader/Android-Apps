package com.clikader.knowyourgovernment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    public ArrayList<Governor> govList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GovAdapter gAdapter;
    private TextView locationText;
    private Locator locator;
    public String currentLoc;
    public String zipcode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationText = (TextView) findViewById(R.id.locationTv);
        locator = new Locator(this);

        loadGovernor(getCurrentZip());

        recyclerView = (RecyclerView) findViewById(R.id.govRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        gAdapter = new GovAdapter(govList, this);
        recyclerView.setAdapter(gAdapter);
    }

    @Override
    protected void onResume() {

        if (networkCheck()) {

        } else {
            locationText.setText(getString(R.string.locationNoData));
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final TextView tv = new TextView(this);
            builder.setView(tv);
            builder.setTitle("No Network Connection");
            builder.setMessage("Data cannot be accessed/loaded without an internet connection");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        super.onResume();
    }

    public void setData(double lat, double lon) {
        currentLoc = doAddress(lat, lon);
        //loadGovernor(getCurrentZip());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult: CALL: " + permissions.length);
        Log.d(TAG, "onRequestPermissionsResult: PERM RESULT RECEIVED");

        if (requestCode == 5) {
            Log.d(TAG, "onRequestPermissionsResult: permissions.length: " + permissions.length);
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionsResult: HAS PERM");
                        locator.setUpLocationManager();
                        locator.determineLocation();
                    } else {
                        Toast.makeText(this, "Location permission was denied - cannot determine address", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onRequestPermissionsResult: NO PERM");
                    }
                }
            }
        }
        Log.d(TAG, "onRequestPermissionsResult: Exiting onRequestPermissionsResult");
    }

    public String doAddress(double lat, double lon) {
        List<Address> addresses = null;
        for (int times = 0; times < 3; times++) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(lat, lon, 1);
                StringBuilder sb = new StringBuilder();

                for (Address ad : addresses) {
                    setCurrentZip(ad.getPostalCode());
                    sb.append(ad.getLocality() + ", " + ad.getAdminArea() + " " + ad.getPostalCode());
                }
                return sb.toString();
            } catch (IOException e) {
                Log.d(TAG, "doAddress: " + e.getMessage());
            }
        }
        return null;
    }

    public void noLocationAvailable() {
        Toast.makeText(this, "No Location Providers were available", Toast.LENGTH_SHORT).show();
    }

    public void setCurrentZip(String zc) {
        zipcode = zc;
    }

    public String getCurrentZip() {
        return zipcode;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAbout:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            case R.id.menuLocation:
                if(networkCheck()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    final EditText et = new EditText(this);
                    et.setGravity(Gravity.CENTER_HORIZONTAL);
                    builder.setView(et);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            govList.clear();
                            loadGovernor(et.getText().toString());
                            Log.d(TAG, "onClick: " + govList.size());
                            gAdapter.notifyDataSetChanged();
                            Log.d(TAG, "onClick: " + govList.size());
                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // canceled
                        }
                    });
                    builder.setMessage("Please enter the city name or zip code:");
                    builder.setTitle("Enter Location");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                } else {
                    Log.d(TAG, "onOptionsItemSelected: No Network Connection Found.");
                    displayMessage("No Network Found", "Internet connection is needed to " +
                            "change the location or update the data.");
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildAdapterPosition(v);
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra("selected_gov", govList.get(pos));
        detailIntent.putExtra("position", locationText.getText().toString());
        detailIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(detailIntent);
    }

    public void loadGovernor(String input) {
        if (AsyncLoad.running) {
            //Toast.makeText(this, "Wait for Async task to finish", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Governor> newList = new ArrayList<>();

        AsyncLoad.running = true;
        try {
            newList = new AsyncLoad(this).execute(input).get();
        } catch (Exception e) {
            Log.d(TAG, "loadGovernor: " + e.getMessage());
            e.printStackTrace();
        }
        if (govList == null) {
            locationText.setText("No Data Found For " + input);
            displayMessage("Error", getString(R.string.returned400));
        }

        if (newList != null) {
            govList.clear();
            govList.addAll(newList);
        } else {
            displayMessage("Illegal Input", "The city name or zip code you entered" +
                    " cannot be found, please check your address.");
            String locationT = getString(R.string.illegalInput);
            locationText.setText(locationT);
            Log.d(TAG, "loadGovernor: " + getString(R.string.illegalInput));
        }
    }

    public boolean networkCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if(netinfo != null && netinfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void displayMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        //locator.shutdown();
        super.onDestroy();
    }
}
