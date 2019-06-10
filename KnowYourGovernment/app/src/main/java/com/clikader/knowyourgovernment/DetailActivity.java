package com.clikader.knowyourgovernment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    private Governor govn;

    private TextView dPosition;
    private TextView dName;
    private TextView dParty;
    private ImageView dPhoto;
    private TextView dAddress;
    private TextView dPhone;
    private TextView dEmail;
    private TextView dWebsite;
    private ImageView dYoutube;
    private ImageView dGoogleplus;
    private ImageView dTwitter;
    private ImageView dFacebook;
    private ConstraintLayout dLayout;
    private TextView locationText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: detailActicityCreated.");
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        setContentView(R.layout.activity_detail);
        locationText = (TextView) findViewById(R.id.locationTv2);
        dLayout = (ConstraintLayout) findViewById(R.id.detailLayout);

        Intent intent = getIntent();
        Governor newGov = (Governor) intent.getSerializableExtra("selected_gov");
        String receivedPos = (String) intent.getSerializableExtra("position");

        if (newGov != null) {
            govn = newGov;
        }

        if ((receivedPos != null) && (receivedPos != "")) {
            locationText.setText(receivedPos);
        }

        dPosition = (TextView) findViewById(R.id.detailPosition);
        dName = (TextView) findViewById(R.id.detailName);
        dParty = (TextView) findViewById(R.id.detailParty);
        dPhoto = (ImageView) findViewById(R.id.detailPhoto);
        dPhoto.setClickable(true);
        dPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent = new Intent(DetailActivity.this, PhotoActivity.class);
                photoIntent.putExtra("selected_gov", govn);
                photoIntent.putExtra("location", locationText.getText().toString());
                photoIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(photoIntent);
            }
        });
        dAddress = (TextView) findViewById(R.id.detailAddress);
        dPhone = (TextView) findViewById(R.id.detailPhone);
        dEmail = (TextView) findViewById(R.id.detailEmail);
        dWebsite = (TextView) findViewById(R.id.detailWebsite);
        dYoutube = (ImageView) findViewById(R.id.detailYoutube);
        dGoogleplus = (ImageView) findViewById(R.id.detailGoogleplus);
        dTwitter = (ImageView) findViewById(R.id.detailTwitter);
        dFacebook = (ImageView) findViewById(R.id.detailFacebook);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: detailActivity resumed.");
        dPosition.setText(govn.getPosition());
        dName.setText(govn.getName());

        if (!govn.getParty().equals("")) {
            dParty.setText("(" + govn.getParty() + ")");
        } else {
            dParty.setText("(Unknown)");
        }

        if (govn.getParty().equals("Republican")) {
            dLayout.setBackgroundColor(Color.parseColor("#ed2121"));
        } else if (govn.getParty().equals("Democratic")) {
            dLayout.setBackgroundColor(Color.parseColor("#2a4ee0"));
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
                            .into(dPhoto);
                }
            }).build();
            picasso.load(imgURL)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(dPhoto);
        } else {
            // no pic provided.
        }

        if (!networkCheck()) dPhoto.setImageResource(R.drawable.placeholder);

        if (!govn.getAddress().equals("")) {
            dAddress.setPaintFlags(dAddress.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            dAddress.setText(govn.getAddress());
            dAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + govn.getAddress());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });
        } else {
            dAddress.setText("No Data Provided");
        }

        if (!govn.getPhone().equals("")) {
            dPhone.setPaintFlags(dPhone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            dPhone.setText(govn.getPhone());
            dPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                    phoneIntent.setData(Uri.parse("tel:" + govn.getPhone()));
                    if (phoneIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(phoneIntent);
                    }
                }
            });
        } else {
            dPhone.setText("No Data Provided");
        }

        if (!govn.getEmail().equals("")) {
            dEmail.setPaintFlags(dEmail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            dEmail.setText(govn.getEmail());
            dEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto"));
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, govn.getEmail());
                    startActivity(Intent.createChooser(emailIntent, "Send Email"));
                }
            });
        } else {
            dEmail.setText("No Data Provided");
        }

        if (!govn.getWebsite().equals("")) {
            dWebsite.setPaintFlags(dWebsite.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            dWebsite.setText(govn.getWebsite());
            dWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri webpage = Uri.parse(govn.getWebsite());
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (webIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(webIntent);
                    }
                }
            });
        } else {
            dWebsite.setText("No Data Provided");
        }

        if (govn.getYoutube().equals("")) {
            dYoutube.setVisibility(View.INVISIBLE);
        } else {
            // todo
        }

        if (govn.getGoogleplus().equals("")) {
            dGoogleplus.setVisibility(View.INVISIBLE);
        } else {
            // todo
        }

        if (govn.getTwitter().equals("")) {
            dTwitter.setVisibility(View.INVISIBLE);
        } else {
            // todo
        }

        if (govn.getFacebook().equals("")) {
            dFacebook.setVisibility(View.INVISIBLE);
        } else {
            // todo
        }

        super.onResume();
    }

    public void twitterClicked(View v) {
        Intent intent = null;
        String tid = govn.getTwitter();
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + tid));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + tid));
        }
        startActivity(intent);
    }

    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + govn.getFacebook();
        String urlToUse = "";
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                //urlToUse = "fb://page/" + channels.get("Facebook");
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void googlePlusClicked(View v) {
        String name = govn.getGoogleplus();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://plus.google.com/" + name)));
        }
    }

    public void youTubeClicked(View v) {
        String name = govn.getYoutube();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
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

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: detailActivity stopped.");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: detailActivity paused.");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: detailActivity destoryed.");
        super.onDestroy();
    }
}
