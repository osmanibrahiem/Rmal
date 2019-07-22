package com.rmal.task.Activities.UploadNew;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.rmal.task.Activities.Base.BasePresenter;
import com.rmal.task.Models.DataResponse;
import com.rmal.task.R;
import com.rmal.task.Tools.Connection.NetworkTool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;

class UploadPresenter extends BasePresenter {

    private UploadActivity activity;
    private UploadView view;
    private int takePicture = 44, recordVideo = 52, imageGallery = 73, videoGallery = 99;
    private static final String IMAGE_DIRECTORY = "/demonuts";

    private String fileName, filePath;
    private String type = "image";

    private String TAG = "uploadFile";

    UploadPresenter(UploadActivity activity, UploadView view) {
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

    void importImageVideo() {
        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showImagePickerOptions() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Upload Image/Video");

        // add a list
        String[] animals = {"Take a picture", "Record video", "Choose image from Gallery", "Choose video from Gallery"};
        builder.setItems(animals, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        onTakePictureSelected();
                        break;
                    case 1:
                        onRecordVideoSelected();
                        break;
                    case 2:
                        onChooseImageGallerySelected();
                        break;
                    case 3:
                        onChooseVideoGallerySelected();
                        break;
                }
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void onTakePictureSelected() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, takePicture);
    }

    private void onRecordVideoSelected() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        activity.startActivityForResult(intent, recordVideo);
    }

    private void onChooseImageGallerySelected() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        activity.startActivityForResult(galleryIntent, imageGallery);
    }

    private void onChooseVideoGallerySelected() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

        activity.startActivityForResult(galleryIntent, videoGallery);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Grant Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton(activity.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, 101);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        if (requestCode == imageGallery) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), contentURI);
                    File path = saveImage(bitmap);
                    fileName = path.getName();
                    filePath = getPath(contentURI);
                    type = "image";

                    view.setImage(bitmap);

                } catch (IOException e) {
                    Toast.makeText(activity, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == takePicture) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            File image = saveImage(thumbnail);
            fileName = image.getName();
            filePath = getPath(data.getData());
            type = "image";

            view.setImage(thumbnail);
        } else if (requestCode == videoGallery) {

            if (data != null) {
                Uri contentURI = data.getData();

                filePath = getPath(contentURI);
                type = "video";

                saveVideoToInternalStorage(filePath);
                view.setVideo(contentURI);


            }

        } else if (requestCode == recordVideo) {
            Uri contentURI = data.getData();
            filePath = getPath(contentURI);
            Log.d("frrr", filePath);
            type = "video";
            saveVideoToInternalStorage(filePath);
            view.setVideo(contentURI);
        }
    }

    private File saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(activity,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();

            return f;
        } catch (IOException e1) {
            return null;
        }
    }

    private void saveVideoToInternalStorage(String filePath) {

        File newfile;

        try {

            File currentFile = new File(filePath);
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
            newfile = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".mp4");
            fileName = newfile.getName();

            if (!wallpaperDirectory.exists()) {
                wallpaperDirectory.mkdirs();
            }

            if (currentFile.exists()) {

                InputStream in = new FileInputStream(currentFile);
                OutputStream out = new FileOutputStream(newfile);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                Log.v("vii", "Video file saved successfully.");
            } else {
                Log.v("vii", "Video saving failed. Source file missing.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    void uploadFile(String email) {
        if (TextUtils.isEmpty(filePath) || TextUtils.isEmpty(fileName)) {
            Toast.makeText(activity, "Choose a image/video", Toast.LENGTH_SHORT).show();
            return;
        }
        view.showLoading();
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), filePath);
        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("value", fileName, requestFile);
        NetworkTool.uploadFile(activity, email, type, multipartBody,
                new Callback<DataResponse>() {
                    @Override
                    public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                        final Gson gson = new Gson();
                        Log.i(TAG, "onResponse: raw: " + response.raw().toString() + " body: " + gson.toJson(response.body()));
                        Log.i(TAG, "onResponse: body: " + gson.toJson(response.body()));
                        Log.i(TAG, "onResponse: url: " + call.request().url().toString());
                        Log.i(TAG, "onResponse: response: " + gson.toJson(response));


                        view.hideLoading();
                        if (response.isSuccessful() && response.body() != null) {
                            DataResponse dataResponse = response.body();
                            if (dataResponse.getStatus().equals("ok")) {
                                view.setSuccess();
                            } else {
                                Toast.makeText(activity, "Error on upload", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(activity, "Error on upload", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DataResponse> call, Throwable t) {
                        view.hideLoading();
                        Toast.makeText(activity, "Error on upload", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
