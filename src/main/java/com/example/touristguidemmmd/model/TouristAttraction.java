package com.example.touristguidemmmd.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TouristAttraction {
    private int attractionID;
    private String name;
    private String description;
    private Location postalCode;
    private List<Tag> tagListe;


    public TouristAttraction(String name, String description, Location postalCode, List<Tag> tags){
        this.name = name;
        this.description = description;
        this.postalCode = postalCode;
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
    public Location getPostalCode(){
        return postalCode;
    }

    public void setPostalCode(Location postalCode){
        this.postalCode = postalCode;
    }
    public int getAttractionID(){
        return attractionID;
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
        return Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(postalCode, that.postalCode) && Objects.equals(tagListe, that.tagListe);
    }

    @Override // denne hører til ovennævnte metode
    public int hashCode() {
        return Objects.hash(name, description, postalCode, tagListe);
    }
}



