package munoz.pablo.directorio.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;

import munoz.pablo.directorio.R;

/**
 * Created by pablo on 1/28/2017.
 */

public class Contractor implements Parcelable{
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
                      String phone, String website, String portrait, double rating) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.website = website;
        this.portrait = portrait;
        this.rating = rating;
    }

    protected Contractor(Parcel in) {
        id = in.readString();
        firstName = in.readString();
        middleName = in.readString();
        lastName = in.readString();
        email = in.readString();
        phone = in.readString();
        website = in.readString();
        portrait = in.readString();
        rating = in.readDouble();
    }

    public static final Creator<Contractor> CREATOR = new Creator<Contractor>() {
        @Override
        public Contractor createFromParcel(Parcel in) {
            return new Contractor(in);
        }

        @Override
        public Contractor[] newArray(int size) {
            return new Contractor[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(firstName);
        dest.writeString(middleName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(website);
        dest.writeString(portrait);
        dest.writeDouble(rating);
    }

    public void populateContractorCard(View view) {
        if (view != null) {
            TextView nameTv = (TextView) view.findViewById(R.id.contractor_card_name);
            RatingBar ratingBar = (RatingBar) view.findViewById(R.id.contractor_card_rating);
            TextView phoneTv = (TextView) view.findViewById(R.id.contractor_card_phone);
            TextView emailTv = (TextView) view.findViewById(R.id.contractor_card_email);
            TextView websiteTv = (TextView) view.findViewById(R.id.contractor_card_website);

            nameTv.setText(this.getFullName());
            ratingBar.setRating((float) this.getRating());
            phoneTv.setText(this.getPhone());
            emailTv.setText(this.getEmail());
            websiteTv.setText(this.getWebsite());
        } else {
            Log.e("Contractor", "Attempted to call 'populateContractorCard' with a null view.");
        }
    }
}
