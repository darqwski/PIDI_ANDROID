package com.example.darqwski.pidi04;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Notepad extends AppCompatActivity {

    public ListView listSaved;
    public ArrayList<ArticleFormat> savedArticles=new ArrayList<ArticleFormat>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad);
        listSaved=(ListView)findViewById(R.id.ListOfSaves);

    }
    @Override
    protected void onStart()
    {
        savedArticles=new ArrayList<ArticleFormat>();
        super.onStart();File path =Notepad.this.getFilesDir();
        File file = new File(path, "savedlinks.txt");
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


        String contents = new String(bytes);
        int numberoffiltres=0;


        //   JSONArray names=
        try {
            JSONObject json=new JSONObject(contents);
            JSONArray jsonArrayTitle=json.getJSONArray("title");
            JSONArray jsonArrayLink=json.getJSONArray("link");
            JSONArray jsonArrayImage=json.getJSONArray("image");
            JSONArray jsonArrayDate=json.getJSONArray("date");
            JSONArray jsonArraySite=json.getJSONArray("site");
            JSONArray jsonArrayCategory=json.getJSONArray("category");
            JSONArray jsonArraySiteImg=json.getJSONArray("siteimg");
            numberoffiltres=jsonArrayTitle.length();
            Log.d("a",String.valueOf(numberoffiltres));
            for(int i=0;i<numberoffiltres;i++)
            {
                ArticleFormat a=new ArticleFormat(jsonArrayTitle.getString(i),
                        jsonArrayLink.getString(i),jsonArrayImage.getString(i),
                        jsonArrayDate.getString(i),jsonArrayCategory.getString(i),
                        jsonArraySite.getString(i),jsonArraySiteImg.getString(i),
                        "0");
                savedArticles.add(a);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final NotepadAdpater adapter = new NotepadAdpater(Notepad.this, R.layout.articlelayout,savedArticles);
        listSaved.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////OPERACJA KLIKNIĘCIA W LINK!!!
//////////////////////////////////////////////////////////////////////////////
                String urlString=savedArticles.get(position).link;
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    intent.setPackage(null);
                    startActivity(intent);
                }
            }

        });

        int index =listSaved.getFirstVisiblePosition();
        View v =listSaved.getChildAt(0);
        int top = (v == null) ? 0 : v.getTop();
        listSaved.setAdapter(adapter);
        listSaved.setSelectionFromTop(index, top);
        registerForContextMenu(listSaved);


    }
    public void saveListToBase(int position)
    {

        File file = new File("/data/data/com.example.darqwski.pidi031/files", "savedlinks.txt");
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
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        int numberofarticles = 0;
        String contents = new String(bytes);

        numberofarticles=savedArticles.size();
        //   JSONArray names=
        String titleLine="\"title\":[";
        String linkLine="\"link\":[";
        String imageLine="\"image\":[";
        String dateLine="\"date\":[";
        String siteLine="\"site\":[";
        String categotyLine="\"category\":[";
        String siteimgLine="\"siteimg\":[";
        String AllData;
        try
        {
            for(int i=0;i<numberofarticles;i++)if(i!=position)
            {
                if(i+1<numberofarticles)if(!(i+1==position&&i+2==numberofarticles)) {

                    ArticleFormat help=savedArticles.get(i);
                    titleLine+="\""+help.title+"\",";
                    linkLine+="\""+help.link+"\",";
                    imageLine+="\""+help.image_link+"\",";
                    dateLine+="\""+help.date+"\",";
                    siteLine+="\""+help.site+"\",";
                    categotyLine+="\""+help.category+"\",";
                    siteimgLine+="\""+help.site_image_link+"\",";
                }
                //   NameLine+="\""+names.get(i)+"\"";
                else {
                    ArticleFormat help=savedArticles.get(i);
                    titleLine+="\""+help.title+"\"";
                    linkLine+="\""+help.link+"\"";
                    imageLine+="\""+help.image_link+"\"";
                    dateLine+="\""+help.date+"\"";
                    siteLine+="\""+help.site+"\"";
                    categotyLine+="\""+help.category+"\"";
                    siteimgLine+="\""+help.site_image_link+"\"";
                    Log.d("LINK",help.title);
                }
            }
        }
        catch (Exception e)
        {
            Log.d("NUMBER",String.valueOf(numberofarticles));
        }


        titleLine+="],";
        linkLine+="],";
        imageLine+="],";
        dateLine+="],";
        siteLine+="],";
        categotyLine+="],";
        siteimgLine+="]";

        AllData="{"+titleLine+ linkLine+ imageLine+ dateLine+ siteLine+ categotyLine+ siteimgLine+"}";
        Log.d("TitleLine",titleLine);
        Log.d("LinkLine",linkLine);
        try {
            stream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            // stream.write("".getBytes());
            stream.write(AllData.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }//data/data/com.example.darqwski.pidi031/files


        // return false;
        file = new File(Notepad.this.getFilesDir(), "savedlinks.txt");
        length = (int) file.length();

        bytes = new byte[length];

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


        contents = new String(bytes);
        int numberoffiltres=0;


        //   JSONArray names=
        try {
            JSONObject json=new JSONObject(contents);
            JSONArray jsonArrayTitle=json.getJSONArray("title");
            JSONArray jsonArrayLink=json.getJSONArray("link");
            JSONArray jsonArrayImage=json.getJSONArray("image");
            JSONArray jsonArrayDate=json.getJSONArray("date");
            JSONArray jsonArraySite=json.getJSONArray("site");
            JSONArray jsonArrayCategory=json.getJSONArray("category");
            JSONArray jsonArraySiteImg=json.getJSONArray("siteimg");
            numberoffiltres=jsonArrayTitle.length();
            Log.d("a",String.valueOf(numberoffiltres));
            for(int i=0;i<numberoffiltres;i++)
            {
                ArticleFormat a=new ArticleFormat(jsonArrayTitle.getString(i),
                        jsonArrayLink.getString(i),jsonArrayImage.getString(i),
                        jsonArrayDate.getString(i),jsonArrayCategory.getString(i),
                        jsonArraySite.getString(i),jsonArraySiteImg.getString(i),
                        "0");
                savedArticles.add(a);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
