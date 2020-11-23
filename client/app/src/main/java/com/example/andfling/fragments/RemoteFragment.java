package com.example.andfling.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

        ActionBar toolbar = ((MainActivity) getActivity()).getSupportActionBar();
        toolbar.setSubtitle(null);

        view.findViewById(R.id.checkButton).setOnClickListener(this::checkServer);
        view.findViewById(R.id.playButton).setOnClickListener(this::sendPlayPause);
        view.findViewById(R.id.spaceButton).setOnClickListener(this::sendSpace);
        view.findViewById(R.id.leftButton).setOnClickListener(this::sendLeft);
        view.findViewById(R.id.rightButton).setOnClickListener(this::sendRight);
        view.findViewById(R.id.toggleMediaButton).setOnClickListener(this::sendMediaToggle);
    }

    private String queryBuilder(String endpoint) {
        EditText addressField = (EditText) getView().findViewById(R.id.addressField);
        return "http://" + addressField.getText().toString() + ":" + PORT + endpoint;
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

    public void sendSpace(View view) {
        makeRequest(view, "/key/space");
    }

    public void sendLeft(View view) {
        makeRequest(view, "/key/left");
    }

    public void sendRight(View view) {
        makeRequest(view, "/key/right");
    }

    public void sendMediaToggle(View view) {
        makeRequest(view, "/action/toggle_media");
    }
}