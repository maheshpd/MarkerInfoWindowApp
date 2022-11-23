package com.example.markerinfowindowapp.network;

import com.example.markerinfowindowapp.model.ListLocationModel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("/v1/801f4111-ed01-4fb2-a7b9-a69f9e41834f")
    Call<ListLocationModel> getAllLocation();

}
