package com.rmal.task.Activities.Base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public abstract class BasePresenter {

    private BaseActivity activity;

    public BasePresenter(BaseActivity activity) {
        this.activity = activity;
    }

    public void hideKeypad() {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//            view.setFocusableInTouchMode(false);
//            view.setFocusable(false);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager mConnectivity =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivity.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            Toast.makeText(activity, "No Internet Access", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}
