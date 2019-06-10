package com.clikader.multinotes;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class AsyncLoad extends AsyncTask<Void, Void, List<Note>> {

    public static boolean running = false;
    private MainActivity mainActivity;
    private static final String TAG = "AsyncLoad";

    public AsyncLoad(MainActivity ma) {
        mainActivity = ma;
    }


    @Override
    protected List<Note> doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground: Start to read the json file and read the user data.");
        List<Note> jsonData = new ArrayList<>();
        try {
            InputStream is = mainActivity.getApplicationContext().openFileInput(mainActivity.getString(R.string.filename));
            JsonReader reader = new JsonReader(new InputStreamReader(is, mainActivity.getString(R.string.enconding)));

            reader.beginArray();
            while(reader.hasNext()) {
                jsonData.add(readResult(reader));
            }
            reader.endArray();
        } catch (FileNotFoundException e) {
            Toast.makeText(mainActivity, "No history file found", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonData;
    }

    private Note readResult(JsonReader reader) throws IOException {
        String titleGot = null,
                lastModifyGot = null,
                contentGot = null;

        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("title")) {
                titleGot = reader.nextString();
            } else if (name.equals("content")) {
                contentGot = reader.nextString();
            } else if (name.equals("lastModify")) {
                lastModifyGot = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        Note constructedNote = new Note(titleGot, lastModifyGot, contentGot);
        return constructedNote;
    }

    @Override
    protected void onPostExecute(List<Note> notes) {
        super.onPostExecute(notes);
        mainActivity.setList(notes);
        running = false;
        Log.d(TAG, "onPostExecute: Asyncload terminating.");
    }
}
