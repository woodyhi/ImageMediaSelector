package com.woodyhi.imageselector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.woodyhi.uriutil.UriUtil;

import java.io.File;

public class IntentImageSelector {

    /**
     * 调用相机时被保存的照片文件，动态变量
     */
    private static File photoFile = null;

    static void setPhotoFile(File file) {
        photoFile = file;
    }

    public static void startIntentForResult(Activity activity, int requestCode) {
        Intent intent = IntentCreator.createFetchImageIntent(activity);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startIntentForResult(Fragment fragment, int requestCode) {
        Intent intent = IntentCreator.createFetchImageIntent(fragment.getContext());
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void onHandleResult(Context context, int resultCode, Intent data, OnResultCallback callback) {
        Uri uri = null;
        String path = null;
        try {
            if (resultCode == Activity.RESULT_OK) {
                // 从相册或文件选择的图片
                if (data != null && data.getData() != null) {
                    uri = data.getData();
                }
                // 相机拍摄返回
                else {
                    uri = Uri.fromFile(photoFile);
                }

                if (uri != null) path = UriUtil.getPath(context, uri);
            }
        } finally {
            if (photoFile != null) photoFile = null;
        }

        if (callback != null)
            callback.onResult(uri, path);
    }
}
