package com.example.touristguidemmmd.service;

import com.example.touristguidemmmd.model.Tag;
import com.example.touristguidemmmd.model.TouristAttraction;
import com.example.touristguidemmmd.repository.TouristRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // annotation som fortæller Spring, at denne klasse styrer / servicerer forretningslogik
public class TouristService {

    private final TouristRepository tr;

    public TouristService(TouristRepository tr) {
        this.tr = tr;
    }

    ////////////////////CRUD//////////////////////////////

    //*** TEST ***
    public List<Tag> getAttractionTagsFromDB(int attractionID) {
        return tr.getAttractionTagsFromDB(attractionID);
    }
    public String getCityFromDB(int attractionID) {
        return tr.getCityFromDB(attractionID);
    }
    public void addTouristAttractionToDB(TouristAttraction ta) {
        tr.addTouristAttractionToDB(ta);
    }
    public void addTouristAttractionTagsToDB(TouristAttraction ta) {
        tr.addTouristAttractionTagsToDB(ta);
    }
    public int getPostalCodeFromCityDB(TouristAttraction ta) {
        return tr.getPostalCodeFromCityDB(ta);
    }

    //------------------------------------
    public void addTouristAttraction(String name, String description, String by, List<Tag> tags) {
        tr.addTouristAttraction(name, description, by, tags);
    }
    public List<TouristAttraction> getListOfAttractions() {
        return tr.getFullTouristRepository();
    }

    public List<Tag> getListOfTags(String name){
        return tr.getListOfTags(name);
    }
    public TouristAttraction getSpecificTouristAttraction(String name) {
        return tr.getByNameTouristRepository(name);
    }

    public void updateAttraction(String name, String description, String by,List<Tag> tagListe){
        tr.updateAttraction(name, description, by, tagListe);
    }

    public String getDescription(String name){
        return tr.getDescription(name);
    }


    public void deleteAttraction(TouristAttraction ta) {
        tr.deleteAttraction(ta);
    }

}