package com.samsungschool.umbrellaproject.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String phoneNumber;
    private String name;
    private String mail;

    public User() {
        // Default constructor
    }

    protected User(Parcel in) {
        phoneNumber = in.readString();
        name = in.readString();
        mail = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phoneNumber);
        dest.writeString(name);
        dest.writeString(mail);
    }
}
