package com.clikader.multinotes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditActivity extends AppCompatActivity {

    private static final String TAG = "EditActivity";

    private int num = -1;
    private Note n;
    private EditText titleText;
    private EditText contentText;
    private List<Note> editNList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        num = (int) intent.getSerializableExtra("selected_note");

        titleText = (EditText) findViewById(R.id.editTitle);
        contentText = (EditText) findViewById(R.id.editContent);

        contentText.setMovementMethod(new ScrollingMovementMethod());
        contentText.setTextIsSelectable(true);
    }

    @Override
    protected void onResume() {
        editNList = MainActivity.noteList;
        if (num >= 0) {
            n = editNList.get(num);
            if (n != null) {
                titleText.setText(n.getTitle());
                contentText.setText(n.getContent());
            }
        } else if (num == -1) {
            n = new Note();
        } else {
            Log.d(TAG, "onResume: ************ PUTEXTRA ERROR HERE *************");
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editSave:
                saveNote();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_warning_black_24dp);

        builder.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveNote();
            }
        });

        builder.setNegativeButton("don't save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        builder.setMessage("Do you want to save your note before exit?");
        builder.setTitle("Note has not been saved");
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();

        if ((titleText.getText().length() != 0) || (contentText.getText().length() != 0)) {
            if ((n.getTitle().equals(titleText.getText().toString())) && (n.getContent().equals(contentText.getText().toString()))){
                finish();
            } else {
                dialog.show();
            }
        } else {
            finish();
        }
    }

    public void saveNote() {
        if (titleText.getText().length() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_warning_black_24dp);
            builder.setNegativeButton("Understand", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.setMessage("You have to give a title to this note!");
            builder.setTitle("Empty Title");
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            if (num >= 0) {
                editNList.remove(num);
                n.setTitle(titleText.getText().toString());
                n.setContent(contentText.getText().toString());
                n.setLastmodify(DateFormat.getDateTimeInstance().format(new Date()));
                editNList.add(0, n);
            } else if (num == -1) {
                n.setTitle(titleText.getText().toString());
                n.setContent(contentText.getText().toString());
                n.setLastmodify(DateFormat.getDateTimeInstance().format(new Date()));
                editNList.add(0, n);
            } else {
                Log.d(TAG, "dialog save: ************* SAVE ERROR ****************");
            }
            try {
                FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.filename), Context.MODE_PRIVATE);
                JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.enconding)));
                writer.setIndent("  ");
                writer.beginArray();
                for (Note note : editNList) {
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
            finish();
        }
    }
}
