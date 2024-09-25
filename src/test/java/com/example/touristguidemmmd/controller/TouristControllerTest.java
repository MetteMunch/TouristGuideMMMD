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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
        mockMvc.perform(get("/attractions"))
                .andExpect(status().isOk())
                .andExpect(view().name("attractionList"));
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
    void showSpecificAttraction() throws Exception {
        mockMvc.perform(get("/attractions/SMK"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("attractionName", touristAttraction.getName()))
                .andExpect(model().attribute("description", touristAttraction.getDescription()))
                .andExpect(model().attribute("tagsOnAttraction", touristAttraction.getTagListe()))
                .andExpect(model().attribute("city", touristAttraction.getBy()))
                .andExpect(view().name("showAttraction"));
    }

    @Test
    void updateAttraction() throws Exception {

        String tags = "MUSEUM,DESIGN"; /*
        Dette kan ikke passes som param til mockmetoden med gettermetoden, da gettermetoden returnerer en List<Tag>,
        men kræver et String element.
        */


        mockMvc.perform(post("/attractions/update")
                        .param("name", touristAttraction.getName())
                        .param("description", touristAttraction.getDescription())
                        .param("by", touristAttraction.getBy())
                        .param("tagListe", tags) // Simulate the list of tags
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection()) // Expecting a redirection
                .andExpect(redirectedUrl("/attractions")); // Verify the redirect URL

        // Assert: verify that the service's updateAttraction method was called with correct arguments

//        fail(); //TODO: NOT IMPLEMENTED
    }

    @Test
    void addAttraction() throws Exception {
        mockMvc.perform(get("/attractions/add")).andExpect(status().isOk())
                .andExpect(model().attribute("allTags", List.of(Tag.values())))
                .andExpect(model().attribute("byListe", List.of("København", "Frederiksberg", "Aarhus", "Odense", "Aalborg")))
                .andExpect(view().name("addAttraction"));
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