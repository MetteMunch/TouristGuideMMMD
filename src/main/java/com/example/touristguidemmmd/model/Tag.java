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

    //nedenstående metode returnerer Tag og ikke String, som skal bruges i vores getListOfTags metode, da data fra
    //databasen skal konverteres til Tags og tilføjes en liste med Tags
    // Metoden skal være statisk da den arbejder med hele Tag typen, og ikke en specifik instans af Tag (Enum kan ikke instatieres)
    public static Tag getEnumTag(String displayName) {
        for(Tag tag: Tag.values()) {
            if(tag.getDisplayName().equalsIgnoreCase(displayName)) {
                return tag;
            }
        }
        throw new IllegalArgumentException("Unknown tag: " +displayName);
    }
}
