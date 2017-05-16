package com.example.darqwski.pidi04;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */public class FullscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        hide_action_bar();
        String fromFile=file_get_contents("settingsy.txt");
        String mainfiltr="Dzis|";
        String mainfiltrname="Domyslny";
        String admin = "0";
        String style="Standard";
if(fromFile!="")
{
    try {
        JSONObject json=new JSONObject(fromFile);
     //   admin =json.getString("Admin");
    //    style =json.getString("Style");
        mainfiltr=json.getString("MainFiltr");
       mainfiltrname=json.getString("MainFiltrName");

    } catch (JSONException e) {
        e.printStackTrace();
    }
}
        else
{
    String newData="{\"MainFiltr\":\""+mainfiltr+"\",\"MainFiltrName\":\""+mainfiltrname+"\",\"Admin\":\""+admin+"\",\"Style\":\""+style+"\"}";
    file_put_contents(newData,"settingsy.txt");

}


        RelativeLayout WholeView=  ((RelativeLayout)findViewById(R.id.activity_main));

        final String finalMainfiltrname = mainfiltrname;
        final String finalMainfiltr = mainfiltr;

        WholeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goTo=new Intent(getApplicationContext(),MainActivity.class);
Log.d("ADRES","http://darqwski.cba.pl/PIDI/antek.php?filtr="+ finalMainfiltr + "&count=");
                goTo.putExtra("Type","http://darqwski.cba.pl/PIDI/antek.php?filtr="+ finalMainfiltr + "|&count=");//tu trzeba bedzie wsadzic pobieranie z pliku
                goTo.putExtra("Title", finalMainfiltrname);//tu trzeba bedzie wsadzic pobieranie z pliku
                startActivity(goTo);

            }
        });

    }

    private void hide_action_bar()
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
    private void wait_for_(int a)
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
            }
        }, a);
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
        String contents=null;
        if(length!=0)contents = new String(bytes);
        else contents= "";
        Log.d("CONTEN",pathy);
        Log.d("CONTEN",String.valueOf(length));
        return contents;
    }

    void file_put_contents(String data,String where)
    {
        File file = new File("/data/data/com.example.darqwski.pidi04/files", where);
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(where, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
