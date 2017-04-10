package munoz.pablo.directorio.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pablo on 4/9/2017.
 *
 * This class represents a User Account. Users may or may not be contractors.
 *
 */

public class Account implements Parcelable {
    private String id;
    private String email;
    private boolean isContractor;
    private String token;
    private Contractor contractor;
    private static Account anonymous = null;

    protected Account(Parcel in) {
        id = in.readString();
        email = in.readString();
        isContractor = in.readByte() != 0;
        token = in.readString();
        contractor = in.readParcelable(Contractor.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(email);
        dest.writeByte((byte) (isContractor ? 1 : 0));
        dest.writeString(token);
        dest.writeParcelable(contractor, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    /**
     * Returns a special, anonymous account that is used as the instance of account to use
     * in cases where the user is not registered or hasn't logged in yet.
     */
    public static Account getAnonymous() {
        if (anonymous == null) {
            anonymous = new Account(
                    "00000000-0000-0000-0000-000000000000", "anonymous@anonymous.com", false, "Bearer: None");
        }

        return anonymous;
    }

    public Account(String id, String email, boolean isContractor, String token) {
        this.id = id;
        this.email = email;
        this.isContractor = isContractor;
        this.token = token;
    }

    public String getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public Contractor getContractor() {
        return this.contractor;
    }

    public void setContractor(Contractor contractor) {
        this.contractor = contractor;
    }

    public boolean getIsContractor() {
        return this.isContractor;
    }

    public boolean isAnonymous() {
        return this == anonymous;
    }

}
