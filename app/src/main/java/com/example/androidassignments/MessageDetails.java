package com.example.androidassignments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MessageDetails extends AppCompatActivity {
    public final static String ACTIVITY_NAME = "MessageDetails";

    MessageFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
        fragment = (MessageFragment) getSupportFragmentManager().findFragmentById(R.id.message_fragment);
        //Log.i(ACTIVITY_NAME, fragment.getArguments().toString());
        TextView messageText = (TextView) findViewById(R.id.message_text);
        TextView messageIdText = (TextView) findViewById(R.id.message_id);
        final long messageId = getIntent().getExtras().getLong("messageId");
        final int messagePos = getIntent().getExtras().getInt("messagePos");
        String messageString = getIntent().getExtras().getString("message");

        messageText.setText(messageString);
        messageIdText.setText(String.valueOf(messageId));

        Button deleteMessage = (Button) findViewById(R.id.delete_message);
        deleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra("messageId", messageId);
                result.putExtra("messagePos", messagePos);
                int resultCode = 42;
                setResult(resultCode, result);
                finish();
            }
        });
    }
}
