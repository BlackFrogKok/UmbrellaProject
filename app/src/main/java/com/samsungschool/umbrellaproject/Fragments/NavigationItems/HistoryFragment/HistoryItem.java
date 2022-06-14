package com.samsungschool.umbrellaproject.Fragments.NavigationItems.HistoryFragment;

import android.os.Parcel;
import android.os.Parcelable;

import com.samsungschool.umbrellaproject.Station;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class HistoryItem implements Parcelable {
    private String address;
    private String time;
    private String date;
    private String stationGetID;
    private String stationPutID;
    private String timeGet;
    private String timePut;
    private boolean status;


    public HistoryItem(){}


    public HistoryItem(String address, String time, String date){
        this.address = address;
        this.time = time;
        this.date = date;

    }


    protected HistoryItem(Parcel in) {
        address = in.readString();
        time = in.readString();
        date = in.readString();
    }

    public static final Creator<HistoryItem> CREATOR = new Creator<HistoryItem>() {
        @Override
        public HistoryItem createFromParcel(Parcel in) {
            return new HistoryItem(in);
        }

        @Override
        public HistoryItem[] newArray(int size) {
            return new HistoryItem[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(timePut);
        dest.writeString(timeGet);
        dest.writeString(stationPutID);
        dest.writeString(stationGetID);
        dest.writeString(address);
        dest.writeString(time);
        dest.writeString(date);
    }


    public String getStationGetID() {
        return stationGetID;
    }

    public void setStationGetID(String stationGetID) {
        this.stationGetID = stationGetID;
    }

    public String getStationPutID() {
        return stationPutID;
    }

    public void setStationPutID(String stationPutID) {
        this.stationPutID = stationPutID;
    }

    public String getTimeGet() {
        return timeGet;
    }

    public void setTimeGet(String timeGet) {
        this.timeGet = timeGet;
    }

    public String getTimePut() {
        return timePut;
    }

    public void setTimePut(String timePut) {
        this.timePut = timePut;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
