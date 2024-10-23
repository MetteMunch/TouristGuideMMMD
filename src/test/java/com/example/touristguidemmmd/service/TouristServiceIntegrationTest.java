package com.example.touristguidemmmd.service;

import com.example.touristguidemmmd.model.TouristAttraction;
import com.example.touristguidemmmd.repository.TouristRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") //Vi fortæller hvilken .properties profil, der skal bruges. I dette tilfælde test.properties.
@SpringBootTest
public class TouristServiceIntegrationTest {

    @Autowired
    private TouristService touristService;
    @Autowired
    private TouristRepository touristRepository;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;


@BeforeEach
void setUp() throws SQLException {
    try (Connection con = DriverManager.getConnection(url, username, password);
         Statement stmt = con.createStatement()) {
        //H2 in-memory test database. Denne bliver brugt til alle integrationstests.
        stmt.execute("DROP TABLE IF EXISTS attraction");
        stmt.execute("CREATE TABLE attraction (attractionID INT AUTO_INCREMENT PRIMARY KEY, attractionName VARCHAR(255), attractionDesc VARCHAR(255), postalcode VARCHAR(10));");

        stmt.execute("INSERT INTO attraction (attractionName, attractionDesc, postalcode) VALUES ('Eiffel Tower', 'French Tower', '75007')");
        stmt.execute("INSERT INTO attraction (attractionName, attractionDesc, postalcode) VALUES ('Louvre Museum', 'Famous art museum', '75001')");
    }
}
    /*
    -------***CREATE***-------
     */
    @Test
    void addTouristAttractionToDB() {
        TouristAttraction ta = new TouristAttraction();
        ta.setName("Test Name");
        ta.setDescription("Test Description");

        touristRepository.addTouristAttractionAndTagsToDB(ta);

        String sql = "SELECT * FROM attraction WHERE attractionName=?";
        try(Connection con = DriverManager.getConnection(url, username, password )) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "Test Name");
            try(ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals("Test Name", rs.getString("attractionName"));
                assertEquals("Test Description", rs.getString("attractionDesc"));
            }

        }catch(SQLException e) {
            e.printStackTrace();
        }

    }

    /*
    -------***READ***-------
     */
    @Test
    void getTouristAttractionsFromDBConvertToObject() {
        //Act
        List<TouristAttraction> list = touristService.getTouristAttractionsFromDBConvertToObject();

        //Assert
        Assertions.assertThat(list).isNotNull();
        Assertions.assertThat(list).hasSize(2);

        TouristAttraction firstAttraction = list.get(0);
        Assertions.assertThat(firstAttraction.getName()).isEqualTo("Eiffel Tower");
        Assertions.assertThat(firstAttraction.getDescription()).isEqualTo("French Tower");

        TouristAttraction secondAttraction = list.get(1);
        Assertions.assertThat(secondAttraction.getName()).isEqualTo("Louvre Museum");
        Assertions.assertThat(secondAttraction.getDescription()).isEqualTo("Famous art museum");
    }

    /*
    -------***UPDATE***-------
     */
    @Test
    void updateAttraction() {
        String sqlSelect ="SELECT attractionID, attractionName, attractionDesc FROM attraction WHERE attractionID=?";
        TouristAttraction ta = new TouristAttraction();

        try(Connection con = DriverManager.getConnection(url, username, password)) {
            PreparedStatement ps = con.prepareStatement(sqlSelect);
            ps.setInt(1,1);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                /*
                Vi laver et nyt attraction objekt fra vores test-database. Bruger kun name og desc for testing formål.
                 */
                String name = rs.getString("attractionName");
                String attractionDesc = rs.getString("attractionDesc");
                int attractionID = rs.getInt("attractionID");
                ta.setName(name);
                ta.setDescription(attractionDesc);
                ta.setAttractionID(attractionID);
            }

        }catch(SQLException e) {
            e.printStackTrace();
        }

        String updatedDescription ="New Description of the Eiffel Tower";
        ta.setDescription(updatedDescription);
        touristRepository.updateTouristAttractionToDB(ta);

        try(Connection con = DriverManager.getConnection(url, username, password)) {
            PreparedStatement ps = con.prepareStatement(sqlSelect);
            ps.setInt(1, ta.getAttractionID());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                assertEquals(updatedDescription, rs.getString("attractionDesc")); //Vi tjekker om opdateringen af description er gået igennem til test-databasen.
                assertEquals(ta.getAttractionID(), rs.getInt("attractionID"));
            }

        }catch(SQLException e) {
            e.printStackTrace();
        }



    }

    /*
    -------***DELETE***-------
     */
    @Test
    void deleteAttraction() { //TODO: Lav om, der bliver ikke tjekket på den egentlige metode.
            String attractionName = "";
            int attractionID = -1; // Vi bruger en settermetode til korrekt at sette ID fra test-databasen. I programmet defaulter den til -1 for debugging formål.
            int expectedSQLSizeBeforeDelete = 2;
            int expectedSQLSizeAfterDelete = 1;

            try (Connection con = DriverManager.getConnection(url, username, password)) {
                //Tjekker om attraction eksisterer
                String sql = "SELECT attractionID, attractionName, attractionDesc FROM attraction WHERE attractionID=? AND attractionName=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, 1);
                ps.setString(2, "Eiffel Tower");
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    attractionName = rs.getString("attractionName");
                    attractionID = rs.getInt("attractionID");
                    assertEquals("Eiffel Tower", attractionName);  // Tjekker om navnene stemmer overens
                    assertEquals(1, attractionID);  // Tjekker om id stemmer overens
                }
                System.out.println("Attraction ID for "+attractionName+": "+attractionID);

                //Vi tæller antallet af records før vi laver vores delete-operation.
                String sqlCountRecords = "SELECT COUNT(*) AS count FROM attraction";
                PreparedStatement psCountBeforeDelete = con.prepareStatement(sqlCountRecords);
                ResultSet rsCountBeforeDelete = psCountBeforeDelete.executeQuery();
                if (rsCountBeforeDelete.next()) {
                    assertEquals(expectedSQLSizeBeforeDelete, rsCountBeforeDelete.getInt("count"));
                }

                //Vi sletter Eiffel tårnet fra test-databasen med brug af metoden.
                touristRepository.deleteAttractionFromDB(attractionName);


                //Vi tæller og asserter om vores test-database er blevet 1 mindre.
                PreparedStatement psCountAfterDelete = con.prepareStatement(sqlCountRecords);
                ResultSet rsCountAfterDelete = psCountAfterDelete.executeQuery();
                if (rsCountAfterDelete.next()) {
                    assertEquals(expectedSQLSizeAfterDelete, rsCountAfterDelete.getInt("count"));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
}
