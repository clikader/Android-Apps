package com.clikader.knowyourgovernment;

import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AsyncLoad extends AsyncTask<String, Void, ArrayList<Governor>> {

    public static boolean running = false;
    private MainActivity mainActivity;
    private static final String TAG = "AsyncLoad";
    private ArrayList<Governor> govData = new ArrayList<>();
    private final String myAPI = "AIzaSyDK6e_wAJuk-m6y5gZg2wHWxw2ck7S3vAw";
    private final String civicURL = "https://www.googleapis.com/civicinfo/v2/representatives?key="
        + myAPI +"&address=";

    public String newLocation;
    
    public AsyncLoad(MainActivity ma) {mainActivity = ma;}

    @Override
    protected ArrayList<Governor> doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: Start to read the governor data.");
        StringBuilder sb = new StringBuilder();
        
        try {
            URL url = new URL(civicURL + strings[0]);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode == 400) {
                return null;
            }
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            govData = parseJson(sb.toString());
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return govData;
    }

    public ArrayList<Governor> parseJson(String s) {
        ArrayList<Governor> resultList = new ArrayList<>();
        try {
            // location part
            JSONObject jObjMain = new JSONObject(s);
            JSONObject locationResult = jObjMain.getJSONObject("normalizedInput");
            newLocation = locationResult.getString("city") + ", " +
                    locationResult.getString("state") + " " + locationResult.getString("zip");

            // office and detail part
            JSONArray officeArray = jObjMain.getJSONArray("offices");
            JSONArray officialArray = jObjMain.getJSONArray("officials");
            //Log.d(TAG, "parseJson: officearray: " + officeArray.length());
            //Log.d(TAG, "parseJson: officialarray: " + officialArray.length());

            for (int i = 0; i < officeArray.length(); i++) {
                String personPosition = "", personName = "", personParty = "";
                String personAddress = "", personPhone = "", personEmail = "";
                String personWebsite = "", personFacebook = "", personTwitter = "";
                String personGoogleplus = "", personYoutube = "", personPhoto = "";
                JSONObject resultObj = (JSONObject) officeArray.get(i);
                personPosition = resultObj.getString("name");
                JSONArray officialIndex = resultObj.getJSONArray("officialIndices");
                //Log.d(TAG, "parseJson: officialIndex size:" + officialIndex.length());

                for (int ii = 0; ii < officialIndex.length(); ii++) {
                    int index = Integer.parseInt(officialIndex.get(ii).toString());
                    JSONObject personDetail = officialArray.getJSONObject(index);


                    personName = personDetail.getString("name");


                    if (personDetail.has("address")) {
                        JSONArray addressArray = personDetail.getJSONArray("address");
                        JSONObject addressObj = addressArray.getJSONObject(0);
                        if (addressObj.has("line2")) {
                            personAddress = addressObj.getString("line1") + "\n" +
                                    addressObj.getString("line2") + "\n" +
                                    addressObj.getString("city") + ", " +
                                    addressObj.getString("state") + " " +
                                    addressObj.getString("zip");
                        } else {
                            personAddress = addressObj.getString("line1") + "\n" +
                                    addressObj.getString("city") + ", " +
                                    addressObj.getString("state") + " " +
                                    addressObj.getString("zip");
                        }
                    }


                    if (personDetail.has("party")) {
                        personParty = personDetail.getString("party");
                    }


                    if (personDetail.has("phones")) {
                        JSONArray phoneArray = personDetail.getJSONArray("phones");
                        StringBuilder phonesb = new StringBuilder();
                        for (int pindex = 0; pindex < phoneArray.length(); pindex++) {
                            phonesb.append(phoneArray.get(pindex).toString() + "\n");
                        }
                        personPhone = phonesb.toString();
                    }


                    if (personDetail.has("urls")) {
                        JSONArray urlArray = personDetail.getJSONArray("urls");
                        StringBuilder urlsb = new StringBuilder();
                        for (int uindex = 0; uindex < urlArray.length(); uindex++) {
                            urlsb.append(urlArray.get(uindex).toString() + "\n");
                        }
                        personWebsite = urlsb.toString();
                    }


                    if (personDetail.has("emails")) {
                        JSONArray emailArray = personDetail.getJSONArray("emails");
                        StringBuilder esb = new StringBuilder();
                        for (int eindex = 0; eindex < emailArray.length(); eindex++) {
                            esb.append(emailArray.get(eindex).toString() + "\n");
                        }
                        personEmail = esb.toString();
                    } else {
                        personEmail = "";
                    }


                    if (personDetail.has("photoUrl")) {
                        personPhoto = personDetail.getString("photoUrl");
                    }


                    if (personDetail.has("channels")) {
                        JSONArray socialMedias = personDetail.getJSONArray("channels");
                        for (int sindex = 0; sindex < socialMedias.length(); sindex++) {
                            JSONObject sm = socialMedias.getJSONObject(sindex);
                            switch (sm.getString("type")) {
                                case "googlePlus":
                                    personGoogleplus = sm.getString("id");
                                case "Facebook":
                                    personFacebook = sm.getString("id");
                                case "Twitter":
                                    personTwitter = sm.getString("id");
                                case "YouTube":
                                    personYoutube = sm.getString("id");
                            }
                        }
                    }
                }
                Governor newGovernor = new Governor(personPosition, personName, personParty, personAddress,
                        personPhone, personEmail, personWebsite, personFacebook, personTwitter, personGoogleplus,
                        personYoutube, personPhoto);
                resultList.add(newGovernor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }

    @Override
    protected void onPostExecute(ArrayList<Governor> governors) {
        running = false;
        TextView mainLocationText = (TextView) mainActivity.findViewById(R.id.locationTv);
        mainLocationText.setText(newLocation);
        Log.d(TAG, "onPostExecute: AsyncTask Terminated.");
        super.onPostExecute(governors);
    }
}
