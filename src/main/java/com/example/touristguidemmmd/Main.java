package com.example.touristguidemmmd;

import com.example.touristguidemmmd.model.Tag;
import com.example.touristguidemmmd.model.TouristAttraction;
import com.example.touristguidemmmd.repository.TouristRepository;
import com.example.touristguidemmmd.service.TouristService;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        TouristService ts = new TouristService(new TouristRepository());
        System.out.println(ts.getFullListLocations());
    }
}
