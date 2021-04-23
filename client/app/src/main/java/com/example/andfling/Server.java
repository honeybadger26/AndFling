package com.example.andfling;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.example.andfling.database.AppDatabase;
import com.example.andfling.database.Message;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();

        if (Method.POST.equals(method)) {
            Map<String, String> request = new HashMap<String, String>();
            try {
                session.parseBody(request);
                Map<String, List<String>> params = session.getParameters();
                Message msg = new Message();
                msg.date = "now";
                msg.contents  = params.get("message").get(0);
                msg.roomId = 1;
                db.messageDao().insertAll(msg);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ResponseException e) {
                e.printStackTrace();
            }
        }

        String formComponent =
            "<form action='' method='post'>" +
            "   <input type='text' name='message'>" +
            "   <input type='submit' name='submit' value='Send'>" +
            "</form>";

        return newFixedLengthResponse(
      "<html><body>" +
                body +
                formComponent +
            "</body></html>"
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
