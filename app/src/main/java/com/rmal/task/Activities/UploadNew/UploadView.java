package com.rmal.task.Activities.UploadNew;

import android.graphics.Bitmap;
import android.net.Uri;

interface UploadView {

    void showEmailError(int message);

    void showLoading();

    void hideLoading();

    void setImage(Bitmap image);

    void setVideo(Uri contentURI);

    void setSuccess();
}
