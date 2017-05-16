package com.example.darqwski.pidi04;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.example.darqwski.pidi04.ArticleAdapter.Base64ToBitmap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public ArrayList<ArticleFormat> Articles;
    private ListView listView;
    public int Article_Count=0;
    public int numberOfVisitedSites=0;
    public boolean Article_is_Over=false;
    public String adres;
    public String name_filtr;
    public boolean isLoading=false;
    public int numberOfDownloadedArticles=0;
    public int numberOFDeletedArticles=0;
    public  ArticleAdapter adapter=null;
    private String m_Text = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // hide_action_bar();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        listView=(ListView)findViewById(R.id.MainList);


    }
    @Override
    protected void onStart()
    {
        super.onStart();
        Intent i=getIntent();
        adres=i.getStringExtra("Type");
        name_filtr=i.getStringExtra("Title");
        Log.d("adres",String.valueOf(adres));

        PersonaliseArticles personaliseArticles=new PersonaliseArticles(getApplicationContext());
        new WebServiceHandler().execute(adres+"0"+personaliseArticles.getBestSearchings());
        Log.d("fav",file_get_contents("favourites.txt"));
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && (listView.getLastVisiblePosition() - listView.getHeaderViewsCount() - listView.getFooterViewsCount()) >= (Articles.size() - 1)) {
                    if(!Article_is_Over) if(!isLoading)
                    {
                        PersonaliseArticles personaliseArticles=new PersonaliseArticles(getApplicationContext());
                        new WebServiceHandler().execute(adres +String.valueOf(10*Article_Count)+personaliseArticles.getBestSearchings());

                    }

                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
        });














    }

    private class WebServiceHandler extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute(){
            isLoading=true;

        }

        @Override
        protected String doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();
                InputStream in = new BufferedInputStream(connection.getInputStream());
                return streamToString(in);                      //KONWERSJA STREAM TO STRING

            } catch (Exception e) {
                Log.d("DO IN BACKGROUND","Something goes wrong");
                Log.d(MainActivity.class.getSimpleName(), e.toString());
                return null;
            }

        }
        protected void onPostExecute(String result)
        {
             isLoading=false;
            int number_of_all_articles;
            try {
                JSONObject json = new JSONObject(result);
                number_of_all_articles=json.getInt("number_of_all_articles");
                if(Articles==null)Articles=new ArrayList<ArticleFormat>();
                for(int i=Article_Count*10;i<Article_Count*10+json.optInt("number");i++)
                {
                    ArticleFormat help=new ArticleFormat();
                    help.image_link=json.optString("img"+String.valueOf(i));
                    help.title=json.optString("title"+String.valueOf(i));
                    help.link=json.optString("link"+String.valueOf(i));
                    help.date=json.optString("date"+String.valueOf(i));
                    help.site=json.optString("site"+String.valueOf(i));
                    help.category=json.optString("category"+String.valueOf(i));
                    help.id_article=json.optString("idarticle"+String.valueOf(i));
                    help.site_image_link=json.optString("sitefoto"+String.valueOf(i));
                    numberOfDownloadedArticles=json.optInt("number");
                    if(help.title!="") Articles.add(help);
                    Log.d("end",json.optString("end"));
                    if(json.optInt("end")==123)
                    {
                        Article_is_Over=true;
                        Log.d("Article is over",String.valueOf(Article_is_Over));
                    }

                }
                Article_Count++;
                adapter = new  ArticleAdapter(MainActivity.this, R.layout.articlelayout,Articles);
                final SwipeDetector swipeDetector=new SwipeDetector();
                listView.setOnTouchListener(swipeDetector);

                //  for(int i=0;i<listView.getCount();i++)listView.getChildAt(i).setOnTouchListener(swipeDetector);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////OPERACJA KLIKNIĘCIA W LINK!!!
//////////////////////////////////////////////////////////////////////////////
                        if(swipeDetector.swipeDetected()) {


                                Object toRemove = listView.getChildAt(position);
                                Articles.remove(position - 1);
                                adapter.notifyDataSetChanged();
                               numberOFDeletedArticles++;
                               if (!Article_is_Over) if (!isLoading) new WebServiceHandler().execute(adres + String.valueOf(10 * Article_Count));




                        }
                        else
                        {
                            position = position - 1;
                            String urlString = Articles.get(position).link;
                            numberOfVisitedSites++;
                            PersonaliseArticles favorite=new PersonaliseArticles(getApplicationContext());
                            favorite.addVisitation( Articles.get(position).category,Articles.get(position).site);
                           // file_put_contents("","favourites.txt");
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setPackage("com.android.chrome");

                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException ex) {
                                Log.d("ERROR",ex.toString());
                                intent.setPackage(null);
                                startActivity(intent);
                            }
                        }

                    }
                });

                int index = listView.getFirstVisiblePosition();
                View v = listView.getChildAt(0);
                int top = (v == null) ? 0 : v.getTop();

                listView.setAdapter(adapter);
                if(Article_Count==1)
                {
                    View header = getLayoutInflater().inflate(R.layout.headerlayout,null);
                    ((TextView)header.findViewById(R.id.headerTextView)).setText("Na chwilę obecną jest: \n"+String.valueOf(number_of_all_articles)+" Artykułów w "+name_filtr);
                    listView.addHeaderView(header);


                }
                listView.setSelectionFromTop(index, top);
                registerForContextMenu(listView);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for(int i2=0;i2<numberOfDownloadedArticles;i2++)
            {
                try{
                   new WebServiceImageAfterArticle().execute(Articles.get(i2+(Article_Count-1-numberOfVisitedSites)*10-numberOFDeletedArticles));
                }
                catch(Exception e)
                {
                    Log.d("ERROR",e.toString());
                    Log.d("ArticleCount",String.valueOf(Article_Count));
                    Log.d("NumberofVisited",String.valueOf(numberOfVisitedSites));
                    Log.d("i2",String.valueOf(i2));
                    Log.d("All-in",String.valueOf(i2+(Article_Count-1-numberOfVisitedSites)*10-numberOFDeletedArticles));
                }
            }
            Toast.makeText(getApplicationContext(),"Więcej artykułów",Toast.LENGTH_SHORT).show();
        }
    }

    private class WebServiceImageAfterArticle extends AsyncTask<ArticleFormat,Void, String>
    {

        @Override
        protected String doInBackground(ArticleFormat... params) {

            try {
                URL url = new URL("http://darqwski.cba.pl/PIDI/antek.php?foto_id="+params[0].id_article);
                URLConnection connection = url.openConnection();
                InputStream in = new BufferedInputStream(connection.getInputStream());
                JSONObject json = new JSONObject(streamToString(in));
                String img64=json.getString("adress");
                params[0].final_image=Base64ToBitmap( img64);
                url = new URL("http://darqwski.cba.pl/PIDI/antek.php?site_id="+params[0].id_article);
              connection = url.openConnection();
               in = new BufferedInputStream(connection.getInputStream());
                json = new JSONObject(streamToString(in));
                img64=json.getString("adress");
                params[0].site_image_foto=Base64ToBitmap( img64);
                return img64;                    //KONWERSJA STREAM TO STRING

            } catch (Exception e) {
                Log.d("DO IN BACKGROUND","Something goes wrong");
                Log.d(MainActivity.class.getSimpleName(), e.toString());
                return null;
            }

        }
        protected void onPostExecute(String result)
        {
            adapter.notifyDataSetChanged();
        }

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
     //   getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.catItem) {
            Intent changeActivity = new Intent(getApplicationContext(), Choice.class);
            changeActivity.putExtra("adres","http://darqwski.cba.pl/PIDI/antek.php?gimmi=cat");
            startActivity(changeActivity);
        }
        else if (id == R.id.siteItem) {
            Intent changeActivity = new Intent(getApplicationContext(), Choice.class);
            changeActivity.putExtra("adres","http://darqwski.cba.pl/PIDI/antek.php?gimmi=site");
            startActivity(changeActivity);
        } else if (id == R.id.dateItem) {
            Intent changeActivity = new Intent(getApplicationContext(), Choice.class);
            changeActivity.putExtra("adres","http://darqwski.cba.pl/PIDI/antek.php?gimmi=date");
            startActivity(changeActivity);

        } else if (id == R.id.tagItem) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Podaj słowo klucz");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AAAAAA")));

            builder.setView(input);
           builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    m_Text = input.getText().toString();
                    dialog.cancel();
                    Intent changeActivity = new Intent(getApplicationContext(), MainActivity.class);
                    changeActivity.putExtra("Type","http://darqwski.cba.pl/PIDI/antek.php?hashtag="+m_Text+"&count=");
                    changeActivity.putExtra("Title",m_Text);
                    startActivity(changeActivity);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {dialog.cancel();}
            });

            builder.show();




        } else if (id == R.id.filterItem) {

            Intent changeActivity = new Intent(getApplicationContext(), FiltrChoice.class);
            startActivity(changeActivity);
        }
        else if (id == R.id.settingsItem) {
           // Toast.makeText(getApplicationContext(),"Czuta",Toast.LENGTH_SHORT).show();
            Intent changeActivity = new Intent(getApplicationContext(), Settings.class);
            startActivity(changeActivity);

        } else if (id == R.id.saveItem) {
            Intent changeActivity = new Intent(getApplicationContext(), Notepad.class);
            startActivity(changeActivity);

        } else if (id == R.id.makerItem) {
            Intent changeActivity = new Intent(getApplicationContext(), FiltrMaker.class);
            startActivity(changeActivity);
        }else if (id == R.id.refreshdatabase) {
            String fromFile=file_get_contents("settingsy.txt");
            String admin = null;
            if(fromFile!="")
            {
                try {
                    JSONObject json=new JSONObject(fromFile);
                      admin =json.getString("Admin");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                String newData="{\"MainFiltr\":\"Dzis|\",\"MainFiltrName\":\"Domyślny\",\"Admin\":\"0\",\"Style\":\"0\"}";
                file_put_contents(newData,"settingsy.txt");

            }
                Log.d("admin",admin);
            if(admin.equals("BOSS"))new RefreshDatabase().execute();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    public class RefreshDatabase extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        private int number_of_rss;

        @Override
        protected void onPreExecute() {

            dialog.setMessage("Poczekaj chwilkie, baza jest duża i sie odświeża");
            dialog.setProgressStyle(1);
            dialog.setMax(50);
            dialog.show();
        }
        @Override
        protected String doInBackground(String... urls) {

            InputStream in=null;
            URL url;
            URLConnection connection;


            try {
                url = new URL("http://darqwski.cba.pl/PIDI/antek.php?d");
                connection = url.openConnection();
                in = new BufferedInputStream(connection.getInputStream());
                JSONObject JASON=new JSONObject(streamToString(in));
                number_of_rss= JASON.optInt("number_of_articles");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                dialog.setMax(number_of_rss);
                for (int i = 0; i < number_of_rss; i++) {
                    dialog.setProgress(i);
                    url = new URL("http://darqwski.cba.pl/PIDI/odswiez.php?a="+String.valueOf(i));
                    connection = url.openConnection();
                    in = new BufferedInputStream(
                            connection.getInputStream());
                }
                url = new URL("http://darqwski.cba.pl/PIDI/odswiez.php?b=chujkurwalewypas");
                connection = url.openConnection();
                in = new BufferedInputStream(
                        connection.getInputStream());

                return streamToString(in);

            } catch (Exception e) {

                Log.d(MainActivity.class.getSimpleName(), e.toString());
                return null;
            }

        }
        @Override
        protected void onPostExecute(String result) {

            dialog.dismiss();
            Intent intent=new Intent(getApplicationContext(),FullscreenActivity.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(),"Baza została odświeżona!",Toast.LENGTH_SHORT).show();



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
