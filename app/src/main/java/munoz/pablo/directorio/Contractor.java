package munoz.pablo.directorio;

import java.util.ArrayList;

/**
 * Created by pablo on 1/28/2017.
 */

public class Contractor {
    private int id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;
    private String website;
    private int rating;

    public static ArrayList<Contractor> makeExample() {
        ArrayList<Contractor> contractors = new ArrayList<>();
        contractors.add(new Contractor(1, "Darío", null, "Rubén", "rubendario@gmail.com", "33312312312", null, 0));
        contractors.add(new Contractor(2, "Cosme", null, "Fulanito", "cosmefulanito@gmail.com", "33398798798", null, 2));
        contractors.add(new Contractor(3, "Alfonso", null, "Muníz", "alfonsomuniz@gmail.com", "33356567562", null, 2));
        return contractors;
    }

    public Contractor(int id, String firstName, String middleName, String lastName, String email,
                      String phone, String website, int rating) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.website = website;
        this.rating = rating;
    }

    public int getId() {
        return this.id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return this.website;
    }

    public void setWebsite(String website) {
        this.website = firstName;
    }

    public int getRating() {
        return this.rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getFullName() {
        String fullName = "";
        if (this.firstName != null) fullName += this.firstName;
        if (this.middleName != null) fullName += " " + this.middleName;
        if (this.lastName != null) fullName += " " + this.lastName;
        return fullName;
    }
}
