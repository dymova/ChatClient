package ru.nsu.ccfit.dymova.chatclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.simpleframework.xml.core.Persister;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NewMessageActivity extends AppCompatActivity {
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

                if(name.isEmpty() || text.isEmpty()) {
                    Toast t = Toast.makeText(this, "Please, fill all fields.", Toast.LENGTH_SHORT);
                    t.show();
                    return true;
                }

                try {
                    saveMessageOnServer(message);
                } catch (Exception e) {
                    Toast t = Toast.makeText(this, "save error", Toast.LENGTH_SHORT);
                    t.show();
                    e.printStackTrace();
                }

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveMessageOnServer(Message message) throws Exception {
        URL url = new URL(HOST);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        Toast t = Toast.makeText(getBaseContext(), "connection establish", Toast.LENGTH_SHORT);
        t.show();

        OutputStream out = connection.getOutputStream();

        t = Toast.makeText(getBaseContext(), "get stream", Toast.LENGTH_SHORT);
        t.show();

        serializer.write(message, out);
        out.flush();
        out.close();

    }
}
