package com.example.touristguidemmmd.repository;

import com.example.touristguidemmmd.model.Tag;
import com.example.touristguidemmmd.model.TouristAttraction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
// annotation som fortæller Spring, at denne klasse har ansvar for adgang til data (fx databaseadministration)
public class TouristRepository {
    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    private final List<TouristAttraction> touristRepository = new ArrayList<>();

    public TouristRepository() {
    }

    /////////////////////CRUD/////////////////////
    public void addTouristAttraction(String name, String description, String by, List<Tag> tags) {
        if (checkIfAttractionAlreadyExist(name)) {
            throw new IllegalArgumentException("Attraktion med dette navn eksisterer allerede");
            //denne fejlmeddelelse fanges i Controllerens POST metode (save), hvor der vil
            //komme en meddelelse til brugeren om at attraktionen allerede findes
        }

        TouristAttraction ta = new TouristAttraction(name, description, by, tags);
        addTouristAttractionAndTagsToDB(ta);

        System.out.println(ta.getAttractionID());
    }

    /*
    ##################################################################
    # Tilføjelse(CREATE) til database via TouristAttraction Objekt #
    ################################################################
     */
    public void addTouristAttractionAndTagsToDB(TouristAttraction ta) {
        String sqlAddAttraction = "INSERT INTO attraction(attractionName, attractionDesc, postalcode) VALUES(?,?,?)";

        try (Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            PreparedStatement ps = con.prepareStatement(sqlAddAttraction);
            ps.setString(1, ta.getName());
            ps.setString(2, ta.getDescription());
            ps.setInt(3, getPostalCodeFromCityDB(ta));
            ps.executeUpdate();

            int attractionID = lookUpTouristAttractionIDFromDB(ta);
            ta.setAttractionID(attractionID);
            //Giver objektets tagværdier videre og assigner dem i SQL i attractiontag
            addTouristAttractionTagsToDB(ta);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void resetPrimaryKeyIncrement() { //Bruger denne metode til at 'nulstille' auto increment, ellers hopper den over primary keys, der allerede er brugte, selv efter sletning.
        String sql ="ALTER TABLE attraction AUTO_INCREMENT = 1";

        try(Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.executeUpdate();

        }catch(SQLException e) {
            e.printStackTrace();
        }
    }
    public int lookUpTouristAttractionIDFromDB(TouristAttraction ta) {
        String SQL = "SELECT attractionID FROM attraction WHERE attractionName=?";
        int attractionID = -1;
        try(Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, ta.getName());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
              attractionID = rs.getInt("attractionID");
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
        if (attractionID == -1) {
            throw new IllegalArgumentException("Not a valid AttractionID TR L111");
        } else {
            return attractionID;
        }
    }

    public void addTouristAttractionTagsToDB(TouristAttraction ta) {

        String sqlGetTagID = "SELECT tagID FROM tag where tagName =?";
        String sqlAddTagsToAttractionDB = "INSERT INTO attractiontag(attractionID, tagID) VALUES(?, ?)";

        try (Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            //Hente eksisterende tag
            PreparedStatement psGetTagID = con.prepareStatement(sqlGetTagID);
            //Tilføje forbindelse mellem attractionID og tagID
            PreparedStatement psAddTagToAttraction = con.prepareStatement(sqlAddTagsToAttractionDB);
            psAddTagToAttraction.setInt(1, getAttractionIDFromAttractionName(ta.getName()));

            deleteExistingAttractionTagsFromDB(ta);
                /*
                //^^Hvis ikke vi vælger at slette eksisterende tags, så skal der laves checks på, hvilke tagrelationer
                der i forvejen foreligger. Det her var den lettere løsning. På denne måde holder vi en 'ren' database.
                 */
            System.out.println("Repository L115 Deleting tags");
            for (Tag tag : ta.getTagListe()) {
                psGetTagID.setString(1, tag.getDisplayName());
                ResultSet rs = psGetTagID.executeQuery();

                if (rs.next()) {
                    int tagID = rs.getInt("tagID");
                    psAddTagToAttraction.setInt(2, tagID);
                    psAddTagToAttraction.executeUpdate();
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteExistingAttractionTagsFromDB(TouristAttraction ta) {
        String sqlDeleteTags = "DELETE FROM attractiontag WHERE attractionID=?";

        try (Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            PreparedStatement ps = con.prepareStatement(sqlDeleteTags);
            ps.setInt(1, ta.getAttractionID());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /*
    ############################################################
    #                   GET FROM DATABASE                      #
    ############################################################
     */
    public int getAttractionIDFromAttractionName(String name) {
//        String sql = "SELECT attractionID FROM touristattractiondb.attraction WHERE LOWER(attractionName)=LOWER(?)";
        String sql = "SELECT attractionID FROM attraction WHERE LOWER(attractionName)=LOWER(?)";
        //LOWER() er egentlig bare .toLowerCase. Bruges som equalsIgnoreCase() i det her tilfælde.
        int idToReturn = -1;

        try (Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                idToReturn = rs.getInt("attractionID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (idToReturn == -1) {
            throw new NoSuchElementException("No attraction found with the name: " + name);
        } else {
            return idToReturn;
        }
    }

    public List<Tag> getAttractionTagsFromDB(int attractionID) {
        String sql = "SELECT GROUP_CONCAT(t.tagName SEPARATOR ', ') AS tags FROM attraction a JOIN attractionTag b ON a.attractionID = b.attractionID JOIN tag t ON b.tagID = t.tagID WHERE a.attractionID=?";
        List<Tag> tagsToReturn = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, attractionID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
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
        System.out.println("Repository L203: Found following attractiontags: " + tagsToReturn); //TODO: DEBUG statement
        return tagsToReturn;
    }


    public String getCityFromDB(int attractionID) { //TODO: Tjek om valid city i DB?
        String sql = "SELECT b.city FROM attraction a JOIN location b ON a.postalcode=b.postalcode WHERE a.attractionID=?";
        String cityToReturn = null;

        try (Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, attractionID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                cityToReturn = rs.getString("city");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cityToReturn;
    }

    public List<String> getAllCitiesFromDB() {
        List<String> citiesToReturn = new ArrayList<>();
        String sql = "SELECT postalcode, city FROM location";

        try (Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String city = rs.getString("city");
                citiesToReturn.add(city);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return citiesToReturn;
    }

    public int getPostalCodeFromCityDB(TouristAttraction ta) {
        String sql = "SELECT postalcode FROM location WHERE city=?";
        int postalcodeToReturn = -1;

        try (Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, ta.getBy());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                postalcodeToReturn = rs.getInt("postalcode");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return postalcodeToReturn;
    }

    /*
    ############################################################
    #                   Full Attraction Lists(READ)            #
    ############################################################
     */
    public List<TouristAttraction> getTouristAttractionsFromDBConvertToObject() {
        touristRepository.clear();
        String sql = "SELECT attractionID, attractionName, attractionDesc FROM attraction";

        try (Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int attractionID = rs.getInt("attractionID");
                String attractionName = rs.getString("attractionName");
                String attractionDesc = rs.getString("attractionDesc");
                String city = getCityFromDB(attractionID); //Her bruger vi attractionID for at sammenligne attraction ID med postalcode. På den måde fanges String city.
                List<Tag> attractionTags = new ArrayList<>(getAttractionTagsFromDB(attractionID));
                TouristAttraction ta = new TouristAttraction(attractionName, attractionDesc, city, attractionTags);
                ta.setAttractionID(attractionID); //Konstruktør sætter automatisk et nyt TA objekts ID til -1, her retter vi det til, hvad det måtte være i SQL-databasen.

                touristRepository.add(ta);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return touristRepository;
    }

    /*
    ############################################################
    #             UPDATE TOURIST ATTRACTION(UPDATE)            #
    ############################################################
     */
    //TODO: HENTE BYLISTE FRA SQL I STEDET FOR HARDCODE
    //TODO: EFTER UPDATEATTRACTION() HIVE NY DATA MED OVER TIL SQL OG OVERSKRIVE DATAEN I TABELLERNE.
    public void updateTouristAttractionToDB(TouristAttraction ta) { //Forbindes med updateAttraction()
        String sql = "UPDATE attraction SET attractionDesc=?, postalcode=? WHERE attractionID=?";

        try (Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            /*
            //Vi kalder bare på attractions egne attributter, da denne metode bliver kaldt lige efter nye værdier
            bliver indsat via setter-metoder i updateAttraction(String name, String description,
            String by, List<Tag> tagListe) i Repository.
             */
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, ta.getDescription());
            ps.setInt(2, getPostalCodeFromCityDB(ta));
            ps.setInt(3, ta.getAttractionID());
            System.out.println("inserting on postalcode: " + getPostalCodeFromCityDB(ta)); //TODO: fjern debug statements whenever
            System.out.println("Finding city: " + ta.getBy());

            ps.executeUpdate();
            addTouristAttractionTagsToDB(ta);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateAttraction(String name, String description, String by, List<Tag> tagListe) {
        for (TouristAttraction t : touristRepository) {
            if (name.equalsIgnoreCase(t.getName())) {
                t.setDescription(description);
                t.setBy(by);
                t.setTagListe(tagListe);
                updateTouristAttractionToDB(t);
            }
        }
    }


    //nedenstående metode benyttes til tjek af om attraktion allerede er oprettet.
    //boolean resultat anvendes så i ovenstående add metode

    public boolean checkIfAttractionAlreadyExist(String name) {
        String sql = "SELECT attractionName FROM attraction WHERE LOWER (attractionName)=LOWER(?)";

        try (Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (name.equals(rs.getString("attractionName"))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public List<TouristAttraction> getFullTouristRepository() {
        return touristRepository;
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



    public String getDescription(String name) {
        String result = "";
        for (TouristAttraction t : touristRepository) {
            if (name.equalsIgnoreCase(t.getName())) {
                result = t.getDescription();
            }
        }
        return result;
    }

    /*
    ############################################################
    #             DELETE ATTRACTION(DELETE)                    #
    ############################################################
    */

    public void deleteAttractionFromDB(String name) {
        String sqlDeletion="DELETE FROM attraction WHERE attractionID=?";

        try(Connection con = DriverManager.getConnection(dbUrl, username, password)) {
            PreparedStatement ps = con.prepareStatement(sqlDeletion);
            ps.setInt(1,getAttractionIDFromAttractionName(name));
            ps.executeUpdate();
//            resetPrimaryKeyIncrement(); //Kan bruges til at nulstille, så den ikke hopper over 'gamle' primary keys. Best practice er dog at lade den hoppe og ikke genbruge gamle primary keys.

        }catch(SQLException e) {
            e.printStackTrace();
        }

    }
    public void deleteAttraction(TouristAttraction ta) {

        touristRepository.remove(ta);
    }
}
