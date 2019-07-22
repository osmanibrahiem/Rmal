package com.rmal.task.Activities.InputEmail;

import android.text.TextUtils;
import android.util.Patterns;

import com.rmal.task.Activities.Base.BasePresenter;
import com.rmal.task.R;

class InputEmailPresenter extends BasePresenter {

    private InputEmailActivity activity;
    private InputEmailView view;

    InputEmailPresenter(InputEmailActivity activity, InputEmailView view) {
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


}
