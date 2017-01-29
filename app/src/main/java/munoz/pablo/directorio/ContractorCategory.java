package munoz.pablo.directorio;

import java.util.ArrayList;

/**
 * Created by pablo on 1/28/2017.
 *
 * This class represents a contractor category. It hold information about a group of contractors
 * including the name of the category (which is basically the profession) e.g. Blacksmiths
 * or Accountants. It also has information about how many contacts are in each category.
 *
 */

public class ContractorCategory {
    private int id;
    private String name;
    private int numContacts;

    public static ArrayList<ContractorCategory> makeExample() {
        ArrayList<ContractorCategory> example = new ArrayList<>();
        example.add(new ContractorCategory(1, "Albañiles", 32));
        example.add(new ContractorCategory(2, "Contadores", 8));
        example.add(new ContractorCategory(3, "Electricistas", 15));
        example.add(new ContractorCategory(4, "Fontaneros", 11));
        example.add(new ContractorCategory(5, "Herreros", 3));
        return example;
    }

    public ContractorCategory(int id, String name, int numContacts) {
        this.id = id;
        this.name = name;
        this.numContacts = numContacts;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumContacts() {
        return this.numContacts;
    }

    public void setNumContacts(int numContacts) {
        this.numContacts = numContacts;
    }
}
