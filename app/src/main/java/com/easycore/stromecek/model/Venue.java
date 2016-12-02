package com.easycore.stromecek.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jakub Begera (jakub@easycoreapps.com) on 02.12.16.
 */
public class Venue implements Parcelable {
    private String name;
    private String address;
    private String picture;
    private double dmsCost;
    private String dmsNumber;
    private String dmsText;
    private String hls;
    private String dsc;

    public Venue() {
    }

    protected Venue(Parcel in) {
        name = in.readString();
        address = in.readString();
        picture = in.readString();
        dmsCost = in.readDouble();
        dmsNumber = in.readString();
        dmsText = in.readString();
        hls = in.readString();
        dsc = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(picture);
        dest.writeDouble(dmsCost);
        dest.writeString(dmsNumber);
        dest.writeString(dmsText);
        dest.writeString(hls);
        dest.writeString(dsc);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Venue> CREATOR = new Creator<Venue>() {
        @Override
        public Venue createFromParcel(Parcel in) {
            return new Venue(in);
        }

        @Override
        public Venue[] newArray(int size) {
            return new Venue[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public double getDmsCost() {
        return dmsCost;
    }

    public void setDmsCost(double dmsCost) {
        this.dmsCost = dmsCost;
    }

    public String getDmsNumber() {
        return dmsNumber;
    }

    public void setDmsNumber(String dmsNumber) {
        this.dmsNumber = dmsNumber;
    }

    public String getDmsText() {
        return dmsText;
    }

    public void setDmsText(String dmsText) {
        this.dmsText = dmsText;
    }

    public String getHls() {
        return hls;
    }

    public void setHls(String hls) {
        this.hls = hls;
    }

    public String getDsc() {
        return dsc;
    }

    public void setDsc(String dsc) {
        this.dsc = dsc;
    }
}
