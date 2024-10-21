package com.example.touristguidemmmd.repository;

import com.example.touristguidemmmd.model.Tag;
import com.example.touristguidemmmd.model.TouristAttraction;
import org.springframework.beans.factory.annotation.Value;
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
        String SQLattractionTag = "INSERT INTO attractiontag (attractionID, tagID) VALUES (?,?)";
        String SQLtag = "SELECT tagID FROM tag WHERE tagName = ?";


        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmtAttraction = con.prepareStatement(SQLattraction, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement pstmtTag = con.prepareStatement(SQLattractionTag);
             PreparedStatement pstmtTagSelect = con.prepareStatement((SQLtag))) {

            pstmtAttraction.setString(1, name);
            pstmtAttraction.setString(2, description);
            pstmtAttraction.setInt(3, getPostalCode(by));
            int affectedRows = pstmtAttraction.executeUpdate();

            if (affectedRows > 0) {
                //Hvis der bliver oprettet en attraktion skal autogenereret nøgle hentes
                try (ResultSet generatedKeys = pstmtAttraction.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int attractionID = generatedKeys.getInt(1);

                        //iterer genne Taglisten og behandler hvert tag
                        for (Tag tag : tags) {
                            pstmtTagSelect.setString(1, tag.name()); //bruger Enum tag navnet som tagName
                            try (ResultSet rsTag = pstmtTagSelect.executeQuery()) {
                                if (rsTag.next()) {
                                    int tagID = rsTag.getInt("tagID");

                                    //indsæt link mellem attraktion og tag i attractionTag tabellen
                                    pstmtTag.setInt(1, attractionID);
                                    pstmtTag.setInt(2, tagID);
                                    pstmtTag.executeUpdate();

                                }
                            }
                        }


                    }
                }
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }


    }

    //Nedenstående metode laver kald til databasen, så vi kan få postnummer på den angivne by
    public int getPostalCode(String by) {
        int result = 0;
        String SQL = "SELECT postalcode FROM location WHERE city = ?";
        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = con.prepareStatement(SQL)) {

            pstmt.setString(1, by);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { //flytter markøren til den første række
                    result = rs.getInt(1); //returnerer værdien af forespørgslen (dvs. postnummer)
                }
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return result;
    }

    //Nedenstående metode tjekker i databasen om attraktionen allerede eksisterer
    public boolean checkIfAttractionAlreadyExist(String attractionName) {
        boolean result = false;
        String SQL = "SELECT COUNT(*) FROM attraction WHERE attractionName = ?";
        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = con.prepareStatement(SQL)) {

            pstmt.setString(1, attractionName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { //flytter pointeren til den første række
                    result = rs.getInt(1) > 0; //Hvis der allerede er en attraktion med det navn, så bliver denne true og ellers false
                }
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return result;
    }


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


    public void updateAttraction(String name, String description, String by, List<Tag> tags) {
        int attID = getByNameTouristRepository(name).getAttractionID();
        if (attID == 0) {
            throw new IllegalArgumentException("Attraktion med dette navn eksisterer ikke");
            //denne fejlmeddelelse skal fanges i Controllerens metode, hvor der skal
            //komme en meddelelse til brugeren om at attraktionen ikke findes og derfor ikke kan updateres
        }

        String SQLattraction = "UPDATE attraction SET attractionDesc = ?, postalcode = ? WHERE attractionID = ?";
        String SQLdeleteTags = "DELETE FROM attractiontag WHERE attractionID = ?";
        //Forespørgslen ved opdatering af attractionTag bliver som INSERT INTO i stedet for UPDATE da sammensat nøgle
        String SQLattractionTag = "INSERT INTO attractiontag (attractionID, tagID) VALUES (?,?)";
        String SQLtag = "SELECT tagID FROM tag WHERE tagName = ?";


        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmtAttraction = con.prepareStatement(SQLattraction);
             PreparedStatement pstmtDelete = con.prepareStatement(SQLdeleteTags);
             PreparedStatement pstmtTag = con.prepareStatement(SQLattractionTag);
             PreparedStatement pstmtTagSelect = con.prepareStatement((SQLtag))) {

            // HER OPDATERES ATTRAKTIONEN (attraction tabellen)
            pstmtAttraction.setString(1, description);
            pstmtAttraction.setInt(2, getPostalCode(by));
            pstmtAttraction.setInt(3, attID);
            pstmtAttraction.executeUpdate();

            //HER SLETTES DE OPRINDELIGE TAGS SÆT (attractiontac tabellen)
            pstmtDelete.setInt(1, attID);
            pstmtDelete.executeUpdate();


            //HER ITERERES GENNEM TAGLISTEN FOR AT FINDE TAG NAVN OG TAG ID
            for (Tag tag : tags) {
                pstmtTagSelect.setString(1, tag.name()); //bruger Enum tag navnet som tagName
                try (ResultSet rsTag = pstmtTagSelect.executeQuery()) {
                    if (rsTag.next()) {
                        int tagID = rsTag.getInt("tagID");

                        //HER INDSÆTTES DE NYE LINK MELLEM ATTRACTIONID OG TAGID (attractionTag tabellen)
                        pstmtTag.setInt(1, attID);
                        pstmtTag.setInt(2, tagID);
                        pstmtTag.executeUpdate();

                    }
                }
            }


        } catch (SQLException exception) {
            exception.printStackTrace();
        }


    }

    public String getDescription(String name) {
        String result = "";

        String SQL = "SELECT attractionDesc FROM attraction WHERE attractionName = ?";

        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = con.prepareStatement(SQL)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                result = rs.getString("attractionDesc");
            } else {
                result = "Denne attraktion findes ikke i oversigten";
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return result;
    }


    public void deleteAttraction(TouristAttraction ta) {

        int attractionID = ta.getAttractionID();

        String SQL = "DELETE FROM attraction WHERE attractionID = ?";

        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = con.prepareStatement(SQL)) {
            pstmt.setInt(1, attractionID);
            pstmt.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public List<String> getFullLocationList() {
        List<String> allLocations = new ArrayList<>();

        String SQL = "SELECT city FROM location";

        try (Connection con = DriverManager.getConnection(url, user, pass);
             Statement stmt = con.createStatement()) {

            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                allLocations.add(rs.getString("city"));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return allLocations;
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
