package ru.nsu.ccfit.dymova.chatclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatService extends Service {

    public static final String LOG_TAG = "MyService";
    public static final String HOST = "http://10.0.0.30:8080/api/update";
    private ExecutorService es;
    private static Persister serializer;


    @Override
    public void onCreate() {
        super.onCreate();
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
        int lastId = intent.getIntExtra("id", -1);
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

            try {
                UpdateResponseMessage response = getUpdate(lastId);

                if (response != null && !response.messages.isEmpty()) {
                    for (Message m : response.messages) {
                        authors.add(m.getAuthor());
                        texts.add(m.getText());
                        ids.add(m.getId());
                    }
                    Log.d(LOG_TAG, "size : " + authors.size());
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

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            Log.d(LOG_TAG, String.valueOf("Get stream : " + in!=null));

            String line;
            StringBuilder response = new StringBuilder();
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();


            String text = response.toString();
            Reader reader = new StringReader(text);
            if (!serializer.validate(UpdateResponseMessage.class, response.toString())) {
                return null;
            }

            return serializer
                    .read(UpdateResponseMessage.class, reader, false);
        }

        void stop() {
            stopSelf(startId);
        }
    }
}
