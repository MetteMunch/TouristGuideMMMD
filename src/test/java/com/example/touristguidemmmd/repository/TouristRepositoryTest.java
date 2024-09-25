package com.example.touristguidemmmd.repository;

import com.example.touristguidemmmd.model.Tag;
import com.example.touristguidemmmd.model.TouristAttraction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TouristRepositoryTest {

    TouristAttraction touristAttraction1;
    TouristAttraction touristAttraction2;
    TouristRepository tr;

    @BeforeEach
    void setUp() {
        touristAttraction1 = new TouristAttraction("touristAttraction1", "des1", "by1", List.of(Tag.FORLYSTELSE));
        touristAttraction2 = new TouristAttraction("touristAttraction2", "des2", "by2", List.of(Tag.MUSEUM));
        tr = new TouristRepository();

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getByNameTouristRepository() {
        TouristAttraction expected = new TouristAttraction("Tivoli", "Forlystelsespark i centrum af KBH",
                "København", List.of(Tag.FORLYSTELSE, Tag.PARK, Tag.RESTAURANT));
        TouristAttraction actual = tr.getByNameTouristRepository("Tivoli");
        assertEquals(expected,actual);

    }


    @Test
    void getDescription() {
        String expected = "Åben park midt på Frederiksberg";
        String actual = tr.getDescription("Frederiksberg Have");
        assertEquals(expected,actual);
    }
}