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


    public void addTouristAttraction(String name, String description, String by, List<Tag> tags) {
        if (checkIfAttractionAlreadyExist(name)) {
            throw new IllegalArgumentException("Attraktion med dette navn eksisterer allerede");
            //denne fejlmeddelelse fanges i Controllerens POST metode (save), hvor der vil
            //komme en meddelelse til brugeren om at attraktionen allerede findes
        }

        String SQLattraction = "INSERT INTO attraction (attractionName, attractionDesc, postalcode) VALUES (?,?,?)";
        String SQLattractionTag = "INSERT INTO attractiontag (atractionID, tagID) VALUES (?,?)";


        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmtAttraction = con.prepareStatement(SQLattraction, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement pstmtTag = con.prepareStatement(SQLattractionTag)) {

            pstmtAttraction.setString(1, name);
            pstmtAttraction.setString(2, description);
            pstmtAttraction.setInt(3, getPostalCode(by));
            int affectedRows = pstmtAttraction.executeUpdate();

            if (affectedRows > 0) {
                //Hvis der bliver oprettet en attraktion skal autogenereret nøgle hentes
                try (ResultSet generatedKeys = pstmtAttraction.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int attractionID = generatedKeys.getInt(1);

                        pstmtTag.setInt(1, attractionID);
                        pstmtTag.setInt(2,);
                    }
                }
            }

        }


    }

    //Nedenstående metode laver kald til databasen, så vi kan få postnummer på den angivne by
    public int getPostalCode(String by) {
        String SQL = "SELECT postalcode FROM location WHERE city = ?";
        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = con.prepareStatement(SQL)) {

            pstmt.setString(1, by);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.getInt(1); //returnerer værdien af forespørgslen (dvs. postnummer)
            }

        }
    }

    //Nedenstående metode tjekker i databasen om attraktionen allerede eksisterer
    public boolean checkIfAttractionAlreadyExist(String attractionName) {
        String SQL = "SELECT COUNT(*) FROM attraction WHERE attractionName = ?";
        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = con.prepareStatement(SQL)) {

            pstmt.setString(1, attractionName);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.getInt(1) > 0; //Hvis der allerede er en attraktion med det navn, så bliver denne true og ellers false
            }

        }
    }


//    NEDENSTÅENDE TO METODER ER OPRETTELSE TIL LISTE OG IKKE DATABASE
//
//    public void addTouristAttraction(String name, String description, String by, List<Tag> tags) {
//        if (checkIfAttractionAlreadyExist(name)) {
//            throw new IllegalArgumentException("Attraktion med dette navn eksisterer allerede");
//            //denne fejlmeddelelse fanges i Controllerens POST metode (save), hvor der vil
//            //komme en meddelelse til brugeren om at attraktionen allerede findes
//        }
//        touristRepository.add(new TouristAttraction(name, description, by, tags));
//    }
//
//    //nedenstående metode benyttes til tjek af om attraktion allerede er oprettet.
//    //boolean resultat anvendes så i ovenstående add metode
//
//    public boolean checkIfAttractionAlreadyExist(String name) {
//        for (TouristAttraction attraction : touristRepository) {
//            if (attraction.getName().equalsIgnoreCase(name)) {
//                return true;
//            }
//        }
//        return false;
//    }

    public List<TouristAttraction> getFullTouristRepository() {
        Map<Integer, TouristAttraction> attractionMap = new HashMap<>(); // denne map skal vi bruge til at gemme unikke attraktioner efter kald i databasen, så samme
        //attraktion ikke medtages flere gange, hvis den har flere tags tilknyttet.
        List<TouristAttraction> fullAttractionList = new ArrayList<>(); //den endelige liste, som returneres

        try (  //try catch med ressource, som lukkes hvis ikke true
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

            while (rs.next()) {
                int attractionID = rs.getInt("ID"); //attribut der skal bruges i Map
                String tagName = rs.getString("tag"); //attribut der skal bruges i Map

                TouristAttraction ta = attractionMap.get(attractionID);
                if (ta == null) {
                    ta = new TouristAttraction(
                            rs.getString("attName"),
                            rs.getString("desp"),
                            rs.getString("city"),
                            new ArrayList<>());
                    attractionMap.put(attractionID, ta);
                    ta.setAttractionID(attractionID);
                }

                //Her tilknytter vi enum værdier baseret på eventuelle String Tagnames fra databasen via hjælpemetoden
                Tag tag = convertStringToTag(tagName);
                if (tag != null) {
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
        List<Tag> tagListe = new ArrayList<>();

        String SQL = "SELECT tag.tagName FROM tag\n" +
                "    JOIN attractiontag ON\n" +
                "    attractiontag.tagID = tag.tagID\n" +
                "    JOIN attraction ON\n" +
                "    attraction.attractionID = attractiontag.attractionID\n" +
                "    WHERE attraction.attractionName = ?";

        try ( //try catch med ressource, som lukkes hvis ikke true
              Connection con = DriverManager.getConnection(url, user, pass);
              PreparedStatement pstmt = con.prepareStatement(SQL)) {

            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String tagName = rs.getString("tagName");
                    //ovenstående String tagName skal konverteres til Tag
                    Tag tag = Tag.getEnumTag(tagName);
                    tagListe.add(tag);
                }
            }
        } catch (SQLException | IllegalArgumentException exception) {
            exception.printStackTrace();
        }

        return tagListe;

    }


    public TouristAttraction getByNameTouristRepository(String name) {
        TouristAttraction ta = null;
        Map<Integer, TouristAttraction> attractionMap = new HashMap<>(); // denne map skal vi bruge til at få
        // attraktionen med kun én gang efter kald i databasen. Hvis en attraktion har flere tags tilknyttet vil
        //den ellers blive vist flere gange.

        String SQL = "SELECT attraction.attractionID AS ID, attraction.attractionName AS attName, \n" +
                "attraction.attractionDesc AS desp, location.city AS city, tag.tagName AS tag FROM attraction\n" +
                "JOIN location ON\n" +
                "attraction.postalcode = location.postalcode\n" +
                "JOIN attractiontag ON\n" +
                "attraction.attractionID = attractiontag.attractionID\n" +
                "JOIN tag ON\n" +
                "tag.tagID = attractiontag.tagID\n" +
                "HAVING attName = ?;";

        try (  //try catch med ressource, som lukkes hvis ikke true
               Connection con = DriverManager.getConnection(url, user, pass);
               PreparedStatement pstmt = con.prepareStatement(SQL)) {

            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int attractionID = rs.getInt("ID"); //attribut der skal bruges i Map
                    String tagName = rs.getString("tag"); //attribut der skal bruges i Map

                    ta = attractionMap.get(attractionID);
                    if (ta == null) {
                        ta = new TouristAttraction(
                                rs.getString("attName"),
                                rs.getString("desp"),
                                rs.getString("city"),
                                new ArrayList<>());
                        attractionMap.put(attractionID, ta);
                        ta.setAttractionID(attractionID);

                    }

                    //Her tilknytter vi enum værdier baseret på eventuelle String Tagnames fra databasen via hjælpemetoden
                    Tag tag = convertStringToTag(tagName);
                    if (tag != null) {
                        ta.getTagListe().add(tag); //Tagget tilknyttes til TouristAttraction
                    }

                }
            }

        } catch (SQLException | IllegalArgumentException exception) {
            exception.printStackTrace();
        }
        return ta;
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
        for (Tag tag : Tag.values()) { //Her ittererer vi igennem vores enum værdier
            if (tag.getDisplayName().equalsIgnoreCase(tagName)) {
                return tag;
            }
        }
        return null;
    }
}
