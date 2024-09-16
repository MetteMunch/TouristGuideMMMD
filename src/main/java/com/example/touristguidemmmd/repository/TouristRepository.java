package com.example.touristguidemmmd.repository;

import com.example.touristguidemmmd.model.TouristAttraction;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TouristRepository {

    private List<TouristAttraction> touristRepository = new ArrayList<>();

    public TouristRepository() {
        addHardcodetDataTilListe();
    }

    /////////////////////CRUD/////////////////////
    public void addHardcodetDataTilListe() {
        touristRepository.add(new TouristAttraction("Tivoli", "Forlystelsespark i centrum af KBH"));
        touristRepository.add(new TouristAttraction("Frederiksberg Have", "Åben park midt på Frederiksberg"));
        touristRepository.add(new TouristAttraction("Københavns Museum", "Museum i KBH der dækker over københavns historie"));;
    }

    public void addTouristAttraction(String name, String description) {
        touristRepository.add(new TouristAttraction(name, description));
    }

    public List<TouristAttraction> getFullTouristRepository() {
        return touristRepository;
    }

    public TouristAttraction getByNameTouristRepository(String name) {
        for (TouristAttraction t : touristRepository) {
            if (t.getName().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }

    public void updateAttraction(String name,String description) {
        for (TouristAttraction t : touristRepository) {
            if (name.equalsIgnoreCase(t.getName())) {
                t.setDescription(description);
            }
        }
    }

    public String getDescription(String name) {
        String result = "";
        for (TouristAttraction t : touristRepository) {
            if (name.equalsIgnoreCase(t.getName())) {
                result = t.getDescription();
            }
        }
        return result;
    }



    public void deleteAttraction(TouristAttraction ta) {
        touristRepository.remove(ta);
    }
}
