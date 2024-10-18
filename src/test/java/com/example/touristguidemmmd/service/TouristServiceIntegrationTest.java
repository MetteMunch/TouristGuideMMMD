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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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


//    @Sql(
//            statements = {
//                    "CREATE TABLE attraction (attractionID INT AUTO_INCREMENT PRIMARY KEY, attractionName VARCHAR(255), attractionDesc VARCHAR(255), postalcode VARCHAR(10));",
//                    "INSERT INTO attraction (attractionName, attractionDesc, postalcode) VALUES ('Eiffel Tower', 'French Tower', '75007')",
//                    "INSERT INTO attraction (attractionName, attractionDesc, postalcode) VALUES ('Louvre Museum', 'Famous art museum', '75001')"
//            })
@BeforeEach
void setUp() throws SQLException {
    try (Connection con = DriverManager.getConnection(url, username, password);
         Statement stmt = con.createStatement()) {
        // Clear the attraction table before each test
        stmt.execute("DROP TABLE IF EXISTS attraction");
        stmt.execute("CREATE TABLE attraction (attractionID INT AUTO_INCREMENT PRIMARY KEY, attractionName VARCHAR(255), attractionDesc VARCHAR(255), postalcode VARCHAR(10));");
        // Prepopulate with initial data for testing
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


}
