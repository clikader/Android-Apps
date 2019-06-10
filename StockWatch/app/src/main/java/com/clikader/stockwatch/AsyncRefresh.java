package com.clikader.stockwatch;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class AsyncRefresh extends AsyncTask<Void, Void, Integer> {
    public static boolean running = false;
    private MainActivity mainActivity;
    private final String urlFront = "https://api.iextrading.com/1.0/stock/";
    private final String urlEnd = "/quote";

    public AsyncRefresh(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        ArrayList<Stock> list = mainActivity.stockList;

        for (int i = 0; i < mainActivity.stockList.size(); i++) {
            StringBuilder sb = new StringBuilder();
            HashMap<String, String> stockDetail = new HashMap<>();
            try {
                URL url = new URL(urlFront + mainActivity.stockList.get(i).getCode() + urlEnd);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn.getResponseCode() == 404) {
                    return 0;
                }

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
                return -1;
            }
            String snewPrice = stockDetail.get("LastPrice");
            double newPrice = Double.parseDouble(snewPrice);
            String snewChange = stockDetail.get("Change");
            double newChange = Double.parseDouble(snewChange);
            String snewPercent = stockDetail.get("Percent");
            double newPercent = Double.parseDouble(snewPercent) * 100;
            String percentNumStr = String.format("%.2f", newPercent);
            double percentNum = Double.parseDouble(percentNumStr);

            mainActivity.stockList.get(i).setLastPrice(newPrice);
            mainActivity.stockList.get(i).setChange(newChange);
            mainActivity.stockList.get(i).setPercentage(percentNum);
        }
        return 1;
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
    protected void onPostExecute(Integer integer) {
        Toast.makeText(mainActivity, "The data has been updated.", Toast.LENGTH_SHORT).show();
        running = false;
        super.onPostExecute(integer);
    }
}
