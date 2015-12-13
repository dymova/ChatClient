package ru.nsu.ccfit.dymova.chatclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {
    public MessageAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_record_message, null);
        }
        Message msg = getItem(position);

        TextView contentTextView = (TextView) convertView.findViewById(R.id.text_view_content);

        contentTextView.setText(msg.getAuthor() + ":" + msg.getText());


        return contentTextView;
    }
}
