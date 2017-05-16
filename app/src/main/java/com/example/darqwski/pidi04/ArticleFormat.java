package com.example.darqwski.pidi04;

import android.graphics.Bitmap;

/**
 * Created by Darqwski on 2017-04-11.
 */

public class ArticleFormat {

    public String title;
    public String link;
    public String image_link;
    public String date;
    public String category;
    public String site;
    public String id_article;
    public String site_image_link;
    public String convertedImage;
    public Bitmap final_image;
    public Bitmap site_image_foto;
    public ArticleFormat(){

    }

    public ArticleFormat(String title,String link, String image_link,String date,String category,String site,String site_image_link,String id_article)
    {
        this.id_article=id_article;
        this.title=title;
        this.link=link;
        this.image_link=image_link;
        this.date=date;
        this.category=category;
        this.site=site;
        this.site_image_link=site_image_link;
    }
}
