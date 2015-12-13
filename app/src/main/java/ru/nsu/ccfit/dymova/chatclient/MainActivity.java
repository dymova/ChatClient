package ru.nsu.ccfit.dymova.chatclient;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    public static String UPDATE_ACTION = "ru.nsu.ccfit.dymova.chatclient.update_messages";

    private ArrayList<Message> messages;
    private UpdateReceiver br;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initFab();

        messages = new ArrayList<>();
        messages.add(new Message("user1", "heeelooooooo!", -1));

        ListView listView = (ListView) findViewById(R.id.list_messages);
        adapter = new MessageAdapter(this, R.layout.list_record_message , messages);
        listView.setAdapter(adapter);


        initUpdatesService();

    }

    private void initUpdatesService() {
        br = new UpdateReceiver(this);
        IntentFilter intFilter = new IntentFilter(UPDATE_ACTION);
        registerReceiver(br, intFilter);
        startService(new Intent(this, ChatService.class));

        final Context c = this;
        Toast t = Toast.makeText(getApplicationContext(), "true", Toast.LENGTH_SHORT);
        t.show();


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    int lastId = messages.isEmpty() ? 0 : messages.get(messages.size() - 1).getId();
                    startService(new Intent(c, ChatService.class).putExtra("id", lastId));
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NewMessageActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateMessage(ArrayList<Message> newMessages) {
        messages.addAll(newMessages);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
    }
}
