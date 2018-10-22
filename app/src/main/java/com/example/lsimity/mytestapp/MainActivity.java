package com.example.lsimity.mytestapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.time.LocalDate;


public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private Intent timeIntent;

    public Intent getTimeIntent() {
        return timeIntent;
    }

    public void setTimeIntent(Intent theIntent) {
        this.timeIntent = theIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void setTime(View view) {
        setTimeIntent(new Intent(this, DisplayMessageActivity.class));
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        timeIntent.putExtra(EXTRA_MESSAGE, message);
        startActivity(timeIntent);
    }

    public void getTime(View view) {
        LocalDate now = LocalDate.now();

        TextView myView = (TextView) findViewById(R.id.timeDisplay);

        myView.setText(now.toString());

    }
}
