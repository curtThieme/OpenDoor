package com.example.curt.opendoor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class OpenDoor extends AppCompatActivity {

    Button button;
    String hashString = "Door-Controller";
    String request = "http://10.5.218.148/openTest.php";


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public void createKey() throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("SHA-256");
        //m.reset();
        m.update(hashString.getBytes());
        for (byte i : m.digest()) {
            System.out.print(String.format("%02x", i));
        }
        System.out.println();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_door);

//        try {
//            createKey();
//        } catch (NoSuchAlgorithmException e) {
//            System.out.println("No algo thrown\n");
//        }


        button = (Button) findViewById(R.id.openDoorButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do request to server
                new LongOperation().execute("http://10.0.2.2/openTest.php");//TODO: i dont remember if i need that string parameter so im leaving it be
            }
        });
    }


    private class LongOperation extends AsyncTask<String, Void, String> {//switch to happen in worker thread, not Async

        @Override
        protected String doInBackground(String... params) {
            OutputStream out = null;
            JSONObject sendObject = new JSONObject();

            try {
                URL url = new URL( request );
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                //conn.setRequestProperty( "Content-Length", Integer.toString(hashString.length()));
                conn.setRequestMethod("POST");
                conn.connect();

                //String data = "pkey="+hashString;
                sendObject.put("pkey", hashString);
                Log.i("open Test", hashString);
                OutputStream os = conn.getOutputStream();

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                writer.write(sendObject.toString());
                writer.flush();
                writer.close();
                os.close();
                //conn.connect();

                //read back, needed by android to not crash, don't actually have to do anything with the data

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                String line = null;
                StringBuilder sb = new StringBuilder();
                while((line = reader.readLine()) != null){
                    sb.append(line);
                }

                reader.close();
                Log.i("returned: ", sb.toString());




                Log.i("open Test", "Async finished");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return hashString;
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
