package com.example.mynotes;

public class Note {

    private String title;
    private String description;
    private  String createdDate;

    public  Note(){ }

//    public Note(String title, String description) {
//        this.title = title;
//        this.description = description;
//    }

    public Note(String title, String description, String date) {
        this.title = title;
        this.description = description;
        this.createdDate = date;
    }

    public  String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String date) {
        this.createdDate = date;
    }
}
