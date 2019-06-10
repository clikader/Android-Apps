package com.clikader.temperatureconverter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private RadioGroup tempradio;
    private EditText userText;
    private TextView output;
    private TextView historyPannel;
    DecimalFormat df = new DecimalFormat("#.0");

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userText = (EditText) findViewById(R.id.userInput);
        output = (TextView) findViewById(R.id.result);
        historyPannel = (TextView) findViewById(R.id.history);
    }

    public void radioReminder(View v) {
        String textSelected = ((RadioButton) v).getText().toString();
        Toast.makeText(this, "You selected: " + textSelected, Toast.LENGTH_SHORT).show();
    }

    public void convertTemp(View v) {
        tempradio = (RadioGroup) findViewById(R.id.tempgroup);
        int whichIDSelected = tempradio.getCheckedRadioButtonId();
        String input = userText.getText().toString();
        String olderHistory = historyPannel.getText().toString();

        if(!input.trim().isEmpty()) {
            double numberInput = Double.parseDouble(input);
            if (whichIDSelected == R.id.ftc) {
                double tempResult = (numberInput - 32.0) * 5.0 / 9.0;
                String resultStr = df.format(tempResult);
                output.setText(resultStr);
                String newAction = "F to C: " + input + " -> " + resultStr + " \n";
                String newHistory = newAction + olderHistory;
                historyPannel.setText(newHistory);
                userText.setText("");
                userText.setHint("Enter value here");
            }
            else {
                double tempResult = (numberInput * 9.0 / 5.0) + 32.0;
                String resultStr = df.format(tempResult);
                output.setText(resultStr);
                String newAction = "C to F: " + input + " -> " + resultStr + " \n";
                String newHistory = newAction + olderHistory;
                historyPannel.setText(newHistory);
                userText.setText("");
                userText.setHint("Enter value here");
            }
        }
        else {
            // give a warning if user input is empty.
            Toast.makeText(this, "Error: You have to enter some value to calculate the result.", Toast.LENGTH_LONG).show();
        }
    }

    public void clearHis(View v) {
        historyPannel.setText("");

        if((historyPannel.getText().toString()) == "") {
            Toast.makeText(this, "History cleared.", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Error: Something went wrong, please try again.", Toast.LENGTH_LONG).show();
        }
    }
}

