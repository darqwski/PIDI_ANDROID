package com.example.darqwski.pidi04;

/**
 * Created by Darqwski on 2017-04-11.
 */

public class FiltrFormat {
    public String name;
    public String content;

    public String toAddress(String sth)
    {
        return "http://darqwski.cba.pl/PIDI/antek.php?filtr="+ sth + "&count=";
    }
    public FiltrFormat(String a,String b)
    {
        name=a;
        content=b;
    }
}
