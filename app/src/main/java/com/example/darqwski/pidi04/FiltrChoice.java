package com.example.darqwski.pidi04;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FiltrChoice extends AppCompatActivity {
    public ArrayList<String> Names=new ArrayList<String>();
    public    ArrayList<String> Filtres=new ArrayList<String>();
    public int NumberOfFiltres;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtr_choice);
        File path =FiltrChoice.this.getFilesDir();
        File file = new File(path, "database.txt");
        int length = (int) file.length();

        byte[] bytes = new byte[length];

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
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final ArrayList<String> Names=new ArrayList<String>();
        final ArrayList<String>Filtres=new ArrayList<String>();

        String contents = new String(bytes);
        ((TextView)findViewById(R.id.Inside)).setText(contents);
        try {

            JSONObject json=new JSONObject(contents);
            JSONArray JSONname=json.getJSONArray("names");
            JSONArray JSONfiltr=json.getJSONArray("filtres");
            NumberOfFiltres=JSONfiltr.length();
            for(int i=0;i<NumberOfFiltres;i++)
            {
                Names.add((String)JSONname.get(i));
                Filtres.add((String)JSONfiltr.get(i));
            }

            ListView list=(ListView)findViewById(R.id.ListOfFiltres);
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(FiltrChoice.this,R.layout.choicelayout,Names);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent changeActivity = new Intent(getApplicationContext(), MainActivity.class);

                    try
                    {
                        changeActivity.putExtra("Type", "http://darqwski.cba.pl/PIDI/antek.php?filtr="+ Filtres.get(position) + "&count=");


                    }
                    catch(Error e)
                    {
                        Log.d("TAG",String.valueOf(Names.get(position)) + "&count=");
                    }
                    changeActivity.putExtra("Title",String.valueOf(Names.get(position)));
                    startActivity(changeActivity);
                }
            });
            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    File path = FiltrChoice.this.getFilesDir();
                    File file = new File(path, "database.txt");
                    FileOutputStream stream = null;
                    int length = (int) file.length();

                    //POBIERANIE DANYCH
                    byte[] bytes = new byte[length];

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
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    int numberoffiltres = 0;
                    ArrayList<String>  names=new ArrayList<String>();
                    ArrayList<String>content=new ArrayList<String>();
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
                    String AllData;//1
                    for(int i=0;i<numberoffiltres;i++)if(i!=position)
                    {
                        NameLine+="\""+names.get(i)+"\"";
                        if(i+1<numberoffiltres)if(!(i+1==position&&i+2==numberoffiltres)) NameLine+=",";
                    }

                    NameLine+="],";
                    for(int i=0;i<numberoffiltres;i++)if(i!=position)
                    {
                        FiltrLine+="\""+content.get(i)+"\"";
                        if(i+1<numberoffiltres)if(!(i+1==position&&i+2==numberoffiltres)) FiltrLine+=",";
                    }

                    FiltrLine+="]";
                    AllData="{"+NameLine+FiltrLine+"}";
                    try {
                        stream = new FileOutputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        stream.write(AllData.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Intent intent = new Intent(getApplicationContext(),FiltrChoice.class);
                    startActivity(intent);
                    return false;

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
