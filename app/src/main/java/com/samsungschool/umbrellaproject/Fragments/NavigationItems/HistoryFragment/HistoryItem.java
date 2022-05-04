package com.samsungschool.umbrellaproject.Fragments.NavigationItems.HistoryFragment;

public class HistoryItem {
    private Integer date = 0;
    private Integer duration  = 0;

    public HistoryItem(Integer date){
        this.date = date;
        this.duration = 0;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
