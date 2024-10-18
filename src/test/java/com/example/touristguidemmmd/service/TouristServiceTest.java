package com.example.touristguidemmmd.service;

import static org.mockito.Mockito.*;

import com.example.touristguidemmmd.model.Tag;
import com.example.touristguidemmmd.model.TouristAttraction;
import com.example.touristguidemmmd.repository.TouristRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;


@SpringBootTest
class TouristServiceTest {

    @MockBean
    private TouristRepository touristRepository;
    @Autowired
    private TouristService touristService;


    @BeforeEach
    public void setUp() throws SQLException {
    }
/*
---------------***CREATE***---------------
 */
    @Test
    void addTouristAttractionToDB() throws SQLException {
        // Create a new TouristAttraction object
        TouristAttraction attraction = new TouristAttraction();
        attraction.setName("Eiffel Tower");
        attraction.setDescription("A wrought-iron lattice tower in Paris.");
        attraction.setBy("Paris");
        attraction.setTagListe(List.of(Tag.DESIGN, Tag.MONUMENTER));

        // Simulate adding an attraction to the database
        touristService.addTouristAttractionToDB(attraction);

        // Verify that the correct method is called on the mocked repository
        verify(touristRepository).addTouristAttractionAndTagsToDB(attraction); // Adjust this line based on the actual method
    }
    @Test
    void testAddTouristAttractionTagsToDB() throws SQLException {
        // Create a new TouristAttraction object
        TouristAttraction attraction = new TouristAttraction();
        attraction.setName("Eiffel Tower");
        List<Tag> tags = Arrays.asList(Tag.PARK, Tag.MONUMENTER);
        attraction.setTagListe(tags);
        attraction.setAttractionID(1);

        touristService.addTouristAttractionTagsToDB(attraction);

        verify(touristRepository).addTouristAttractionTagsToDB(attraction);
    }
    /*
    ---------------***READ***---------------
     */
    @Test
    void getTouristAttractionsFromDBConvertToObject() throws SQLException {
        //Arrange
        TouristAttraction attraction = new TouristAttraction();
        attraction.setName("Eiffel Tower");
        attraction.setDescription("First Description");
        attraction.setBy("Paris");
        attraction.setTagListe(List.of(Tag.DESIGN, Tag.MONUMENTER));
        attraction.setAttractionID(1);
        when(touristRepository.getTouristAttractionsFromDBConvertToObject()).thenReturn(List.of(attraction));

        //Act
//        touristService.addTouristAttractionToDB(attraction);
        List<TouristAttraction> list = touristService.getTouristAttractionsFromDBConvertToObject();
//        TouristAttraction fetchAttraction = list.get(0);
//        fetchAttraction.setDescription("Updated Description");

        //Assert
//        Assertions.assertNotNull(fetchAttraction);
//        Assertions.assertEquals(attraction.getName(), fetchAttraction.getName());
//        Assertions.assertEquals(fetchAttraction.getDescription(), "Updated Description");
        Assertions.assertEquals(1, list.size());
        verify(touristRepository).getTouristAttractionsFromDBConvertToObject();

    }
    /*
---------------***UPDATE***---------------
 */


}