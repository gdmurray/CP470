package com.example.androidassignments;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatWindow extends AppCompatActivity {
    public final static String ACTIVITY_NAME = "ChatWindow";
    ArrayList<String> messages;
    ListView listView;
    Button sendButton;
    ChatAdapter messageAdapter;
    ChatDatabaseHelper dbHelper;
    SQLiteDatabase db;
    Boolean dualPane;
    Cursor listCursor;
    MessageFragment mfg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        dbHelper = new ChatDatabaseHelper(this);
        listView = (ListView) findViewById(R.id.ListView);
        sendButton = (Button) findViewById(R.id.SendButton);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        dualPane =  (frameLayout != null && frameLayout.getVisibility() == View.VISIBLE);
        Log.d(ACTIVITY_NAME, "DUALPANE: " + dualPane);
        final EditText messageField = (EditText) findViewById(R.id.EditMessageText);
        messages = new ArrayList<String>();
        db = dbHelper.getWritableDatabase();
        final String query = "SELECT * from " + ChatDatabaseHelper.TABLE_NAME + " ORDER BY id ASC";
        Cursor cursor = db.rawQuery(query, null);
        listCursor = db.rawQuery(query, null);
        String msg;
        while (cursor.moveToNext()){
            msg = cursor.getString(cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE));
            messages.add(msg);
            Log.i(ACTIVITY_NAME, "SQL Message: " + msg);
            Log.i(ACTIVITY_NAME, "Cursors Column Count: " + cursor.getColumnCount() );
        }
        cursor.close();
        messageAdapter =new ChatAdapter( this );
        listView.setAdapter (messageAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageField.getEditableText().toString();
                Log.i(ACTIVITY_NAME, "Added Message: " + message);
                ContentValues values = new ContentValues();
                values.put(ChatDatabaseHelper.KEY_MESSAGE, message);
                db.insert(ChatDatabaseHelper.TABLE_NAME, null, values);
                messageAdapter.notifyDataSetChanged();
                messages.add(message);
                messageField.setText("");
                listCursor = db.rawQuery(query, null);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putLong("messageId", id);
                bundle.putInt("messagePos", position);
                bundle.putString("message", messages.get(position));
                if(dualPane){
                    listView.setItemChecked(position, true);
                    MessageFragment fragment = (MessageFragment) getSupportFragmentManager().findFragmentById(R.id.message_fragment);
                    fragment = new MessageFragment(ChatWindow.this);
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frame_layout, fragment);
                    mfg = fragment;
                    ft.commit();
                }else {
                    Intent intent = new Intent(ChatWindow.this, MessageDetails.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 10);
                }
            }
        });

    }
    public void deleteMessage(long id, int messagePos){
        Log.d(ACTIVITY_NAME, "DELETING MESSAGE: " + id);
        db.execSQL("DELETE FROM " + ChatDatabaseHelper.TABLE_NAME + " WHERE " + ChatDatabaseHelper.KEY_ID +"="+id);
        messages.remove(messagePos);
        messageAdapter.notifyDataSetChanged();
    }
    public void removeFragment(){
        getSupportFragmentManager().beginTransaction().remove(mfg).commit();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(resultCode == 42){
            long messageId = data.getLongExtra("messageId", 0);
            int messagePos = data.getIntExtra("messagePos", 0);
            Log.d(ACTIVITY_NAME, "DELETE ID: " + messageId);
            this.deleteMessage(messageId, messagePos);
        }
    }

    private class ChatAdapter extends ArrayAdapter{
        public ChatAdapter(Context ctx){
            super(ctx, 0);
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public String getItem(int position) {
            return messages.get(position);
        }

        @Override
        public long getItemId(int position) {
            listCursor.moveToPosition(position);
            int msgId = listCursor.getInt(listCursor.getColumnIndex(ChatDatabaseHelper.KEY_ID));
            Log.i(ACTIVITY_NAME, "Position: " + position +  " Message ID: " + msgId );
            return msgId;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //return super.getView(position, convertView, parent);
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
            View result = null;
            if(position % 2 == 0){
                result = inflater.inflate(R.layout.chat_row_incoming, null);
            }else{
                result = inflater.inflate(R.layout.chat_row_outgoing, null);
            }

            TextView message = (TextView)result.findViewById(R.id.message_text);
            message.setText(   getItem(position)  );
            return result;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listCursor.close();
        db.close();
    }
}
