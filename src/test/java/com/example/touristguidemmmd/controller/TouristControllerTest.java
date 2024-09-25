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

    String tagsInString = "MUSEUM, DESIGN"; /*
    SKAL MATCHE TOURISTATTRACTIONS TAGLISTE
    */

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
                .andExpect(model().attribute("description", touristAttraction.getDescription()))
                .andExpect(model().attribute("by", touristAttraction.getBy()))
                .andExpect(model().attribute("byListe", List.of("København", "Frederiksberg", "Aarhus", "Odense", "Aalborg")))
                .andExpect(model().attribute("tagListe", touristAttraction.getTagListe()))
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
        mockMvc.perform(post("/attractions/update")
                        .param("name", touristAttraction.getName())
                        .param("description", touristAttraction.getDescription())
                        .param("by", touristAttraction.getBy())
                        .param("tagListe", tagsInString)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)) //Specifies that the request content type is application/x-www-form-urlencoded, which is the standard content type when submitting forms via HTTP POST
                .andExpect(status().is3xxRedirection()) // Vi forventer en 300-http statuskode(redirect)
                .andExpect(redirectedUrl("/attractions"));
    }

    @Test
    void addAttraction() throws Exception {
        mockMvc.perform(get("/attractions/add")).andExpect(status().isOk())
                .andExpect(model().attribute("allTags", List.of(Tag.values())))
                .andExpect(model().attribute("byListe", List.of("København", "Frederiksberg", "Aarhus", "Odense", "Aalborg")))
                .andExpect(view().name("addAttraction"));
    }

    @Test
    void saveAttraction() throws Exception {
        mockMvc.perform(post("/attractions/save")
                        .param("name", touristAttraction.getName())  // Use touristAttraction's name
                        .param("description", touristAttraction.getDescription())  // Use description from object
                        .param("by", touristAttraction.getBy())  // Use city from object
                        .param("tagListe", tagsInString))
                .andDo(print())  // Optional: print the request and response for debugging
                .andExpect(status().is3xxRedirection())  // Expecting a redirection
                .andExpect(redirectedUrl("/attractions"));
    }

    @Test
    void deleteAttraction() throws Exception {
        mockMvc.perform(post("/attractions/delete/{name}", touristAttraction.getName())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())  // Optional: print the request and response for debugging
                .andExpect(status().is3xxRedirection())  // Expecting a redirection
                .andExpect(redirectedUrl("/attractions"));
//        fail(); //TODO: NOT IMPLEMENTED
    }
}