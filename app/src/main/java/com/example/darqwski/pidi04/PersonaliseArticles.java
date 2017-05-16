package com.example.darqwski.pidi04;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Darqwski on 2017-04-30.
 */

public class PersonaliseArticles {

     public ArrayList<String> categories=null;
     public ArrayList<Integer> catValues=null;
     public ArrayList<String> sites=null;
     public ArrayList<Integer> sitesValues=null;
     public Context context=null;

    String getBestSearchings() {

        getInfoFromFile();
       ArrayList<String> index=new ArrayList<String>();
       ArrayList<String> indexSite=new ArrayList<String>();
        String returned="";

        for (int i = 0; i < catValues.size(); i++)index.add("");
        for (int i = 0; i < catValues.size(); i++)
        {
            int counter=0;
            for(int i2=0;i2<catValues.size();i2++)
            {
                if(i2!=i&&catValues.get(i).intValue()>catValues.get(i2).intValue())
                {
                    counter++;
                    if(catValues.get(i).equals(catValues.get(i2))&&i2>i)counter--;
                }
            }
            index.set(counter,categories.get(i));
        }
        if(catValues.size()>0)returned+="&favcat=";
        for (int i =catValues.size()-1;i>=0; i--)
        {
            returned+=categories.get(i).toString();
            if(i!=0)returned+="|";
        }

        for (int i = 0; i < sitesValues.size(); i++)index.add("");
        for (int i = 0; i <  sitesValues.size(); i++)
        {
            int counter=0;
            for(int i2=0;i2< sitesValues.size();i2++)
            {
                if(i2!=i&& sitesValues.get(i).intValue()> sitesValues.get(i2).intValue())
                {
                    counter++;
                    if( sitesValues.get(i).equals( sitesValues.get(i2))&&i2>i)counter--;
                }
            }
            index.set(counter,sites.get(i));
        }
        if(sitesValues.size()>0)returned+="&favsit=";
        for (int i = sitesValues.size()-1;i>=0; i--)
        {
            returned+=sites.get(i).toString();
            if(i!=0)returned+="|";
        }
        return returned;

    }
    public void getInfoFromFile()
    {
        String file=file_get_contents("favourites.txt");
        JSONObject json;
        JSONArray JScats=null;
        JSONArray JSsites=null;
        JSONArray JScatValues=null;
        JSONArray JSsiteValues=null;
        if(file!="")  try {
            json = new JSONObject(file);
            JScats=json.getJSONArray("CatsNames");
            JSsites=json.getJSONArray("SiteNames");
            JScatValues=json.getJSONArray("CatsValues");
            JSsiteValues=json.getJSONArray("SiteValues");

        } catch (JSONException e) {
            e.printStackTrace();
            file_put_contents("","favourites.txt");
        }
        categories=new ArrayList<String>();
        catValues=new ArrayList<Integer>();
       sites=new ArrayList<String>();
       sitesValues=new ArrayList<Integer>();
        if(file!="") for(int i=0;i<JScats.length();i++)
        {

            try {
                categories.add(JScats.get(i).toString());
                catValues.add(Integer.parseInt(JScatValues.get(i).toString()));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(file!="")for(int i=0;i<JSsites.length();i++)
        {
            try {
                sites.add(JSsites.get(i).toString());
                sitesValues.add(Integer.parseInt(JSsiteValues.get(i).toString()));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    public void addVisitation(String category,String Site)
    {
        getInfoFromFile();
        boolean isSite=false;
        boolean isCat=false;

        if(sites!=null)
        {
            for(int i=0;i<sites.size();i++)
            {
                if(sites.get(i).equals(Site))
                {
                    sitesValues.set(i,sitesValues.get(i)+1);
                    isSite=true;
                    break;
                }

            }
            if(!isSite)
            {

                sitesValues.add(1);
                sites.add(Site);
            }
        }
        else
        {

            sites=new ArrayList<String>();
            sitesValues=new ArrayList<Integer>();
            sitesValues.add(1);
            sites.add(Site);
        }
        if(categories!=null) {

            for(int i=0;i<categories.size();i++)
            {
                if(categories.get(i).equals(category))
                {
                    catValues.set(i,catValues.get(i)+1);
                    isCat=true;
                    break;
                }
            }
            if(!isCat)
            {
                catValues.add(1);
                categories.add(category);
            }
        }
        else
        {
            categories=new ArrayList<String>();
            catValues=new ArrayList<Integer>();
            catValues.add(1);
            categories.add(category);
        }


        saveToFile(categories,sites,catValues,sitesValues);


    }
    public void deleteVisitation(String category,String Site)
    {

    }

     public void saveToFile(ArrayList<String> CatNames,ArrayList<String> SiteNames,ArrayList<Integer> CatValues,ArrayList<Integer> SiteValues)
     {
        sites=SiteNames;
        categories=CatNames;
         catValues=CatValues;
        sitesValues=SiteValues;

         String data="{";
         data+="\"SiteNames\":[";
         for(int i=0;i<sites.size();i++)
         {
             String a="\""+sites.get(i)+"\"";
             if(i+1!=sites.size())a+=",";
            data+=a;
         }
         data+="],";

         data+="\"CatsNames\":[";
         for(int i=0;i<categories.size();i++)
         {
             String a="\""+categories.get(i)+"\"";
             if(i+1!=categories.size())a+=",";
             data+=a;
         }
         data+="],";

         data+="\"SiteValues\":[";
         for(int i=0;i<sitesValues.size();i++)
         {
             String a="\""+sitesValues.get(i)+"\"";
             if(i+1!=sitesValues.size())a+=",";
             data+=a;
         }
         data+="],";

         data+="\"CatsValues\":[";
         for(int i=0;i<catValues.size();i++)
         {
             String a="\""+catValues.get(i)+"\"";
             if(i+1!=catValues.size())a+=",";
             data+=a;
         }
         data+="]}";

         Log.d("Site",String.valueOf(sitesValues));
         file_put_contents(data,"favourites.txt");

     }


    public PersonaliseArticles(Context context)
{
    this.context=context;

}

    String file_get_contents(String pathy)
    {
        File file = new File(context.getFilesDir(), pathy);
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
            outputStream = context.openFileOutput(where, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
