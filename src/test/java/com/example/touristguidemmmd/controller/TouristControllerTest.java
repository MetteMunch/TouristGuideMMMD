package com.example.touristguidemmmd.controller;

import com.example.touristguidemmmd.model.Tag;
import com.example.touristguidemmmd.model.TouristAttraction;
import com.example.touristguidemmmd.service.TouristService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TouristController.class)
class TouristControllerTest {

    private TouristAttraction touristAttraction;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TouristService touristService;


    @BeforeEach
    void setUp() {
        touristAttraction = new TouristAttraction();
        touristAttraction.setName("SMK");
        touristAttraction.setDescription("A famous museum");
        touristAttraction.setBy("København");
        touristAttraction.setTagListe(List.of(Tag.MUSEUM, Tag.DESIGN));

        when(touristService.getSpecificTouristAttraction("SMK")).thenReturn(touristAttraction);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getFullListOfAttractions() throws Exception {
        mockMvc.perform(get("/attractions")).andExpect(status().isOk()).andExpect(view().name("attractionList"));
    }

    @Test
    void showTagsOnSpecificAttraction() throws Exception {
        mockMvc.perform(get("/attractions/SMK/tags"))
                .andExpect(status().isOk())
                .andExpect(view().name("tags"))
                .andExpect(content().string(containsString("Tags")));
    }

    @Test
    void goToEditAttraction() throws Exception {
        mockMvc.perform(get("/attractions/SMK/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("updateAttraction"))
                .andExpect(model().attribute("description", "A famous museum"))
                .andExpect(model().attribute("by", "København"))
                .andExpect(model().attribute("byListe", List.of("København", "Frederiksberg", "Aarhus", "Odense", "Aalborg")))
                .andExpect(model().attribute("tagListe", List.of(Tag.MUSEUM, Tag.DESIGN)))
                .andExpect(model().attribute("allPossibleTags", Tag.values()));
    }

    @Test
    void showSpecificAttraction() {
        fail(); //TODO: NOT IMPLEMENTED
    }

    @Test
    void updateAttraction() {
        fail(); //TODO: NOT IMPLEMENTED
    }

    @Test
    void addAttraction() throws Exception {
        mockMvc.perform(get("/attractions/add")).andExpect(status().isOk()).
                andExpect(model().attribute("allTags", List.of(Tag.values()))).andExpect(model().
                        attribute("byListe", List.of("København","Frederiksberg","Aarhus","Odense","Aalborg"))).
                andExpect(view().name("addAttraction"));
    }

    @Test
    void saveAttraction() {
        fail(); //TODO: NOT IMPLEMENTED
    }

    @Test
    void deleteAttraction() {
        fail(); //TODO: NOT IMPLEMENTED
    }
}