package com.example.touristguidemmmd.repository;

import com.example.touristguidemmmd.model.Tag;
import com.example.touristguidemmmd.model.TouristAttraction;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
// annotation som fortæller Spring, at denne klasse har ansvar for adgang til data (fx databaseadministration)
public class TouristRepository {

    private final List<TouristAttraction> touristRepository = new ArrayList<>();
    public String url = System.getenv("DB_URL");
    public String user = System.getenv("DB_USER");
    public String pass = System.getenv("DB_PASS");

    public TouristRepository() {
        //addHardcodetDataTilListe();
    }

    /////////////////////CRUD/////////////////////
    public void addHardcodetDataTilListe() {
        touristRepository.add(new TouristAttraction("Tivoli", "Forlystelsespark i centrum af KBH", "København", List.of(Tag.FORLYSTELSE, Tag.PARK, Tag.RESTAURANT)));
        touristRepository.add(new TouristAttraction("Frederiksberg Have", "Åben park midt på Frederiksberg", "Frederiksberg", List.of(Tag.PARK, Tag.NATUR)));
        touristRepository.add(new TouristAttraction("Københavns Museum", "Museum i KBH der dækker over københavns historie", "København", List.of(Tag.MUSEUM)));
    }

    public void addTouristAttraction(String name, String description, String by, List<Tag> tags) {
        if (checkIfAttractionAlreadyExist(name)) {
            throw new IllegalArgumentException("Attraktion med dette navn eksisterer allerede");
            //denne fejlmeddelelse fanges i Controllerens POST metode (save), hvor der vil
            //komme en meddelelse til brugeren om at attraktionen allerede findes
        }
        touristRepository.add(new TouristAttraction(name, description, by, tags));
    }

    //nedenstående metode benyttes til tjek af om attraktion allerede er oprettet.
    //boolean resultat anvendes så i ovenstående add metode

    public boolean checkIfAttractionAlreadyExist(String name) {
        for (TouristAttraction attraction : touristRepository) {
            if (attraction.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public List<TouristAttraction> getFullTouristRepository() {
        Map<Integer, TouristAttraction> attractionMap = new HashMap<>(); // denne map skal vi bruge til at gemme unikke attraktioner efter kald i databasen, så der ikke
        //oprettes flere af den samme attraktion, hvis den har flere tags tilknyttet.
        List<TouristAttraction> fullAttractionList = new ArrayList<>(); //den endelige liste, som returneres

        try (
                Connection con = DriverManager.getConnection(url, user, pass);
                Statement stmt = con.createStatement()) {

            String SQL = "SELECT attraction.attractionID AS ID, attraction.attractionName AS attName, \n" +
                    "attraction.attractionDesc AS desp, location.city AS city, tag.tagName AS tag FROM attraction\n" +
                    "JOIN location ON\n" +
                    "attraction.postalcode = location.postalcode\n" +
                    "JOIN attractiontag ON\n" +
                    "attraction.attractionID = attractiontag.attractionID\n" +
                    "JOIN tag ON\n" +
                    "tag.tagID = attractiontag.tagID;";

            ResultSet rs = stmt.executeQuery(SQL);

            while(rs.next()){
                int attractionID = rs.getInt("ID"); //attribut der skal bruges i Map
                String tagName = rs.getString("tag"); //attribut der skal bruges i Map

                TouristAttraction ta =attractionMap.get(attractionID);
                if(ta == null) {
                    ta = new TouristAttraction(
                            rs.getString("attName"),
                            rs.getString("desp"),
                            rs.getString("city"),
                            new ArrayList<>());
                    attractionMap.put(attractionID,ta);
                }

                //Her tilknytter vi enum værdier baseret på eventuelle String Tagnames fra databasen via hjælpemetoden
                Tag tag = convertStringToTag(tagName);
                if(tag != null) {
                    ta.getTagListe().add(tag); //Tagget tilknyttes til TouristAttraction
                }
            }
            fullAttractionList.addAll(attractionMap.values());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fullAttractionList;
    }

    public List<Tag> getListOfTags(String name) {
        for (TouristAttraction t : touristRepository) {
            if (t.getName().equalsIgnoreCase(name)) {
                return t.getTagListe();
            }
        }
        return null;

    }

    public TouristAttraction getByNameTouristRepository(String name) {
        for (TouristAttraction t : touristRepository) {
            if (t.getName().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }

    public void updateAttraction(String name, String description, String by, List<Tag> tagListe) {
        for (TouristAttraction t : touristRepository) {
            if (name.equalsIgnoreCase(t.getName())) {
                t.setDescription(description);
                t.setBy(by);
                t.setTagListe(tagListe);
            }
        }
    }

    public String getDescription(String name) {
        String result = "";
        for (TouristAttraction t : touristRepository) {
            if (name.equalsIgnoreCase(t.getName())) {
                result = t.getDescription();
            }
        }
        return result;
    }


    public void deleteAttraction(TouristAttraction ta) {
        touristRepository.remove(ta);
    }

    //////////////////hjælpe metoder////////////////////

    //metode til at konvertere String tag navnet fra databasen til tag enum værdi
    private Tag convertStringToTag(String tagName) {
        for(Tag tag: Tag.values()) { //Her ittererer vi igennem vores enum værdier
            if(tag.getDisplayName().equalsIgnoreCase(tagName)) {
                return tag;
            }
        }
        return null;
    }
}
