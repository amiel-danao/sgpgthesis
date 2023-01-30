package com.example.sgpgthesis.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable{
    String name;
    String profileImg;
    String phoneNumber;
    String address;
    String gender;

    public UserModel() {
    }

    public UserModel(String name, String phoneNumber, String address, String gender, String profileImg) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.gender = gender;
        this.profileImg = profileImg;
    }

    protected UserModel(Parcel in) {
        name = in.readString();
        profileImg = in.readString();
        phoneNumber = in.readString();
        address = in.readString();
        gender = in.readString();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(phoneNumber);
        parcel.writeString(address);
        parcel.writeString(gender);
        parcel.writeString(profileImg);
    }
}
