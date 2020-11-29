package com.example.andfling.fragments;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        mainActivity.getSupportActionBar().setSubtitle("Server is starting...");

        view.findViewById(R.id.sendButton).setOnClickListener(this::sendMessage);

        setHasOptionsMenu(true);

        final Observer<List<Message>> messagesClientObserver = this::setMessages;
        db = mainActivity.getDb();
        db.messageDao().getAll().observe(getViewLifecycleOwner(), messagesClientObserver);

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
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add("Delete");
        selectedMessage = ((MessageTextView) v).getMessage();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch ((String) item.getTitle()) {
            case "Delete":
                Message messageToDelete = selectedMessage;
                Executors.newSingleThreadExecutor().execute(() -> db.messageDao().delete(messageToDelete));
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
            toolbar.setSubtitle("Server failed to started");
            return;
        }

        String ipAddress;
        try {
            ipAddress = server.getIPAddress(mainActivity.getApplicationContext());
        } catch (Exception e) {
            toolbar.setSubtitle("Wi-Fi disconnected");
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

    public void sendMessage(View view) {
        EditText messageField = getView().findViewById(R.id.message);
        String msgString = messageField.getText().toString();

        if (msgString.equals("")) return;

        Message newMsg = new Message();
        newMsg.date = "now";
        newMsg.contents = msgString;
        Executors.newSingleThreadExecutor().execute(() -> db.messageDao().insertAll(newMsg));

        messageField.setText("");
    }
}