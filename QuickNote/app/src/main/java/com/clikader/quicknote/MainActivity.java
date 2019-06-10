package com.clikader.quicknote;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import android.util.JsonReader;
import android.util.JsonWriter;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private EditText noteContent;
    private TextView lastDate;
    private Quicknote noteClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteContent = (EditText) findViewById(R.id.userNote);
        lastDate = (TextView) findViewById(R.id.updateTime);

        noteContent.setMovementMethod(new ScrollingMovementMethod());
        noteContent.setTextIsSelectable(true);
    }

    @Override
    protected void onResume() {
        noteClass = loadFile();
        if(noteClass != null) {
            noteContent.setText(noteClass.getSavedNote());

            if (noteClass.getSavedTime() == null){
                lastDate.setText(DateFormat.getDateTimeInstance().format(new Date()));
            } else {
                lastDate.setText(noteClass.getSavedTime());
            }
        }

        super.onResume();
    }

    private Quicknote loadFile() {
        noteClass = new Quicknote();
        try {
            InputStream is = getApplicationContext().openFileInput(getString(R.string.file_name));
            JsonReader reader = new JsonReader(new InputStreamReader(is, getString(R.string.encoding)));

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("savedTime")) {
                    noteClass.setSavedTime(reader.nextString());
                } else if (name.equals("savedNote")) {
                    noteClass.setSavedNote(reader.nextString());
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return noteClass;
    }

    @Override
    protected void onPause() {
        noteClass.setSavedNote(noteContent.getText().toString());
        noteClass.setSavedTime(DateFormat.getDateTimeInstance().format(new Date()));
        super.onPause();
    }

    @Override
    protected void onStop() {
        saveQuicknote();
        super.onStop();
    }

    private void saveQuicknote() {
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.encoding)));
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("savedNote").value(noteClass.getSavedNote());
            writer.name("savedTime").value(noteClass.getSavedTime());
            writer.endObject();
            writer.close();

            //Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    /*
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("TIME", lastDate.getText().toString());
        outState.putString("NOTES", noteContent.getText().toString());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        noteContent.setText(savedInstanceState.getString("NOTES"));
        lastDate.setText(savedInstanceState.getString("TIME"));
    }
    */
}
