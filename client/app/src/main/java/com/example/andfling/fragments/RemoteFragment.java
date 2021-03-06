package com.example.andfling.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.andfling.MainActivity;
import com.example.andfling.R;
import com.google.android.material.snackbar.Snackbar;

public class RemoteFragment extends Fragment {
    public static final int PORT = 4000;
    public static final String DEFAULT_SERVER_IP = "192.168.0.1";

    SharedPreferences sharedPref;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_remote, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        ActionBar toolbar = ((MainActivity) getActivity()).getSupportActionBar();
        toolbar.setSubtitle(null);

        // Set button behaviours
        view.findViewById(R.id.checkButton).setOnClickListener(this::checkServer);
        view.findViewById(R.id.playButton).setOnClickListener(this::sendPlayPause);
        view.findViewById(R.id.leftButton).setOnClickListener(this::sendLeft);
        view.findViewById(R.id.rightButton).setOnClickListener(this::sendRight);
        view.findViewById(R.id.previousTrackButton).setOnClickListener(this::sendPreviousTrack);
        view.findViewById(R.id.nextTrackButton).setOnClickListener(this::sendNextTrack);
        view.findViewById(R.id.volumeDownButton).setOnClickListener(this::sendVolumeDown);
        view.findViewById(R.id.volumeUpButton).setOnClickListener(this::sendVolumeUp);
        view.findViewById(R.id.muteButton).setOnClickListener(this::sendMute);

        EditText addressField = (EditText) view.findViewById(R.id.addressField);
        String remote_server_ip = sharedPref.getString(getString(R.string.pref_remote_server_ip), DEFAULT_SERVER_IP);

        addressField.setText(remote_server_ip);
        addressField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.pref_remote_server_ip), editable.toString());
                editor.apply();
            }
        });
    }

    private String queryBuilder(String endpoint) {
        String remote_server_ip = sharedPref.getString(getString(R.string.pref_remote_server_ip), DEFAULT_SERVER_IP);
        return "http://" + remote_server_ip + ":" + PORT + endpoint;
    }

    private void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, 3000);
        snackbar.show();
    }

    private void makeRequest(View view, String endpoint) {
        String url = queryBuilder(endpoint);
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            response -> {},
            error -> showSnackbar(view, "Failed to send request")
        );

        queue.add(stringRequest);
    }

    public void checkServer(View view) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = queryBuilder("/check");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            response -> showSnackbar(view, "Success"),
            error -> showSnackbar(view, "Failed to connect to server")
        );

        queue.add(stringRequest);
    }

    public void sendPlayPause(View view) {
        makeRequest(view, "/key/play");
    }

    public void sendLeft(View view) {
        makeRequest(view, "/key/left");
    }

    public void sendRight(View view) {
        makeRequest(view, "/key/right");
    }

    public void sendPreviousTrack(View view) {
        makeRequest(view, "/key/prevtrack");
    }

    public void sendNextTrack(View view) {
        makeRequest(view, "/key/nexttrack");
    }

    public void sendVolumeDown(View view) {
        makeRequest(view, "/key/voldown");
    }

    public void sendVolumeUp(View view) {
        makeRequest(view, "/key/volup");
    }

    public void sendMute(View view) {
        makeRequest(view, "/key/mute");
    }
}