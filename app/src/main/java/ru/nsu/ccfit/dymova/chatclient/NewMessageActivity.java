package ru.nsu.ccfit.dymova.chatclient;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.simpleframework.xml.core.Persister;

import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewMessageActivity extends AppCompatActivity {
    public static final String LOG_TAG = "NEW_MESSAGE";

    public static final String HOST = "http://10.0.0.30:8080/api/message";

    private EditText nameEditText;
    private EditText textEditText;
    private static Persister serializer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        serializer = new Persister();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameEditText = (EditText) findViewById(R.id.editText_name);
        textEditText = (EditText) findViewById(R.id.editText_text);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_message: {
                String name = nameEditText.getText().toString();
                String text = textEditText.getText().toString();
                Message message = new Message(name, text, 0); //todo

                if (name.isEmpty() || text.isEmpty()) {
                    Toast t = Toast.makeText(this, "Please, fill all fields.", Toast.LENGTH_SHORT);
                    t.show();
                    return true;
                }

                Task task = new Task(message);
                task.execute();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class Task extends AsyncTask<Void, Void, Void> {
        private final Message message;

        Task(Message message) {
            this.message = message;
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("http", "10.0.0.30", 8080, "/api/message").openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("User-agent", "TestClient");

                connection.setDoOutput(true);
                try (OutputStream outputStream = connection.getOutputStream()) {
                    Persister serializer = new Persister();
                    StringWriter writer = new StringWriter();

                    serializer.write(message, writer);

                    outputStream.write(writer.toString().getBytes());
                }
                System.out.println(connection.getResponseCode());
            } catch (Exception e) {
                Toast t = Toast.makeText(getApplicationContext(), "save error", Toast.LENGTH_SHORT);
                t.show();
                e.printStackTrace();
            }

            return null;
        }
    }
}
