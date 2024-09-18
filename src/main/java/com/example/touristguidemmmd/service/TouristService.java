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

    public void updateAttraction(String name, String description){
        tr.updateAttraction(name, description);
    }

    public String getDescription(String name){
        return tr.getDescription(name);
    }


    public void deleteAttraction(TouristAttraction ta) {
        tr.deleteAttraction(ta);
    }

}