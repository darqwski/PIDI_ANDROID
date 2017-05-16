package com.example.darqwski.pidi04;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class FiltrMaker extends AppCompatActivity {

    public String options;
    public ArrayList<String> ListOfAll = new ArrayList<String>();
    public int SumOf;
    public int Ls, Lc, Ld;
    public LinearLayout Lista1;
    public int numberoffiltres;
    public ArrayList<String> names=null,content=null;
    /*
    *   JSONObject json=new JSONObject(streamToString(in));
                int Ls,Lc,Ld;
                Ls=json.optInt("SiteNumber");
                Lc=json.optInt("CatsNumber");
                Ld=json.optInt("DateNumber");

                for(int i=0;i<Ls;i++)ListOfSite.add(json.optString("Site"+String.valueOf(i)));
                for(int i=0;i<Lc;i++)ListOfCats.add(json.optString("Cat"+String.valueOf(i)));
                for(int i=0;i<Ld;i++)ListOfDates.add(json.optString("Date"+String.valueOf(i)));
                */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtr_maker);
        Lista1 = (LinearLayout) findViewById(R.id.ListOfAll);


    }

    @Override
    protected void onStart() {
        super.onStart();
        new MakeList().execute("Ja wohl");
//
    }

    private class MakeList extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://darqwski.cba.pl/PIDI/antek.php?filtrmaker");
                URLConnection connection = url.openConnection();
                InputStream in = new BufferedInputStream(
                        connection.getInputStream());

                JSONObject json = new JSONObject(streamToString(in));

                Ls = json.optInt("SiteNumber");
                Lc = json.optInt("CatsNumber");
                Ld = json.optInt("DateNumber");
                SumOf = Lc + Ls + Ld;

                for (int i = 0; i < Ls; i++)
                    ListOfAll.add(json.optString("Site" + String.valueOf(i)));
                for (int i = 0; i < Lc; i++)
                    ListOfAll.add(json.optString("Cat" + String.valueOf(i)));
                for (int i = 0; i < Ld; i++)
                    ListOfAll.add(json.optString("Date" + String.valueOf(i)));
                return null;

            } catch (Exception e) {
                Log.d(MainActivity.class.getSimpleName(), e.toString());
                return null;
            }

        }

        @Override
        protected void onPostExecute(String a) {

            final float scale = getResources().getDisplayMetrics().density;
            int sizetextbox = 70;
            int sizecheckbox = 60;
            int fontsize = 20;
            int pixels;
            TextView antoni = new TextView(FiltrMaker.this);
            antoni.setText("Zestaw stron");

            pixels = (int) (sizetextbox * scale + 0.5f);
            antoni.setHeight(pixels);
            antoni.setPadding(5, 5, 5, 5);
            antoni.setTextSize(fontsize + 10);

            Lista1.addView(antoni);

            for (int i = 0; i < Ls; i++) {
                CheckBox checkbox = new CheckBox(FiltrMaker.this);
                checkbox.setText(ListOfAll.get(i));
                pixels = (int) (sizecheckbox * scale + 0.5f);
                checkbox.setHeight(pixels);
                checkbox.setId(i);
                checkbox.setTextSize(fontsize);
                checkbox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#0F0F88")));
                //(Color.parseColor("#0F0F88"));
                Lista1.addView(checkbox);
            }

            antoni = new TextView(FiltrMaker.this);
            antoni.setText("Zestaw kategorii");

            antoni.setPadding(5, 5, 5, 5);
            pixels = (int) (sizetextbox * scale + 0.5f);
            antoni.setHeight(pixels);
            antoni.setTextSize(fontsize + 10);

            Lista1.addView(antoni);

            for (int i = Ls; i < Ls + Lc; i++) {
                CheckBox checkbox = new CheckBox(FiltrMaker.this);
                checkbox.setText(ListOfAll.get(i));
                pixels = (int) (sizecheckbox * scale + 0.5f);
                checkbox.setHeight(pixels);
                checkbox.setId(i + 1);
                checkbox.setTextSize(fontsize);
                checkbox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#0F0F88")));
                Lista1.addView(checkbox);

            }
            antoni = new TextView(FiltrMaker.this);
            antoni.setText("Zestaw dat");

            pixels = (int) (sizetextbox * scale + 0.5f);
            antoni.setHeight(pixels);
            antoni.setPadding(5, 5, 5, 5);
            antoni.setTextSize(fontsize + 10);

            Lista1.addView(antoni);

            for (int i = Ls + Lc; i < Ls + Lc + Ld; i++) {
                CheckBox checkbox = new CheckBox(FiltrMaker.this);
                checkbox.setText(ListOfAll.get(i));
                checkbox.setId(i + 2);
                pixels = (int) (sizecheckbox * scale + 0.5f);
                checkbox.setHeight(pixels);
                checkbox.setTextSize(fontsize);
                checkbox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#0F0F88")));
                Lista1.addView(checkbox);
            }


            Button finish = (Button) findViewById(R.id.save_filtr_button);
            finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    LinearLayout lista = (LinearLayout) findViewById(R.id.ListOfAll);
                    String name = ((EditText) findViewById(R.id.newFiltrName)).getText().toString();
                    options = "";
                    for (int i = 0; i < lista.getChildCount(); i++) {
                        View a = lista.getChildAt(i);
                        if (a instanceof CheckBox)
                            if (((CheckBox) lista.getChildAt(i)).isChecked())
                                options += (((CheckBox) lista.getChildAt(i)).getText() + "|");
                    }

                    File path = FiltrMaker.this.getFilesDir();
                    File file = new File(path, "database.txt");
                    FileOutputStream stream = null;
                    int length = (int) file.length();

                    //POBIERANIE DANYCH
                    byte[] bytes = new byte[length];
                    if(length!=0)
                    {
                        FileInputStream in = null;
                        try {
                            in = new FileInputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        try {
                            in.read(bytes);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(in!=null) in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }



                    names=new ArrayList<String>();
                    content=new ArrayList<String>();
                    String contents = new String(bytes);
                    try {
                        JSONObject json=new JSONObject(contents);
                        JSONArray jsonArrayName=json.getJSONArray("names");
                        JSONArray jsonArrayFiltr=json.getJSONArray("filtres");
                        numberoffiltres=jsonArrayName.length();
                        for(int i=0;i<numberoffiltres;i++)
                        {
                            names.add((String)jsonArrayName.get(i));
                            content.add((String)jsonArrayFiltr.get(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //ZAPIS NOWYCH DANYCH
                    //names filres

                    String NameLine="\"names\":[";
                    String FiltrLine="\"filtres\":[";
                    String AllData;
                    for(int i=0;i<numberoffiltres;i++)NameLine+="\""+names.get(i)+"\",";
                    NameLine+="\""+name+"\"";
                    NameLine+="],";
                    for(int i=0;i<numberoffiltres;i++)FiltrLine+="\""+content.get(i)+"\",";
                    FiltrLine+="\""+options+"\"";
                    FiltrLine+="]";
                    AllData="{"+NameLine+FiltrLine+"}";
                    try {
                        stream = new FileOutputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        stream.write(AllData.getBytes());
                        //stream.write("".getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(FiltrMaker.this, "Filtr zostal zapisany", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    static String streamToString(InputStream is) {
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
