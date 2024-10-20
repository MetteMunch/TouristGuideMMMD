package com.example.touristguidemmmd;

import com.example.touristguidemmmd.model.Tag;
import com.example.touristguidemmmd.model.TouristAttraction;
import com.example.touristguidemmmd.repository.TouristRepository;
import com.example.touristguidemmmd.service.TouristService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
       TouristService ts = new TouristService(new TouristRepository());

//        for(TouristAttraction ta: ts.getListOfAttractions()){
//            System.out.println(ta + " " +ta.getTagListe());
//        }
//
//        System.out.println("");
//
//        List<Tag> list = ts.getListOfTags("Tivoli");
//        for(Tag tag: list){
//            System.out.println(tag);
//        }
//
//        System.out.println("");
//
//        TouristAttraction ta = ts.getSpecificTouristAttraction("Aros");
//        System.out.println(ta);
//        System.out.println("AttID: " +ta.getAttractionID());
//        System.out.println("By: " +ta.getBy());
//        System.out.println(ta.getTagListe());

        //ts.addTouristAttraction("Domkirken","Ribe Domkirke med gamle kalkmalerier og nyere glasmosaik","Ribe",List.of(Tag.MONUMENTER,Tag.MUSEUM));
//        for(TouristAttraction ta: ts.getListOfAttractions()){
//            System.out.println(ta + " " +ta.getTagListe());
//        }
//
//        System.out.println("");
//
//        ts.updateAttraction("Domkirken","Domkirke i Danmarks ældste by med borgertårn og storkerede","Ribe",List.of(Tag.MUSEUM, Tag.MONUMENTER));
//
//        for(TouristAttraction ta: ts.getListOfAttractions()){
//            System.out.println(ta + " " +ta.getTagListe());
//        }

        System.out.println(ts.getDescription("domkirke"));


    }
}
