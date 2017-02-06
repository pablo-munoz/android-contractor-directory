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
    private String img;

    public static ArrayList<ContractorCategory> makeExample() {
        ArrayList<ContractorCategory> example = new ArrayList<>();
        example.add(new ContractorCategory(1, "Albañiles", "https://www.healthable.org/wp-content/uploads/2016/10/Construction-Workers.jpg", 32));
        example.add(new ContractorCategory(2, "Contadores", "https://assets.entrepreneur.com/content/16x9/822/how-to-hire-an-accountant.jpg", 8));
        example.add(new ContractorCategory(3, "Electricistas", "http://pavimarconstrucciones.com/wp-content/uploads/electricistas-en-alicante-02.png", 15));
        example.add(new ContractorCategory(4, "Fontaneros", "http://www.uabtube.cat/wp-content/uploads/2014/06/fontanerosvalenciaonline.jpg", 11));
        example.add(new ContractorCategory(5, "Herreros", "http://gracielabenito.com/wp-content/uploads/2016/03/01-eventos-plaza-irlanda-herreros-graciela-benito-fotografia.jpg", 3));
        return example;
    }

    public ContractorCategory(int id, String name, String img, int numContacts) {
        this.id = id;
        this.name = name;
        this.img = img;
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

    public String getImg() {
        return this.img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getNumContacts() {
        return this.numContacts;
    }

    public void setNumContacts(int numContacts) {
        this.numContacts = numContacts;
    }
}
