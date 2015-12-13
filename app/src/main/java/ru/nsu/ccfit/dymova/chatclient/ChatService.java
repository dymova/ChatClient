package ru.nsu.ccfit.dymova.chatclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatService extends Service {

    public static final String LOG_TAG = "MyService";
//    public static final String HOST = "http://localhost:8080/api/update";
//    public static final String HOST = "http://192.168.56.1:8080/api/update";
    public static final String HOST = "http://10.0.0.30:8080/api/update";
    private ExecutorService es;
    private static Persister serializer;


    @Override
    public void onCreate() {
        super.onCreate();
        Toast t = Toast.makeText(getApplicationContext(), "create", Toast.LENGTH_SHORT);
        t.show();
        Log.d(LOG_TAG, "MyService onCreate");
        es = Executors.newFixedThreadPool(1);
        serializer = new Persister();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("service", "MyService onStartCommand");
        int lastId = intent.getIntExtra("id", 0);
        es.execute(new Task(startId, lastId));
//        return super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }

    class Task implements Runnable {
        int startId;
        private int lastId;
        private ArrayList<String> authors;
        private ArrayList<String> texts;
        private ArrayList<Integer> ids;

        public Task(int startId, int lastId) {
            Log.d(LOG_TAG, "Task#" + lastId + " create");
            authors = new ArrayList<>();
            texts = new ArrayList<>();
            ids = new ArrayList<>();
        }

        public void run() {
            cleanArrays();

            Log.d(LOG_TAG, "Task# run" + startId);
//            //todo remove
//            authors.add("user1");
//            texts.add("hi! lalala");
//            ids.add(0);

            try {
                UpdateResponseMessage response = null;//                getUpdate(lastId);

                if(response != null && !response.messages.isEmpty()) {
                    for (Message  m: response.messages) {
                        Toast t = Toast.makeText(getBaseContext(), m.getText(), Toast.LENGTH_SHORT);
                        t.show();
                        authors.add(m.getAuthor());
                        texts.add(m.getText());
                        ids.add(m.getId());
                    }
                    Intent intent = new Intent(MainActivity.UPDATE_ACTION);
                    intent.putStringArrayListExtra(UpdateReceiver.AUTHORS, authors);
                    intent.putStringArrayListExtra(UpdateReceiver.TEXTS, texts);
                    intent.putIntegerArrayListExtra(UpdateReceiver.IDS, ids);
                    sendBroadcast(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            stop();
        }

        private void cleanArrays() {
            authors.clear();
            texts.clear();
            ids.clear();
        }

        private UpdateResponseMessage getUpdate(int id) throws Exception {
            URL url = new URL(HOST + "?id=" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            Log.d(LOG_TAG, "\nSending 'GET' request to URL : " + url);
            Log.d(LOG_TAG, "Response Code : " + responseCode);
            Toast t = Toast.makeText(getBaseContext(), responseCode, Toast.LENGTH_SHORT);
            t.show();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = in.readLine()) != null) {
                Log.d(LOG_TAG, line);
                response.append(line);
            }
            in.close();


            if(!serializer.validate(Message.class, response.toString())) {
                return null;
            }

            UpdateResponseMessage newMessages = serializer
                    .read(UpdateResponseMessage.class, response.toString());

            return newMessages;
        }

        void stop() {
            stopSelf(startId);
        }
    }
}
