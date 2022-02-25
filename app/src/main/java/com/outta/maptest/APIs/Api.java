package com.outta.maptest.APIs;

import com.outta.maptest.Model.MapModel;

import retrofit2.Call;
import retrofit2.http.GET;


public interface Api {

    @GET("getlastpopupinfo/18682")
    Call<MapModel> getVehicleData();



}

