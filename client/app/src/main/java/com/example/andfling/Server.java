package com.example.andfling;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.io.IOException;
import java.util.List;

import fi.iki.elonen.NanoHTTPD;

public class Server extends NanoHTTPD {
    public static final int PORT = 8080;
    private String body = "";

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

    public void startServer() throws IOException {
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String pageHTML = "<html><body>" + this.body + "</body></html>";
        return newFixedLengthResponse(pageHTML);
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
