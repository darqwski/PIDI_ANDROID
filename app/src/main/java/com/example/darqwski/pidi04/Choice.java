package com.example.darqwski.pidi04;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class Choice extends AppCompatActivity {
    private ListView list ;
    private ArrayAdapter<String> adapter ;
    public int number_of_cats;
    public String adres;
    public String getFromEngineAdres;
    public String getTitleButton;
    public ArrayList choices =new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        Intent i=getIntent();
        adres = i.getStringExtra("adres");
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        new WebServiceHandler().execute(adres);
    }
    private class WebServiceHandler extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();
                InputStream in = new BufferedInputStream(
                        connection.getInputStream());
                return streamToString(in);                      //KONWERSJA STREAM TO STRING

            } catch (Exception e) {
                Log.d(MainActivity.class.getSimpleName(), e.toString());
                return null;
            }

        }

        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject json = new JSONObject(result);
                number_of_cats = json.optInt("number");
                getFromEngineAdres=json.optString("getFromEngineAdres");
                getTitleButton=json.optString("getTitleButton");
                for (int i = 0; i < number_of_cats; i++)
                    choices.add(json.opt("item" + String.valueOf(i)));

            } catch (JSONException e) {
                e.printStackTrace();

            }


            list = (ListView) findViewById(R.id.listOfChoices);
            adapter = new ArrayAdapter<String>(Choice.this, R.layout.choicelayout, choices);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                Intent changeActivity = new Intent(getApplicationContext(), MainActivity.class);

                                                try
                                                {
                                                    changeActivity.putExtra("Type", getFromEngineAdres+ String.valueOf(choices.get(position)) + "&count=");
                                                }
                                                catch(Error e)
                                                {
                                                    Log.d("TAG",getFromEngineAdres+ String.valueOf(choices.get(position)) + "&count=");
                                                }
                                                changeActivity.putExtra("Title",getTitleButton+String.valueOf(choices.get(position)));
                                                choices =new ArrayList<String>();
                                                startActivity(changeActivity);

                                            }
                                        }
            );
        }

    }

    public static String streamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        try {

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            reader.close();

        } catch (IOException e) {
            Log.d(MainActivity.class.getSimpleName(), e.toString());
        }

        return stringBuilder.toString();
    }
}

