package com.example.andfling;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.text.format.Formatter;

import androidx.annotation.RequiresApi;

import com.example.andfling.database.AppDatabase;
import com.example.andfling.database.Message;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fi.iki.elonen.NanoHTTPD;

public class Server extends NanoHTTPD {
    public static final int PORT = 8080;

    private AppDatabase db;
    private String body = "";

    public void setDb(AppDatabase db) {
        this.db = db;
    }

    public Server() {
        super(Server.PORT);
    }

    public void setMessages(List<Message> messages) {
        StringBuilder newBody = new StringBuilder();
        for  (Message m : messages) {
            newBody.append("<div>").append(m.contents).append("</div>");
        }
        body = newBody.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();

        if (Method.POST.equals(method)) {
            Map<String, String> request = new HashMap<>();

            try {
                session.parseBody(request);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ResponseException e) {
                e.printStackTrace();
            }

            Map<String, List<String>> params = session.getParameters();
            Message msg = new Message();
            msg.date = "now";
            msg.roomId = 1;

            if (params.containsKey("message")) {
                msg.contents = params.get("message").get(0);
            } else if (params.containsKey("file")) {
                msg.contents  = params.get("file").get(0);

                File inFile = new File(Objects.requireNonNull(request.get("file")));
                String downloadsDir = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                String outFilePath = downloadsDir + "/" + new Date().getTime() + "_" + msg.contents;
                File outFile = new File(outFilePath);
                try {
                    Files.copy(inFile.toPath(), outFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                msg.contents = "INVALID MESSAGE";
            }

            db.messageDao().insertAll(msg);
        }

        String formComponent =
            "<div>" +
            "  <form action='' method='post'>" +
            "     <input type='text' name='message'>" +
            "     <input type='submit' name='submit' value='Send'>" +
            "  </form>" +
            "</div>" +
            "<div>" +
            "  <form action='' method='post' enctype='multipart/form-data'>" +
            "     <input type='file' name='file'>" +
            "     <input type='submit' name='submit' value='Upload'>" +
            "  </form>" +
            "</div>";

        return newFixedLengthResponse(
      "<html>" +
            "  <body>" + body + formComponent + "</body>" +
            "</html>"
        );
    }

    public String getIPAddress(Context context) throws Exception {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
           throw new Exception();
        }

        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }
}
