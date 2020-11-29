package com.example.andfling;

import android.content.Context;
import android.widget.TextView;

import com.example.andfling.database.Message;

public class MessageTextView extends TextView {
    private Message message;

    public MessageTextView(Context context, Message message) {
        super(context);
        this.message = message;
        setText(message.contents);
        setTextIsSelectable(true);
    }

    public Message getMessage() {
        return message;
    }
}
