package com.example.touristguidemmmd.model;

import java.util.ArrayList;
import java.util.List;

public class TouristAttraction {
    private String name;
    private String description;
    private String by;
    private List<Tag> tagListe;


    public TouristAttraction(String name, String description, String by, List<Tag> tags){
        this.name = name;
        this.description = description;
        this.by = by;
        this.tagListe = new ArrayList<>(tags);
    }
    public TouristAttraction() {

    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }
    public String getBy(){
        return by;
    }

    public void setBy(String by){
        this.by = by;
    }

    public List<Tag> getTagListe(){
        return tagListe;
    }

    public void setTagListe(List<Tag> tagListe){
        this.tagListe = tagListe;
    }




    @Override
    public String toString(){
        return name + " "+ description;
    }
}
