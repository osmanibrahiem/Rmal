package com.rmal.task.Activities.ViewAll;

import com.rmal.task.Models.DataFile;

import java.util.List;

interface ViewAllView {

    void showEmailError(int message);

    void showEmptyMessage(String message);

    void onGetData(List<DataFile> fileList);

    void setLoading(boolean loading);
}
