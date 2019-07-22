package com.rmal.task.Activities.InputEmail;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.rmal.task.Activities.Base.BaseActivity;
import com.rmal.task.Activities.ViewAll.ViewAllActivity;
import com.rmal.task.R;
import com.rmal.task.Tools.Constants;

public class InputEmailActivity extends BaseActivity implements InputEmailView {

    private TextInputLayout inputEmail;
    private AppCompatEditText emailET;
    private FloatingActionButton done;

    private InputEmailPresenter presenter;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_input_email;
    }

    @Override
    protected void initViews() {
        inputEmail = findViewById(R.id.input_email);
        emailET = findViewById(R.id.email_et);
        done = findViewById(R.id.done_btn);

        presenter = new InputEmailPresenter(this, this);
    }

    @Override
    protected void initActions() {
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.hideKeypad();
                inputEmail.setError(null);
                String email = emailET.getText().toString().trim();
                if (presenter.isNetworkAvailable() &&
                        presenter.isValidEmail(email)) {
                    viewAllData(email);
                }
            }
        });
    }

    @Override
    public void showEmailError(int message) {
        inputEmail.setError(getText(message));
    }

    private void viewAllData(String email) {
        Intent intent = new Intent(this, ViewAllActivity.class);
        intent.putExtra(Constants.EMAIL, email);
        startActivity(intent);
    }
}
