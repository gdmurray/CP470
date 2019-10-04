package com.example.androidassignments;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    protected static final String ACTIVITY_NAME = "StartActivity";
    Button segueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        segueButton = (Button) findViewById(R.id.button);
        segueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, ListItemsActivity.class);
                startActivityForResult(intent, 10);
            }
        });

        Button startChat = (Button) findViewById(R.id.button3);
        startChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(ACTIVITY_NAME, "User clicked Start Chat");
                Intent intent = new Intent(StartActivity.this, ChatWindow.class );
                startActivityForResult(intent, 10);
            }
        });

        Log.i(ACTIVITY_NAME, "In onCreate()");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            Log.i(ACTIVITY_NAME, "Returned to StartActivity.onActivityResult");
        }

        if (resultCode == Activity.RESULT_OK) {
            try {
                String messagePassed = data.getStringExtra("Response");
                CharSequence text = "ListItemsActivity passed: " + messagePassed;
                Toast toast = Toast.makeText(StartActivity.this, text, Toast.LENGTH_LONG);
                toast.show();
            } catch (NullPointerException e) {
                Log.i(ACTIVITY_NAME, "Could not find data: 'Response'");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(ACTIVITY_NAME, "In onResume()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(ACTIVITY_NAME, "In onStart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(ACTIVITY_NAME, "In onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(ACTIVITY_NAME, "In onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(ACTIVITY_NAME, "In onDestroy()");
    }
}
