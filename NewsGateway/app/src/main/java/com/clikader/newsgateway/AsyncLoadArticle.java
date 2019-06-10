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

public class AsyncLoadArticle extends AsyncTask<String, Void, ArrayList<Article>> {

    public static boolean running = false;
    private MainActivity mainActivity;
    private static final String TAG = "AsyncLoadArticle";
    private final String myAPI = "a603a4e59a0b47b8aafbb6b9477cf511";
    private final String firstPartURL = "https://newsapi.org/v1/articles?source=";
    private final String lastPartURL = "&apiKey=" + myAPI;
    private ArrayList<Article> articleData = new ArrayList<>();

    public AsyncLoadArticle(MainActivity ma) {mainActivity = ma;}

    @Override
    protected ArrayList<Article> doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: Reading articles.");
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(firstPartURL + strings[0] + lastPartURL);
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

            articleData = parseJson(sb.toString());
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return articleData;
    }

    public ArrayList<Article> parseJson(String s) {
        ArrayList<Article> resultList = new ArrayList<>();
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray jArrMain =jObjMain.getJSONArray("articles");

            // get each article
            for (int i = 0; i < jArrMain.length(); i++) {
                JSONObject articleObj = (JSONObject) jArrMain.get(i);

                String author = articleObj.getString("author");
                String title = articleObj.getString("title");
                String des = articleObj.getString("description");
                String imgUrl = articleObj.getString("urlToImage");
                String date = articleObj.getString("publishedAt");
                String articleUrl = articleObj.getString("url");
                
                Article newArt = new Article(author, title, des, articleUrl, imgUrl, date);
                resultList.add(newArt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    @Override
    protected void onPostExecute(ArrayList<Article> articles) {
        running = false;
        Log.d(TAG, "onPostExecute: Article Detail Loading Terminated.");
        super.onPostExecute(articles);
    }
}
