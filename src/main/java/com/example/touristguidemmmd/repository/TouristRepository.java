package com.example.touristguidemmmd.repository;

import com.example.touristguidemmmd.model.Tag;
import com.example.touristguidemmmd.model.TouristAttraction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository // annotation som fortæller Spring, at denne klasse har ansvar for adgang til data (fx databaseadministration)
public class TouristRepository {
    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    private final List<TouristAttraction> touristRepository = new ArrayList<>();

    public TouristRepository() {
//        addHardcodetDataTilListe();
    }

    /////////////////////CRUD/////////////////////
    public void addHardcodetDataTilListe() {
//        touristRepository.add(new TouristAttraction("Tivoli", "Forlystelsespark i centrum af KBH", "København", List.of(Tag.FORLYSTELSE, Tag.PARK, Tag.RESTAURANT)));
//        touristRepository.add(new TouristAttraction("Frederiksberg Have", "Åben park midt på Frederiksberg", "Frederiksberg", List.of(Tag.PARK, Tag.NATUR)));
//        touristRepository.add(new TouristAttraction("Københavns Museum", "Museum i KBH der dækker over københavns historie", "København", List.of(Tag.MUSEUM)));
    }

    public void addTouristAttraction(String name, String description, String by, List<Tag> tags) {
        if(checkIfAttractionAlreadyExist(name)){
            throw new IllegalArgumentException("Attraktion med dette navn eksisterer allerede");
            //denne fejlmeddelelse fanges i Controllerens POST metode (save), hvor der vil
            //komme en meddelelse til brugeren om at attraktionen allerede findes
        }
        touristRepository.add(new TouristAttraction(name, description, by, tags));
    }

    //nedenstående metode benyttes til tjek af om attraktion allerede er oprettet.
    //boolean resultat anvendes så i ovenstående add metode

    public boolean checkIfAttractionAlreadyExist(String name) {
        for(TouristAttraction attraction : touristRepository){
            if(attraction.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }
    public List<Tag> getAttractionTagsFromDB(int attractionID) {
        String sql = "SELECT GROUP_CONCAT(t.tagName SEPARATOR ', ') AS tags FROM attraction a JOIN attractionTag b ON a.attractionID = b.attractionID JOIN tag t ON b.tagID = t.tagID WHERE a.attractionID=?";
        List<Tag> tagsToReturn = new ArrayList<>();

        try(Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, attractionID);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                String concatStrings = rs.getString("tags");
                if (concatStrings != null) {
                    String[] tagArr = concatStrings.split(",\\s*");

                    for (String tagToFind : tagArr) {
                        for (Tag tag : Tag.values()) {
                            if (tag.getDisplayName().equalsIgnoreCase(tagToFind)) {
                                tagsToReturn.add(tag);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tagsToReturn;
    }
    public String getCityFromDB(int attractionID) {
        String sql ="SELECT a.attractionName, a.attractionDesc, b.city  FROM attraction a JOIN location b ON a.postalcode=b.postalcode WHERE a.attractionID=?";
        String cityToReturn=null;

        try(Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, attractionID);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                cityToReturn = rs.getString("city");
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
        return cityToReturn;
    }

    public List<TouristAttraction> getFullTouristRepository() {

        return touristRepository;
    }

    public List<Tag> getListOfTags(String name){
        for (TouristAttraction t : touristRepository){
            if(t.getName().equalsIgnoreCase(name)){
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

    public void updateAttraction (String name,String description, String by, List<Tag> tagListe) {
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
}
