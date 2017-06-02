package com.examples.cleanproject;

/**
 * Created by Mac on 2017. 6. 2..
 */

public class NoticeData {
    private String title;
    private String date ;
    private String content;
    private int id;

    public NoticeData(String title, String date ,String content, int id){
        this.title = title;
        this.date = date;
        this.content = content;
        this.id = id;
    }

    public String getDate(){
        return date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }
}
