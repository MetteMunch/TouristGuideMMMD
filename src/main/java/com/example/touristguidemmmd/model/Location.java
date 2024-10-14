package com.example.touristguidemmmd.model;

import java.time.LocalDate;

public class Location {

    private String city;
    private int postalCode;

    public Location(int postalCode, String city){
        this.postalCode = postalCode;
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String toString(){
        return "Postnummer: "+postalCode+" By: "+city;
    }
}
