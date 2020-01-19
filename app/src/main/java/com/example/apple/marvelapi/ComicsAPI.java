package com.example.apple.marvelapi;

import com.example.apple.marvelapi.model.ComicsItem;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ComicsAPI {

    @GET
    public Call<ComicsItem> getJSON(@Url String string);
}
