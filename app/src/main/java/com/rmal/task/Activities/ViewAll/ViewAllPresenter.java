package com.rmal.task.Activities.ViewAll;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.google.gson.Gson;
import com.rmal.task.Activities.Base.BasePresenter;
import com.rmal.task.Models.DataResponse;
import com.rmal.task.R;
import com.rmal.task.Tools.Connection.NetworkTool;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ViewAllPresenter extends BasePresenter {

    private String TAG = "dataFile";

    private ViewAllActivity activity;
    private ViewAllView view;

    ViewAllPresenter(ViewAllActivity activity, ViewAllView view) {
        super(activity);
        this.activity = activity;
        this.view = view;
    }

    boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            view.showEmailError(R.string.requried);
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.showEmailError(R.string.invalid_email);
            return false;
        }
        return true;
    }

    void getData(String email) {
        view.setLoading(true);
        Log.i(TAG, "email: " + email);
        NetworkTool.getAllDataFiles(activity, email, new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                final Gson gson = new Gson();
                Log.i(TAG, "onResponse: raw: " + response.raw().toString() + " body: " + gson.toJson(response.body()));
                Log.i(TAG, "onResponse: body: " + gson.toJson(response.body()));
                Log.i(TAG, "onResponse: url: " + call.request().url().toString());
                Log.i(TAG, "onResponse: response: " + gson.toJson(response));


                view.setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    DataResponse dataResponse = response.body();
                    if (dataResponse.isSuccess() && dataResponse.getData() != null && dataResponse.getData().size() > 0) {
                        view.onGetData(dataResponse.getData());
                    } else {
                        view.showEmptyMessage("There is no data found for this email");
                    }
                } else {
                    view.showEmptyMessage("There is no data found for this email");
                }
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                view.setLoading(false);
                view.showEmptyMessage("There is no data found for this email");
            }
        });
    }


}
