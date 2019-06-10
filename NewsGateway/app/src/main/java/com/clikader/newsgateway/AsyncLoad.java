package com.clikader.newsgateway;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AsyncLoad extends AsyncTask<String, Void, ArrayList<Source>> {

    public static boolean running = false;
    private MainActivity mainActivity;
    private static final String TAG = "AsyncLoad";
    private ArrayList<Source> sourceList = new ArrayList<>();
    private final String myAPI = "a603a4e59a0b47b8aafbb6b9477cf511";
    private final String apiPartURL = "&apiKey=" + myAPI;
    private final String catPartURL = "&category=";
    private final String firstPartURL = "https://newsapi.org/v1/sources?language=en&country=us";

    public AsyncLoad(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected ArrayList<Source> doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: Start to read source data.");
        StringBuilder sb = new StringBuilder();

        try {
            URL url;

            if (strings[0].equals("all")) {
                url = new URL(firstPartURL + apiPartURL);
            } else {
                url = new URL(firstPartURL + catPartURL + strings[0] + apiPartURL);
            }

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode == 400) {
                return null;
            }
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            sourceList = parseJson(sb.toString());
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return sourceList;
    }

    public ArrayList<Source> parseJson(String s) {
        ArrayList<Source> sourceData = new ArrayList<>();
        try {
            // get the source part
            JSONObject jObjMain = new JSONObject(s);
            JSONArray jArrayMain = jObjMain.getJSONArray("sources");

            // get each source
            for (int i = 0; i < jArrayMain.length(); i++) {
                JSONObject sourceDetail = (JSONObject) jArrayMain.get(i);
                String sourceId = sourceDetail.getString("id");
                String sourceName = sourceDetail.getString("name");
                Source newSource = new Source(sourceId, sourceName);
                sourceData.add(newSource);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sourceData;
    }

    @Override
    protected void onPostExecute(ArrayList<Source> sources) {
        running = false;
        Log.d(TAG, "onPostExecute: AsyncTask Load Sources Terminated.");
        super.onPostExecute(sources);
    }
}
