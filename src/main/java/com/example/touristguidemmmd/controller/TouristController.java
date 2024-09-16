package com.example.touristguidemmmd.controller;

import com.example.touristguidemmmd.model.TouristAttraction;
import com.example.touristguidemmmd.service.TouristService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller //annotation som fortæller SpringBoot
@RequestMapping("attractions")//annotation som fortæller at alle request hhtp til /attractions skal køre nedenstående

public class TouristController {


    private TouristService ts;

    public TouristController(TouristService ts){
        this.ts = ts;
    }
}
