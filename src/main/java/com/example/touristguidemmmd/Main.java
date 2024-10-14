package com.example.touristguidemmmd;

import com.example.touristguidemmmd.model.TouristAttraction;
import com.example.touristguidemmmd.repository.TouristRepository;
import com.example.touristguidemmmd.service.TouristService;

public class Main {
    public static void main(String[] args) {
        TouristRepository tr = new TouristRepository();

        for(TouristAttraction ta: tr.getFullTouristRepository()){
            System.out.println(ta + " " +ta.getTagListe());
        }
    }
}
