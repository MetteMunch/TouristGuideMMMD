package com.example.touristguidemmmd;

import com.example.touristguidemmmd.repository.TouristRepository;
import com.example.touristguidemmmd.service.TouristService;

public class Main {
    public static void main(String[] args) {
        TouristService ts = new TouristService(new TouristRepository());

        System.out.println(ts.getListOfAttractions());
    }
}
