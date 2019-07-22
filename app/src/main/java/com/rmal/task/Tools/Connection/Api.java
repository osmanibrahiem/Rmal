package com.rmal.task.Tools.Connection;

import com.rmal.task.Models.DataResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    @FormUrlEncoded
    @POST(URLS.URL_GET_ALL)
    Call<DataResponse> getAllData(@Field("email") String email);

    @Multipart
    @POST(URLS.URL_STORE)
    Call<DataResponse> storeFile(@Part("email") RequestBody email,
                                 @Part("type") RequestBody type,
                                 @Part MultipartBody.Part file);

}
