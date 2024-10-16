package com.example.touristguidemmmd.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TouristAttraction {
    private String name;
    private String description;
    private String by;
    private List<Tag> tagListe;

    //TEST - Ved ikke om det er nyttigt at gemme et eller andet sted.
    private int attractionID;


    public TouristAttraction(String name, String description, String by, List<Tag> tags){
        this.name = name;
        this.description = description;
        this.by = by;
        this.tagListe = new ArrayList<>(tags);
        attractionID = -1;
    }
    public TouristAttraction() {

    }
    //TEST attractionID
    public int getAttractionID() {
        if (attractionID == -1) {
            throw new IllegalArgumentException("The attraction ID for "+this.name+" is invalid.");
        } else {
            return attractionID;
        }
    }
    public void setAttractionID(int newID) {
        this.attractionID = newID;
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

    @Override // denne Override metode bruges til test, hvor to objekter skal sammenlignes med hinanden
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TouristAttraction that = (TouristAttraction) o;
        return Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(by, that.by) && Objects.equals(tagListe, that.tagListe);
    }

    @Override // denne hører til ovennævnte metode
    public int hashCode() {
        return Objects.hash(name, description, by, tagListe);
    }
}



