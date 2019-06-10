package com.clikader.multinotes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener{

    private static final String TAG = "MainActivity";

    public static List<Note> noteList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NotesAdapter nAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        loadFile();
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        nAdapter = new NotesAdapter(noteList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(nAdapter);
        for (Note no : noteList) {
            Log.d(TAG, "Note read: " + no.toString() + "!!!!!!!!!!!!");
        }
        super.onResume();
    }

    public void loadFile() {
        if (AsyncLoad.running) {
            Toast.makeText(this, "Wait for Async loading to be done", Toast.LENGTH_SHORT).show();
            return;
        }

        AsyncLoad.running = true;
        try {
            noteList = new AsyncLoad(this).execute().get();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void setList(List<Note> nList) {
        noteList = nList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAbout:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            case R.id.menuNew:
                Intent editIntent = new Intent(this, EditActivity.class);
                editIntent.putExtra("selected_note", -1);
                startActivity(editIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Intent editIntent = new Intent(this, EditActivity.class);
        editIntent.putExtra("selected_note", pos);
        startActivity(editIntent);
    }

    @Override
    public boolean onLongClick(View view) {
        final int pos = recyclerView.getChildLayoutPosition(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_warning_black_24dp);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                noteList.remove(pos);
                nAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "onClick: Usered selected cancel.");
            }
        });

        builder.setMessage("Do you want to delete this note?");
        builder.setTitle("Delete Note");
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    @Override
    protected void onStop() {
        saveNotes();
        super.onStop();
    }

    public void saveNotes() {
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.filename), Context.MODE_PRIVATE);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.enconding)));
            writer.setIndent("  ");
            writer.beginArray();
            for (Note note : noteList) {
                writer.beginObject();
                writer.name("title").value(note.getTitle());
                writer.name("content").value(note.getContent());
                writer.name("lastModify").value(note.getLastmodify());
                writer.endObject();
            }
            writer.endArray();
            writer.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
