package com.example.androidassignments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class MessageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public final static String ACTIVITY_NAME = "MessageFragment";
    private static final String MESSAGE_ID = "messageId";
    private static final String MESSAGE_POS = "messagePos";
    private static final String MESSAGE_TEXT = "message";
    ChatWindow chatWindow = null;

    // TODO: Rename and change types of parameters
    private long messageId;
    private String messageText;
    private int messagePos;

    private OnFragmentInteractionListener mListener;

    public MessageFragment(){

    }
    public MessageFragment(ChatWindow chatWindow) {
        this.chatWindow = chatWindow;
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessageFragment.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            messageId    = getArguments().getLong(MESSAGE_ID);
            messageText = getArguments().getString(MESSAGE_TEXT);
            messagePos = getArguments().getInt(MESSAGE_POS);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView messageIdView = (TextView) view.findViewById(R.id.message_id);
        TextView messageTextView = (TextView) view.findViewById(R.id.message_text);
        messageIdView.setText(String.valueOf(messageId));
        messageTextView.setText(messageText);
        Button deleteMessage = (Button) view.findViewById(R.id.delete_message);
        deleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(ACTIVITY_NAME, "DELETE BUTTON CLICKED");
                chatWindow.deleteMessage(messageId, messagePos);
                chatWindow.removeFragment();
            }
        });

    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
