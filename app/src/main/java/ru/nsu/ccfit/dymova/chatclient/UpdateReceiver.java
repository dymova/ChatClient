package ru.nsu.ccfit.dymova.chatclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class UpdateReceiver extends BroadcastReceiver {

    public static final String AUTHORS = "authors";
    public static final String TEXTS = "texts";
    public static final String IDS = "ids";
    private final MainActivity mainActivity;


    public UpdateReceiver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        List<String> authors = intent.getStringArrayListExtra(AUTHORS);
        List<String> texts = intent.getStringArrayListExtra(TEXTS);
        List<Integer> ids = intent.getIntegerArrayListExtra(IDS);
        ArrayList<Message> newMessages = new ArrayList<>();
        for (int i = 0; i < authors.size(); i++) {
            newMessages.add(new Message(authors.get(i), texts.get(i), ids.get(i)));
        }

        mainActivity.updateMessage(newMessages);
    }
}
