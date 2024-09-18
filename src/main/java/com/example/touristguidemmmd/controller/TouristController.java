package com.example.touristguidemmmd.controller;

import com.example.touristguidemmmd.model.TouristAttraction;
import com.example.touristguidemmmd.service.TouristService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller //annotation som fortæller Spring at denne klasse håndterer HTTP-forespørgsler
@RequestMapping("attractions")//annotation som fortæller hvilken url / sti at alle forespørgslerne til denne controller skal have for at køre metoderne

public class TouristController {

    private TouristService ts;

    public TouristController(TouristService ts){
        this.ts = ts;
    }

    @GetMapping //annotation der bruges til at specificere hvilken url der skal kaldes med HTTP GET-request for at køre en given metode
    public String getFullListOfAttractions(Model model){
        List<TouristAttraction> fullListOfAttractions = ts.getListOfAttractions();
        model.addAttribute("listOfAttractions",fullListOfAttractions);
        return "attractionList";
    }

    //@PostMapping //annotation der bruges til at specificere hvilken url der skal kaldes ved håndtering af en HTTP-POST-request (fx sende data via formular til server eller andet)
}
