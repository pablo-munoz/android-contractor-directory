package models;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by pablo on 1/28/2017.
 */

public class Contractor{
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;
    private String website;
    private String portrait;
    private double rating;
    private JSONArray comments;


    public Contractor(String id, String firstName, String middleName, String lastName, String email,
                      String phone, String website, String portrait, double rating, JSONArray comments) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.website = website;
        this.portrait = portrait;
        this.rating = rating;
        this.comments = comments;
    }

    public String getId() {
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

    public String getPortrait() {
        return this.portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public double getRating() {
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

    public JSONArray getComments() {
        return this.comments;
    }
}
