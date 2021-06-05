package com.example.andfling.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.andfling.MessageTextView;
import com.example.andfling.database.AppDatabase;
import com.example.andfling.MainActivity;
import com.example.andfling.database.Message;
import com.example.andfling.R;
import com.example.andfling.Server;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

import fi.iki.elonen.NanoHTTPD;


public class ChatFragment extends Fragment {
    // Context Actions
    private static final String CA_COPY = "Copy";
    private static final String CA_DELETE = "Delete";

    // ActionBar Messages
    private static final String AM_STARTING = "Server is starting...";
    private static final String AM_FAILEDTOSTART = "Server failed to start";
    private static final String AM_WIFIDISCONNECTED = "Wi-Fi disconnected";

    private AppDatabase db;
    private Message selectedMessage;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getSupportActionBar().setSubtitle(AM_STARTING);

        ImageButton sendBtn = view.findViewById(R.id.sendButton);
        view.findViewById(R.id.message).setOnFocusChangeListener((v, hasFocus) -> {
            this.messageFieldFocusChanged(view, hasFocus);
        });
        sendBtn.setOnClickListener(this::sendButtonPressed);
        view.findViewById(R.id.clipboardButton)
            .setOnClickListener(this::clipboardButtonPressed);
        sendBtn.setVisibility(View.GONE);

        setHasOptionsMenu(true);

        final Observer<List<Message>> messagesClientObserver = this::setMessages;
        db = mainActivity.getDb();

        // FIXME: This currently grabs messages from room with id=0 only. Need to save the previous
        //       room to settings?
        db.messageDao().getAll(1).observe(getViewLifecycleOwner(), messagesClientObserver);

        startServer();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.chat_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_messages:
                Executors.newSingleThreadExecutor().execute(() -> db.messageDao().deleteAll());
                return true;
            case R.id.action_restart_server:
                startServer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(
        @NonNull ContextMenu menu,
        @NonNull View v,
        @Nullable ContextMenu.ContextMenuInfo menuInfo
    ) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(CA_COPY);
        menu.add(CA_DELETE);
        selectedMessage = ((MessageTextView) v).getMessage();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch ((String) item.getTitle()) {
            case CA_COPY:
                ClipboardManager clipboard =
                    (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("CopiedText", selectedMessage.contents);
                clipboard.setPrimaryClip(clip);
                break;
            case CA_DELETE:
                // Message messageToDelete = selectedMessage;
                Executors.newSingleThreadExecutor().execute(() -> {
                    db.messageDao().delete(selectedMessage);
                });
                break;
            default:
                break;
        }
        selectedMessage = null;
        return true;
    }

    private void startServer() {
        MainActivity mainActivity = (MainActivity) getActivity();
        ActionBar toolbar = mainActivity.getSupportActionBar();
        Server server = mainActivity.getServer();

        try {
            server.stop();
            server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        }
        catch (IOException e) {
            toolbar.setSubtitle(AM_FAILEDTOSTART);
            return;
        }

        String ipAddress;
        try {
            ipAddress = server.getIPAddress(mainActivity.getApplicationContext());
        } catch (Exception e) {
            toolbar.setSubtitle(AM_WIFIDISCONNECTED);
            return;
        }

        toolbar.setSubtitle(ipAddress + ":" + Server.PORT);
    }


    private void setMessages(List<Message> messages) {
        LinearLayout messagesLayout = getView().findViewById(R.id.messagesLayout);
        messagesLayout.removeAllViews();

        for (Message m : messages) {
            MessageTextView textView = new MessageTextView(getActivity(), m);
            registerForContextMenu(textView);
            messagesLayout.addView(textView);
        }
    }

    private void messageFieldFocusChanged(View view, boolean hasFocus) {
        ImageButton sendBtn = view.findViewById(R.id.sendButton);
        ImageButton clipboardBtn = view.findViewById(R.id.clipboardButton);

        if (hasFocus) {
            sendBtn.setVisibility(View.VISIBLE);
            clipboardBtn.setVisibility(View.GONE);
        } else {
            sendBtn.setVisibility(View.GONE);
            clipboardBtn.setVisibility(View.VISIBLE);
        }
    }

    private void createMessage(String contents) {
        Message newMsg = new Message();
        newMsg.date = "now";
        newMsg.contents = contents;
        newMsg.roomId = 1;
        Executors.newSingleThreadExecutor().execute(() -> db.messageDao().insertAll(newMsg));
    }

    public void sendButtonPressed(View view) {
        EditText messageField = getView().findViewById(R.id.message);
        String msgString = messageField.getText().toString();

        if (msgString.equals("")) return;
        messageField.setText("");
        createMessage(msgString);
    }

    public void clipboardButtonPressed(View vew) {
        ClipboardManager clipboard =
            (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
        createMessage(item.getText().toString());
    }
}