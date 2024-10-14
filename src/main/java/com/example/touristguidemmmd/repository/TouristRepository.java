package com.example.touristguidemmmd.repository;

import com.example.touristguidemmmd.model.Tag;
import com.example.touristguidemmmd.model.TouristAttraction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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
//        String sql ="SELECT attractionID, attractionName, attractionDesc FROM touristattractiondb.attraction";
//
//        try(Connection con = DriverManager.getConnection(dbUrl, username, password)) {
//            PreparedStatement ps = con.prepareStatement(sql);
//            ResultSet rs = ps.executeQuery();
//
//            while(rs.next()) {
//                TouristAttraction ta = new TouristAttraction();   //TODO: Arbejdskode
//
//                int attractionID = rs.getInt("attractionID");
//                String attractionName = rs.getString("attractionName");
//                String attractionDesc = rs.getString("attractionDesc");
//
//                ta.setAttractionID(attractionID);
//                ta.setName(attractionName);
//                ta.setDescription(attractionDesc);
//                ta.setBy(getCityFromDB(attractionID));
//                ta.setTagListe(getAttractionTagsFromDB(attractionID));
//
//                touristRepository.add()
//            }
//        } catch(SQLException e) {
//            e.printStackTrace();
//        }
        touristRepository.add(new TouristAttraction(name, description, by, tags));
    }
    /*
    ################################################################
    # Tilføjelse(CREATE) til database via TouristAttraction Objekt #
    ################################################################
     */
    public void addTouristAttractionToDB(TouristAttraction ta) {
        if (checkIfAttractionAlreadyExist(ta.getName())) {
            throw new IllegalArgumentException("There is already an attraction with that name.");
        }
        String sqlAddAttraction = "INSERT INTO attraction(attractionID, attractionName, attractionDesc, postalcode) VALUES(?,?,?)";

        try(Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            PreparedStatement ps  = con.prepareStatement(sqlAddAttraction);
            ps.setString(1, ta.getName());
            ps.setString(2, ta.getDescription());
            ps.setInt(3, getPostalCodeFromCityDB(ta));

            ps.executeUpdate();
            //Giver objektets tagværdier videre og assigner dem i SQL i attractiontag
            addTouristAttractionTagsToDB(ta);

        }catch(SQLException e) {
            e.printStackTrace();
        }
    }
    public void addTouristAttractionTagsToDB(TouristAttraction ta) {
        String sqlGetTagID ="SELECT tagID FROM tag where tagName =?";
        String sqlAddTagsToAttractionDB ="INSERT INTO attractiontag(attractionID, tagID) VALUES(?, ?)";

        try(Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            //Hente eksisterende tag
            PreparedStatement psGetTagID = con.prepareStatement(sqlGetTagID);
            //Tilføje forbindelse mellem attractionID og tagID
            PreparedStatement psAddTagToAttraction = con.prepareStatement(sqlAddTagsToAttractionDB);

            psAddTagToAttraction.setInt(1, getAttractionIDFromAttractionName(ta.getName()));

            for (Tag tag : ta.getTagListe()) {
                psGetTagID.setString(1, tag.getDisplayName());
                ResultSet rs = psGetTagID.executeQuery();

                if (rs.next()) {
                    int tagID = rs.getInt("tagID");
                    psAddTagToAttraction.setInt(2, tagID);
                    psAddTagToAttraction.executeUpdate();
                }
            }

        }catch(SQLException e) {
            e.printStackTrace();
        }
    }
    /*
    ############################################################
    #                   GET FROM DATABASE                      #
    ############################################################
     */
    public int getAttractionIDFromAttractionName(String name) {
        String sql = "SELECT attractionID FROM touristattractiondb.attraction WHERE attractionName=?";
        int idToReturn = -1;

        try(Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                idToReturn = rs.getInt("attractionID");
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }

        if (idToReturn == -1) {
            throw new NoSuchElementException("No attraction found with the name: "+name);
        } else {
            return idToReturn;
        }
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
        String sql ="SELECT b.city FROM attraction a JOIN location b ON a.postalcode=b.postalcode WHERE a.attractionID=?";
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
    public int getPostalCodeFromCityDB(TouristAttraction ta) {
        String sql = "SELECT postalcode FROM location WHERE city=?";
        int postalcodeToReturn = -1;

        try(Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, ta.getBy());
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                postalcodeToReturn = rs.getInt("postalcode");
            }

        }catch(SQLException e) {
            e.printStackTrace();
        }
        return postalcodeToReturn;
    }
    /*
    ############################################################
    #                   Full Attraction Lists(READ)            #
    ############################################################
     */
    public List<TouristAttraction> getTouristAttractionsFromDB() {
        String sql ="SELECT attractionName, attractionDesc FROM attraction";

        try(Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

        }catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    //nedenstående metode benyttes til tjek af om attraktion allerede er oprettet.
    //boolean resultat anvendes så i ovenstående add metode

    public boolean checkIfAttractionAlreadyExist(String name) {
        String sql = "SELECT attractionName FROM attraction WHERE LOWER (attractionName)=LOWER(?)";

        try(Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (name.equals(rs.getString("attractionName"))) {
                    return true;
                }
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
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
