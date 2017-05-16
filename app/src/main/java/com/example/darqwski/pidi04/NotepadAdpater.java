package com.example.darqwski.pidi04;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by Darqwski on 2017-04-11.
 */
public class NotepadAdpater extends ArrayAdapter<ArticleFormat> {

    Context context;
    int layoutResourceId;
    ArrayList<ArticleFormat> data = null;

    public NotepadAdpater(Context context, int layoutResourceId, ArrayList< ArticleFormat> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Bitmap b = null;

        RowBeanHolder holder = null;
        if(row == null)
        {
            ArticleFormat object = data.get(position);
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RowBeanHolder();
            holder.txtTitle= (TextView)row.findViewById(R.id.txtTitle);
            holder.img=(ImageView)row.findViewById(R.id.imageView);
            holder.Category=(TextView)row.findViewById(R.id.categoty);
            holder.SiteLink=(TextView)row.findViewById(R.id.SiteLink);
            holder.Siteimg=(ImageView)row.findViewById(R.id.SiteImage);
            holder.date=(TextView) row.findViewById(R.id.dateview);
            holder.bg1=(TextView)row.findViewById(R.id.textView5);
            holder.bg2=(TextView)row.findViewById(R.id.textView6);
            holder.saveTobasee=(TextView)row.findViewById(R.id.ArticleSaveButton);
            holder.Layout=(RelativeLayout)row.findViewById(R.id.ArticleView) ;
            holder.saveTobasee.setText("Usuń z notatnika");
//data/data/com.example.darqwski.pidi031/files
            //   holder.star=(Button)row.findViewById(R.id.StarButton);
            row.setTag(holder);
        }
        else
        {
            holder = (RowBeanHolder)row.getTag();
        }


        final ArticleFormat object = data.get(position);
        holder.txtTitle.setText(object.title);
        holder.img_adress=object.image_link;
        holder.Category.setText("Dział :   "+object.category);
        holder.SiteLink.setText(object.site);
        holder.img_site_adress=object.site_image_link;
        holder.date.setText("Z dnia "+object.date);
        holder.id=object.id_article;
        new LongOperation2().execute(holder);
      /*  holder.Layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN: {
                        downX = event.getX();
                        downY = event.getY();
                        mSwipeDetected = SwipeDetector.Action.None;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        upX = event.getX();
                        upY = event.getY();

                        deltaX =  (upX-downX);
                        v.setLeft((int)deltaX);


                        return true;
                    }
                    case MotionEvent.ACTION_CANCEL:
                    {
                        v.setLeft(0);
                    }

                }
                return false;
            }
        });*/
        holder.saveTobasee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<ArticleFormat> savedArticles=new ArrayList<ArticleFormat>();
                File file = new File(getContext().getFilesDir(), "savedlinks.txt");
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
                int where = 0;
                String contents = new String(bytes);
                try {
                    JSONObject json=new JSONObject(contents);
                    JSONArray jsonArrayTitle=json.getJSONArray("title");
                    JSONArray jsonArrayLink=json.getJSONArray("link");
                    JSONArray jsonArrayImage=json.getJSONArray("image");
                    JSONArray jsonArrayDate=json.getJSONArray("date");
                    JSONArray jsonArraySite=json.getJSONArray("site");
                    JSONArray jsonArrayCategory=json.getJSONArray("category");
                    JSONArray jsonArraySiteImg=json.getJSONArray("siteimg");
                    numberofarticles=jsonArrayTitle.length();
                    Log.d("a",String.valueOf(numberofarticles));
                    for(int i=0;i<numberofarticles;i++)
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

                numberofarticles=savedArticles.size();

                for(int i=0;i<numberofarticles;i++)
                {
                    Log.d(savedArticles.get(i).title,object.title);
                    if(savedArticles.get(i).title.equals(object.title))
                    {
                        Log.d("Chyba","pyklo");
                        where=i;
                    }
                }


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
                    for(int i=0;i<numberofarticles;i++)if(i!=where)
                    {
                        if(i+1!=numberofarticles&&!(i + 1 == where && i + 2 == numberofarticles)) {

                            ArticleFormat help = savedArticles.get(i);
                            titleLine += "\"" + help.title + "\",";
                            linkLine += "\"" + help.link + "\",";
                            imageLine += "\"" + help.image_link + "\",";
                            dateLine += "\"" + help.date + "\",";
                            siteLine += "\"" + help.site + "\",";
                            categotyLine += "\"" + help.category + "\",";
                            siteimgLine += "\"" + help.site_image_link + "\",";

                        }
                        else if((i + 1 == where && i + 2 == numberofarticles))
                        {

                            ArticleFormat help=savedArticles.get(i); Log.d("TITLE",help.title);
                            titleLine+="\""+help.title+"\"";
                            linkLine+="\""+help.link+"\"";
                            imageLine+="\""+help.image_link+"\"";
                            dateLine+="\""+help.date+"\"";
                            siteLine+="\""+help.site+"\"";
                            categotyLine+="\""+help.category+"\"";
                            siteimgLine+="\""+help.site_image_link+"\"";
                        }

                        //   NameLine+="\""+names.get(i)+"\"";
                        else {

                            ArticleFormat help=savedArticles.get(i); Log.d("TITLE",help.title);
                            titleLine+="\""+help.title+"\"";
                            linkLine+="\""+help.link+"\"";
                            imageLine+="\""+help.image_link+"\"";
                            dateLine+="\""+help.date+"\"";
                            siteLine+="\""+help.site+"\"";
                            categotyLine+="\""+help.category+"\"";
                            siteimgLine+="\""+help.site_image_link+"\"";
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
Log.d("ALL",contents);
                AllData="{"+titleLine+ linkLine+ imageLine+ dateLine+ siteLine+ categotyLine+ siteimgLine+"}";
                Log.d("TitleLine",titleLine);
                Log.d("LinkLine",linkLine);
                Log.d("POSZUKIWANY",savedArticles.get(where).title);
                Log.d("POSZUKIWANY","NIC");
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
                file = new File(getContext().getFilesDir(), "savedlinks.txt");
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
                Toast.makeText(getContext(),"Usunięto",Toast.LENGTH_SHORT).show();
                Intent goTo = new Intent(context, MainActivity.class);
                goTo.putExtra("Type","http://darqwski.cba.pl/PIDI/antek.php?today=");
                context.startActivity(goTo);

            }


        });

        holder.img.setImageBitmap(Base64ToBitmap(object.image_link));

        return row;
    }

    static class RowBeanHolder
    {
        RelativeLayout Layout;
        TextView txtTitle;
        TextView date;
        TextView saveTobasee;
        String id;
        TextView bg1;
        TextView bg2;
        ImageView img;
        ImageView Siteimg;
        TextView SiteLink;
        TextView Category;
        String img_adress;
        String img_site_adress;
        Bitmap back_bitmap;
        Bitmap back_site_bitmap;
    }

    private class LongOperation2 extends AsyncTask< RowBeanHolder, Void, RowBeanHolder> {

        @Override
        protected RowBeanHolder doInBackground( RowBeanHolder... params) {
            params[0].back_bitmap=getBitmapFromURL(params[0].img_site_adress);
            return params[0];
        }

        @Override
        protected void onPostExecute(RowBeanHolder result) {

            result.Siteimg.setImageBitmap( result.back_bitmap);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Bitmap Base64ToBitmap(String name)
    {
        try
        {
            String pureBase64Encoded = name.substring(name.indexOf(",")  + 1);
            byte[] decodedString = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return decodedByte;

        }
        catch (Exception e)
        {
            Log.d("A",name);
            return null;
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
