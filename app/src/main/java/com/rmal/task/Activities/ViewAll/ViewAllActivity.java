package com.rmal.task.Activities.ViewAll;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.rmal.task.Activities.Base.BaseActivity;
import com.rmal.task.Activities.UploadNew.UploadActivity;
import com.rmal.task.Adapters.DataFileAdapter;
import com.rmal.task.Models.DataFile;
import com.rmal.task.R;
import com.rmal.task.Tools.Constants;

import java.util.List;

public class ViewAllActivity extends BaseActivity implements ViewAllView {

    private SwipeRefreshLayout refreshLayout;
    private TextInputLayout inputEmail;
    private AppCompatEditText emailET;
    private ImageView refresh;
    private RecyclerView recyclerView;
    private TextView empty;
    private FloatingActionButton uploadNew;

    private ViewAllPresenter presenter;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_view_all;
    }

    @Override
    protected void initViews() {
        setSupportActionBarWithBack("Rmal", "View All Images/Videos");
        refreshLayout = findViewById(R.id.swipe_container);
        inputEmail = findViewById(R.id.input_email);
        emailET = findViewById(R.id.email_et);
        refresh = findViewById(R.id.refresh_btn);
        recyclerView = findViewById(R.id.data_recycler);
        uploadNew = findViewById(R.id.upload_btn);
        empty = findViewById(R.id.empty);

        presenter = new ViewAllPresenter(this, this);
    }

    @Override
    protected void initActions() {
        presenter.hideKeypad();
        String email = getIntent().getStringExtra(Constants.EMAIL);
        emailET.setText(email);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.hideKeypad();
                inputEmail.setError(null);
                String newEmail = emailET.getText().toString().trim();
                if (presenter.isNetworkAvailable()) {
                    if (presenter.isValidEmail(newEmail))
                        presenter.getData(newEmail);
                } else showEmptyMessage("No Internet Connection");
            }
        });

        uploadNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewAllActivity.this, UploadActivity.class);
                String newEmail = emailET.getText().toString().trim();
                intent.putExtra(Constants.EMAIL, newEmail);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String email = emailET.getText().toString().trim();
        if (presenter.isNetworkAvailable()) {
            presenter.getData(email);
        } else showEmptyMessage("No Internet Connection");

    }

    @Override
    public void showEmailError(int message) {
        inputEmail.setError(getText(message));
    }

    @Override
    public void showEmptyMessage(String message) {
        presenter.hideKeypad();
        recyclerView.setVisibility(View.GONE);
        empty.setText(message);
        empty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGetData(List<DataFile> fileList) {
        presenter.hideKeypad();
        recyclerView.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);

        DataFileAdapter adapter = new DataFileAdapter(this, fileList);
        int column = getResources().getInteger(R.integer.column_count);
        GridLayoutManager glm = new GridLayoutManager(this, column);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(glm);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void setLoading(boolean loading) {
        refreshLayout.setRefreshing(loading);
    }
}
