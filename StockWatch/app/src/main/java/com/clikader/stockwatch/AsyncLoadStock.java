package com.clikader.stockwatch;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AsyncLoadStock extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

    public static boolean running = false;
    private MainActivity mainActivity;
    private static final String TAG = "AsyncLoadStock";
    private ArrayList<HashMap<String, String>> stockData =  new ArrayList<HashMap<String, String>>();
    private final String stockURL = "http://d.yimg.com/aq/autoc?region=US&lang=en-US&query=";

    public AsyncLoadStock(MainActivity ma) {mainActivity = ma;}

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: Start to read the stocks based on the user input.");
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(stockURL + strings[0]);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

            stockData = parseJson(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return stockData;
    }

    public ArrayList<HashMap<String, String>> parseJson(String s) {
        ArrayList<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
        try {
            JSONObject jObjMain = new JSONObject(s);

            JSONObject resultSet = jObjMain.getJSONObject("ResultSet");
            JSONArray results = resultSet.getJSONArray("Result");

            for (int i = 0; i < results.length(); i++) {
                HashMap<String, String> stockMap = new HashMap<>();
                JSONObject resultObj = (JSONObject) results.get(i);
                if ((resultObj.getString("type")).equals("S")) {
                    if (!((resultObj.getString("symbol")).contains("."))) {
                        stockMap.put("Symbol", resultObj.getString("symbol"));
                        stockMap.put("Name", resultObj.getString("name"));
                        stockMap.put("Type", resultObj.getString("type"));
                        resultList.add(stockMap);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return resultList;
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
        running = false;
        Log.d(TAG, "onPostExecute: AsyncTask terminated.");
        super.onPostExecute(hashMaps);
    }
}
