//package com.example.touristguidemmmd.repository;
//
//import com.example.touristguidemmmd.model.Tag;
//import com.example.touristguidemmmd.model.TouristAttraction;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class TouristRepositoryTest {
//
//
//    TouristRepository tr;
//
//    @BeforeEach
//    void setUp() {
//        tr = new TouristRepository();
//
//    }
//
//    @AfterEach
//    void tearDown() {
//    }
//
//    @Test
//    void getByNameTouristRepository() {
//        TouristAttraction expected = new TouristAttraction("Tivoli", "Forlystelsespark i centrum af KBH",
//                "København", List.of(Tag.FORLYSTELSE, Tag.PARK, Tag.RESTAURANT));
//        TouristAttraction actual = tr.getByNameTouristRepository("Tivoli");
//        assertEquals(expected, actual);
//
//    }
//
//    @Test
//    void getDescription() {
//        String expected = "Åben park midt på Frederiksberg";
//        String actual = tr.getDescription("Frederiksberg Have");
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void addTouristAttractionWithSuccess() {
//        tr.addTouristAttraction("NyAttraktion", "Beskrivelse", "By", List.of(Tag.MUSEUM));
//        //Tjek af om attraktionen findes ved hjælp af vores metode:
//        assertTrue(tr.checkIfAttractionAlreadyExist("NyAttraktion"));
//    }
//
//    @Test
//    void tryAddAlreadyExistingAttraction() {
//        tr.addTouristAttraction("at1","desp","by",List.of(Tag.PARK));
//        //her tjekker vi om den smider exception som forventet, når vi præver at oprette samme attraktion igen
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//                tr.addTouristAttraction("at1","desp","by",List.of(Tag.PARK));
//        });
//
//    }
//
//}