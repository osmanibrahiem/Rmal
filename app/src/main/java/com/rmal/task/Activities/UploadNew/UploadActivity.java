package com.rmal.task.Activities.UploadNew;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.rmal.task.Activities.Base.BaseActivity;
import com.rmal.task.R;
import com.rmal.task.Tools.Constants;

public class UploadActivity extends BaseActivity implements UploadView {

    private TextInputLayout inputEmail;
    private AppCompatEditText emailET;
    private ImageView imageView;
    private VideoView videoView;
    private FloatingActionButton upload;
    private AppCompatButton pick;
    private ProgressDialog progressBar;

    private UploadPresenter presenter;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_upload;
    }

    @Override
    protected void initViews() {
        setSupportActionBarWithBack("Rmal", "Upload New Image/Video");
        inputEmail = findViewById(R.id.input_email);
        emailET = findViewById(R.id.email_et);
        imageView = findViewById(R.id.image_view);
        videoView = findViewById(R.id.video_view);
        upload = findViewById(R.id.upload_btn);
        pick = findViewById(R.id.pick_btn);

        presenter = new UploadPresenter(this, this);
    }

    @Override
    protected void initActions() {
        presenter.hideKeypad();
        String email = getIntent().getStringExtra(Constants.EMAIL);
        emailET.setText(email);

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.importImageVideo();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.hideKeypad();
                inputEmail.setError(null);
                String newEmail = emailET.getText().toString().trim();
                if (presenter.isNetworkAvailable()) {
                    if (presenter.isValidEmail(newEmail))
                        presenter.uploadFile(newEmail);
                }
            }
        });
    }

    @Override
    public void showEmailError(int message) {
        inputEmail.setError(getText(message));
    }

    @Override
    public void showLoading() {
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setMessage("File uploading ...");
        progressBar.show();
        if (videoView.isPlaying())
            videoView.pause();
    }

    @Override
    public void hideLoading() {
        if (progressBar != null)
            progressBar.cancel();
    }

    @Override
    public void setImage(Bitmap image) {
        pick.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(image);
    }

    @Override
    public void setVideo(Uri contentURI) {
        pick.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        videoView.setVideoURI(contentURI);
        videoView.requestFocus();
        videoView.start();
    }

    @Override
    public void setSuccess() {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        presenter.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
