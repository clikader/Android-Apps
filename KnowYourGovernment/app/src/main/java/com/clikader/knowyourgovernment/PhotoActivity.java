package com.clikader.knowyourgovernment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class PhotoActivity extends AppCompatActivity {
    private Governor govn;
    private ConstraintLayout pLayout;
    private TextView pPosition;
    private TextView pName;
    private ImageView pPhoto;
    private TextView locationText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        setContentView(R.layout.activity_photo);
        locationText = (TextView) findViewById(R.id.locationTv3);
        pLayout = (ConstraintLayout) findViewById(R.id.pLayout);

        Intent intent = getIntent();
        govn = (Governor) intent.getSerializableExtra("selected_gov");
        String passedLoc = (String) intent.getSerializableExtra("location");
        locationText.setText(passedLoc);

        pPosition = (TextView) findViewById(R.id.photoPosition);
        pName = (TextView) findViewById(R.id.photoName);
        pPhoto = (ImageView) findViewById(R.id.photoPhoto);
    }

    @Override
    protected void onResume() {
        pPosition.setText(govn.getPosition());
        pName.setText(govn.getName());

        if (govn.getParty().equals("Republican")) {
            pLayout.setBackgroundColor(Color.parseColor("#ed2121"));
        } else if (govn.getParty().equals("Democratic")) {
            pLayout.setBackgroundColor(Color.parseColor("#2a4ee0"));
        }

        if (!govn.getPhoto().equals("")) {
            final String imgURL = govn.getPhoto();
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    final String changedUrl = imgURL.replace("http:", "https:");
                    picasso.load(changedUrl)
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(pPhoto);
                }
            }).build();
            picasso.load(imgURL)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(pPhoto);
        } else {
            // no pic provided.
        }

        if (!networkCheck()) {
            pPhoto.setImageResource(R.drawable.placeholder);
        }
        super.onResume();
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
}
