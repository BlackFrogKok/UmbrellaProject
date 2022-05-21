package com.samsungschool.umbrellaproject.Fragments.NavigationItems.HistoryFragment;

import com.samsungschool.umbrellaproject.Station;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class HistoryItem {
    //todo with firebase
    private String address;
    private String time;
    private String date;

    public HistoryItem(){}


    public HistoryItem(String address, String time, String date){
        this.address = address;
        this.time = time;
        this.date = date;

    }


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
}
