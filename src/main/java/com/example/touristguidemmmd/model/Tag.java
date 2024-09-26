package com.example.touristguidemmmd.model;

public enum Tag {
    TEATER("Teater"),
    RESTAURANT("Restaurant"),
    KONCERT("Koncert"),
    NATUR("Natur"),
    MUSEUM("Museum"),
    SPORT("Sport"),
    PARK("Park"),
    FORLYSTELSE("Forlystelse"),
    DESIGN("Design"),
    ARKITEKTUR("Arkitektur"),
    MONUMENTER("Monumenter");


    private final String displayName;

    Tag(String displayName) { //Enum constructor må ikke være public
        this.displayName = displayName;
    }

        public String getDisplayName() {
            return displayName;
        }
    }
