package com.example.markerinfowindowapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListLocationModel {
    @SerializedName("data")
    private List<LocationModel> mData;

    public List<LocationModel> getmData() {
        return mData;
    }

    public void setmData(List<LocationModel> mData) {
        this.mData = mData;
    }
}
