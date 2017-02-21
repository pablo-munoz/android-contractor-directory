package models;

/**
 * Created by pablo on 1/28/2017.
 *
 * This class represents a contractor category. It hold information about a group of contractors
 * including the name of the category (which is basically the profession) e.g. Blacksmiths
 * or Accountants. It also has information about how many contacts are in each category.
 *
 */

public class ContractorCategory {
    private String id;
    private String name;
    private String shortName;
    private String img;

    public ContractorCategory(String id, String name, String shortName, String img) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.img = img;
    }

    public String getId() {
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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
