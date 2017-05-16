package com.example.darqwski.pidi04;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Settings extends AppCompatActivity {
public String fromFile=null;
    public ArrayList<FiltrFormat> FiltrList=new ArrayList<FiltrFormat>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final RadioGroup radioGroup = (RadioGroup)findViewById(R.id.ListOfFavourites);
        fromFile=file_get_contents("database.txt");
        Log.d("FROM",fromFile);
        try {
            JSONObject json=new JSONObject(fromFile);
            JSONArray JSONname=json.getJSONArray("names");
            JSONArray JSONfiltr=json.getJSONArray("filtres");

            for(int i=0;i<JSONname.length();i++) FiltrList.add(new FiltrFormat(JSONname.get(i).toString(),JSONfiltr.get(i).toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
      FiltrList.add(new FiltrFormat("Domyślny","Dzis"));
        for(int i=0;i<FiltrList.size();i++)
        {

            RadioButton radioButton=new RadioButton(getApplicationContext());
            radioButton.setId(100+i);
            radioButton.setTextSize(18);
            radioButton.setTextColor(Color.BLACK);
            radioButton.setText(FiltrList.get(i).name);
            radioGroup.addView(radioButton);
        }
        ((Button)findViewById(R.id.SaveMainFiltr)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               int a= radioGroup.getCheckedRadioButtonId()-100;

                fromFile=file_get_contents("settingsy.txt");
                String mainfiltr;
                String mainfiltrname;
                String admin = null;
                String style=null;

                try {
                    JSONObject json=new JSONObject(fromFile);
                    admin =json.getString("Admin");
                   style =json.getString("Style");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("A",String.valueOf(a)) ;
                mainfiltr=FiltrList.get(a).content;
                mainfiltrname=FiltrList.get(a).name;
                String newData="{\"MainFiltr\":\""+mainfiltr+"\",\"MainFiltrName\":\""+mainfiltrname+"\",\"Admin\":\""+admin+"\",\"Style\":\""+style+"\"}";
                file_put_contents(newData,"settingsy.txt");
            }
        });

        ((Button)findViewById(R.id.adminbutton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a=((EditText)findViewById(R.id.AdminPassword)).getText().toString();
                Log.d("A",String.valueOf(a.equals("supertrupertajnehaslo")));

                if(a.equals("supertrupertajnehaslo"))
                {
                    Log.d("A",String.valueOf(a.equals("supertrupertajnehaslo")));
                    String fromFile=file_get_contents("settingsy.txt");
                    String mainfiltr="Dzis|";
                    String mainfiltrname="Domyslny";
                    String admin = "BOSS";
                    String style="Standard";
                    if(fromFile!="")
                    {

                        try {
                            JSONObject json=new JSONObject(fromFile);

                               style =json.getString("Style");
                            mainfiltr=json.getString("MainFiltr");
                            mainfiltrname=json.getString("MainFiltrName");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String newData="{\"MainFiltr\":\""+mainfiltr+"\",\"MainFiltrName\":\""+mainfiltrname+"\",\"Admin\":\""+admin+"\",\"Style\":\""+style+"\"}";
                        Log.d("NewData",newData);
                        file_put_contents(newData,"settingsy.txt");
                        Toast.makeText(getApplicationContext(),"Właśnie zostałeś administratorem",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String newData="{\"MainFiltr\":\""+mainfiltr+"\",\"MainFiltrName\":\""+mainfiltrname+"\",\"Admin\":\""+admin+"\",\"Style\":\""+style+"\"}";
                        file_put_contents(newData,"settingsy.txt");
                        Toast.makeText(getApplicationContext(),"Właśnie zostałeś administratorem",Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });

    }

    String file_get_contents(String pathy)
    {
        File file = new File(getFilesDir(), pathy);
        int length = (int) file.length();
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
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
        String contents;
        if(length!=0)contents = new String(bytes);
        else contents= "";
        return contents;
    }

    void file_put_contents(String data,String where)
    {
        File file = new File(getFilesDir(), where);
        FileOutputStream stream=null;
        try {
            stream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            //  stream.write("".getBytes());
            stream.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
