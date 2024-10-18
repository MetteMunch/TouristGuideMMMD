package com.example.touristguidemmmd;

import com.example.touristguidemmmd.model.Tag;
import com.example.touristguidemmmd.model.TouristAttraction;
import com.example.touristguidemmmd.repository.TouristRepository;
import com.example.touristguidemmmd.service.TouristService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TouristRepository tr = new TouristRepository();

//        for(TouristAttraction ta: tr.getFullTouristRepository()){
//            System.out.println(ta + " " +ta.getTagListe());
//        }
//
//        System.out.println("");
//
//        List<Tag> list = tr.getListOfTags("Aros");
//        for(Tag tag: list){
//            System.out.println(tag);
//        }
//
//        System.out.println("");

        TouristAttraction ta = tr.getByNameTouristRepository("Aros");
        System.out.println(ta);
        System.out.println("AttID: " +ta.getAttractionID());
        System.out.println("By: " +ta.getBy());
        System.out.println(ta.getTagListe());


    }
}
