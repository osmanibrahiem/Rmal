package com.rmal.task.Tools.Connection;

import android.content.Context;

import com.rmal.task.Models.DataResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;

public class NetworkTool {

    private static final String TAG = "NetworkTool";

    public static void getAllDataFiles(final Context context, final String email, final Callback<DataResponse> apiCallBack) {
        final RetrofitTool retrofitTool = new RetrofitTool();
        retrofitTool.getRetrofit(URLS.URL_BASE, context).create(Api.class).getAllData(email)
                .enqueue(apiCallBack);
    }

    public static void uploadFile(final Context context, final String email, final String type, final MultipartBody.Part file, final Callback<DataResponse> apiCallBack) {
        final RetrofitTool retrofitTool = new RetrofitTool();
        RequestBody emailBody =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, email);
        RequestBody typeBody =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, type);
        retrofitTool.getRetrofit(URLS.URL_BASE, context).create(Api.class).storeFile(emailBody, typeBody, file)
                .enqueue(apiCallBack);
    }
}
