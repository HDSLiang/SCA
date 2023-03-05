package com.example.sca.ui.cloud.transfer;

import java.io.File;

public interface ImageDownLoadCallBack {
    void onDownLoadSuccess(File file);

    void onDownLoadFailed();
}
