package com.clikader.stockwatch;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class AsyncLoadStockDetail extends AsyncTask<String, Void, HashMap<String, String>> {
    public static boolean running = false;
    private MainActivity mainActivity;
    private HashMap<String, String> stockDetail = new HashMap<>();
    private final String urlFront = "https://api.iextrading.com/1.0/stock/";
    private final String urlEnd = "/quote";

    private static final String TAG = "AsyncLoadStockDetail";

    public AsyncLoadStockDetail(MainActivity ma) {mainActivity = ma;}

    @Override
    protected HashMap<String, String> doInBackground(String... strings) {
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(urlFront + strings[0] + urlEnd);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (conn.getResponseCode() == 404) {
                return null;
            }

            Log.d(TAG, "doInBackground: response code:" + conn.getResponseCode());

            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            stockDetail = parseJson(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return stockDetail;
    }

    public HashMap<String, String> parseJson(String s) {
        HashMap<String, String> detailResult = new HashMap<>();

        try {
            JSONObject details = new JSONObject(s);
            detailResult.put("Symbol", details.getString("symbol"));
            detailResult.put("LastPrice", details.getString("latestPrice"));
            detailResult.put("Change", details.getString("change"));
            detailResult.put("Percent", details.getString("changePercent"));
        } catch (Exception e) {
            e.printStackTrace();;
        }
        return detailResult;
    }

    @Override
    protected void onPostExecute(HashMap<String, String> stringStringHashMap) {
        running = false;
        Log.d(TAG, "onPostExecute: Async details loading terminated.");
        super.onPostExecute(stringStringHashMap);
    }
}
