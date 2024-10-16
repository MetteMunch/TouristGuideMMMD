package com.example.touristguidemmmd.controller;

import com.example.touristguidemmmd.model.Tag;
import com.example.touristguidemmmd.model.TouristAttraction;
import com.example.touristguidemmmd.service.TouristService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Controller //annotation som fortæller Spring at denne klasse håndterer HTTP-forespørgsler
@RequestMapping("/attractions")//annotation Endpoint som fortæller hvilken url / sti at alle forespørgslerne til denne controller skal have for at køre metoderne

public class TouristController {

    private final TouristService ts;

    public TouristController (TouristService ts){
        this.ts = ts;
    }

    @GetMapping //annotation der bruges til at specificere hvilken url der skal kaldes med HTTP GET-request for at køre en given metode
    public String getFullListOfAttractions(Model model){
//        List<TouristAttraction> fullListOfAttractions = ts.getListOfAttractions();
        List<TouristAttraction> fullListOfAttractions = ts.getTouristAttractionsFromDBConvertToObject(); //TODO Indsat for at teste. Dette virker.
        model.addAttribute("listOfAttractions",fullListOfAttractions);
        return "attractionList";
    }

    //*** TEST ***
    @GetMapping("/testCRUD/{attractionID}")
    public String testCrudSQL(@PathVariable int attractionID, Model model) {
        List<Tag> listOfTags = ts.getAttractionTagsFromDB(attractionID);
        String attractionCity = ts.getCityFromDB(attractionID);

        model.addAttribute("city",attractionCity );
        model.addAttribute("listOfTags", listOfTags);
        for (Tag tag : listOfTags) {
            System.out.println(tag);
        }
        System.out.println(attractionCity);

        return "testCRUDSQL";
    }
    //---------------------------------


    @GetMapping("/{name}/tags") //denne metode kaldes ved URL attractions/Tivoli/tags
    public String showTagsOnSpecificAttraction(@PathVariable String name, Model model){ //Hvad er forskellen på PathVariable og RequestVariable?
        List<Tag> tagListe = ts.getListOfTags(name);
        model.addAttribute("tagListe",tagListe);
        model.addAttribute("attractionName",name);
        return "tags"; //HTML filen der skal læses

    }
    @GetMapping("/{name}/edit")
    public String goToEditAttraction(@PathVariable String name, Model model){
        TouristAttraction attraction = ts.getSpecificTouristAttraction(name);
        model.addAttribute("description",attraction.getDescription());
        model.addAttribute("by",attraction.getBy());
        model.addAttribute("byListe",List.of("København","Frederiksberg","Aarhus","Odense","Aalborg"));
        model.addAttribute("tagListe",attraction.getTagListe());
        model.addAttribute("allPossibleTags",Tag.values());
        return "updateAttraction";
    }

    @GetMapping("/{name}") //annotation som fortæller at ved kald af /attarctions/name, så køres denne metode der returnerer hvilken HTML der skal vises
    public String showSpecificAttraction(@PathVariable String name, Model model){
        TouristAttraction ta = ts.getSpecificTouristAttraction(name);
        model.addAttribute("attractionName",name);
        model.addAttribute("description",ta.getDescription());
        model.addAttribute("tagsOnAttraction",ta.getTagListe());
        model.addAttribute("city",ta.getBy());
        return "showAttraction";
    }

    @PostMapping("/update") //annotation der bruges til at specificere hvilken url der skal kaldes ved håndtering af en HTTP-POST-request (fx sende data via formular til server eller andet)
    public String updateAttraction(@RequestParam String name, @RequestParam String description, @RequestParam String by, @RequestParam List<Tag> tagListe){
        ts.updateAttraction(name, description, by, tagListe);
        return "redirect:/attractions"; //redirect fortæller gå tilbage til browseren og vis denne side (så kaldet kommer fra browseren igen)
    }

    @GetMapping("/add")
    public String addAttraction(Model model) {
        List<Tag> tagsToPresent = new ArrayList<>();
        Collections.addAll(tagsToPresent, Tag.values());
        model.addAttribute("allTags", tagsToPresent);
        model.addAttribute("byListe",List.of("København","Frederiksberg","Aarhus","Odense","Aalborg"));
        return "addAttraction";
    }

    @PostMapping("/save") //annotation der bruges til at specificere hvilken url der skal kaldes ved håndtering af en HTTP-POST-request (fx sende data via formular til server eller andet)
    public String saveAttraction(@RequestParam String name, @RequestParam String description, @RequestParam String by, @RequestParam List<Tag> tagListe, Model model) {
        try {
            ts.addTouristAttraction(name, description, by, tagListe);
            return "redirect:/attractions";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage",e.getMessage());
            //her fanges og vises fejlmeddelelse fra Repository metoden
            return "addAttraction";
        }

    }

    @PostMapping("/delete/{name}")
    public String deleteAttraction(@PathVariable String name) {
        TouristAttraction ta = ts.getSpecificTouristAttraction(name);
        ts.deleteAttraction(ta);
        return "redirect:/attractions";
    }

}
