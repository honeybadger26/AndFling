package com.example.andfling.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.andfling.AppDatabase;
import com.example.andfling.MainActivity;
import com.example.andfling.Message;
import com.example.andfling.R;
import com.example.andfling.Server;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

public class ChatFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getSupportActionBar().setSubtitle("Server is starting...");

        view.findViewById(R.id.sendButton).setOnClickListener(this::sendMessage);

        setHasOptionsMenu(true);

        final Observer<List<Message>> messagesClientObserver = this::setMessages;
        AppDatabase db = mainActivity.getDb();
        db.messageDao().getAll().observe(getViewLifecycleOwner(), messagesClientObserver);

        setAddressLabel(view);
    }


    private void setAddressLabel(View view) {
        MainActivity mainActivity = (MainActivity) getActivity();
        ActionBar toolbar = mainActivity.getSupportActionBar();
        Server server = mainActivity.getServer();

        try {
            server.startServer();
        } catch (IOException e) {
            toolbar.setSubtitle("Server could not be started");
            return;
        }

        String ipAddress;
        try {
            ipAddress = server.getIPAddress(mainActivity.getApplicationContext());
        } catch (Exception e) {
            toolbar.setSubtitle("Wi-Fi is off");
            return;
        }

        toolbar.setSubtitle("Running at " + ipAddress + ":" + Server.PORT);
    }


    private void setMessages(List<Message> messages) {
        LinearLayout messagesLayout = getView().findViewById(R.id.messagesLayout);
        messagesLayout.removeAllViews();

        for (Message m : messages) {
            TextView textView = new TextView(getActivity());
            textView.setText(m.contents);
            messagesLayout.addView(textView);
        }
    }

    public void sendMessage(View view) {
        EditText messageField = getView().findViewById(R.id.message);
        String msgString = messageField.getText().toString();
        AppDatabase db = ((MainActivity) getActivity()).getDb();

        Message newMsg = new Message();
        newMsg.date = "now";
        newMsg.contents = msgString;
        Executors.newSingleThreadExecutor().execute(() -> db.messageDao().insertAll(newMsg));

        messageField.setText("");
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.chat_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        AppDatabase db = ((MainActivity) getActivity()).getDb();
        switch (item.getItemId()) {
            case R.id.action_delete_messages:
                Executors.newSingleThreadExecutor().execute(() -> db.messageDao().deleteAll());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}