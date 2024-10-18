package com.example.touristguidemmmd.service;

import com.example.touristguidemmmd.model.TouristAttraction;
import com.example.touristguidemmmd.repository.TouristRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
public class TouristServiceIntegrationTest {

    @Autowired
    private TouristService touristService;
    @Autowired
    private TouristRepository touristRepository;

    @Sql(
            statements = {
                    "CREATE TABLE attraction (attractionID INT AUTO_INCREMENT PRIMARY KEY, attractionName VARCHAR(255), attractionDesc VARCHAR(255), postalcode VARCHAR(10));",
                    "INSERT INTO attraction (attractionName, attractionDesc, postalcode) VALUES ('Eiffel Tower', 'French Tower', '75007')",
                    "INSERT INTO attraction (attractionName, attractionDesc, postalcode) VALUES ('Louvre Museum', 'Famous art museum', '75001')"
            })
    /*
    -------***CREATE***-------
     */
    @Test
    void addTouristAttractionToDB() {

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
