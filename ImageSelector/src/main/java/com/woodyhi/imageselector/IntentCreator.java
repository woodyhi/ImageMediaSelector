package com.woodyhi.imageselector;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;

import java.io.File;
import java.util.Arrays;

public class IntentCreator {

    public static Intent createChooserIntent(String title, Intent... intents) {
        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        if (title != null) {
            chooser.putExtra(Intent.EXTRA_TITLE, title);
        }
        if (intents != null && intents.length > 0) {
            chooser.putExtra(Intent.EXTRA_INTENT, intents[0]);

            if (intents.length > 1) {
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                        Arrays.copyOfRange(intents, 1, intents.length));
            }
        }
        return chooser;
    }

    public static Intent createFetchImageIntent(Context context) {
        Intent intent = createChooserIntent("选择图片来源",
                createPickImageIntent(), createCompatCaputureIntent(context));
        return intent;
    }

    public static Intent createPickImageIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        return intent;
    }

    public static Intent createPickImageIntent_2() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        } else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        return intent;
    }

    public static Intent createCompatCaputureIntent(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return createCaputurePhotoIntent_v24(context);
        } else {
            return createCaputurePhotoIntent(context);
        }
    }

    public static Intent createCaputurePhotoIntent(Context context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File cacheDir = context.getExternalCacheDir();
        File photoFile = new File(cacheDir, System.currentTimeMillis() + ".jpg");
        if (!photoFile.getParentFile().exists()) {
            photoFile.getParentFile().mkdirs();
        }
        IntentImageSelector.setPhotoFile(photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        return intent;
    }

    // content://com.june.imageselector.demo.myprovider/image_selector/1557110568178.jpg
    @RequiresApi(Build.VERSION_CODES.N)
    public static Intent createCaputurePhotoIntent_v24(Context context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) cacheDir = context.getCacheDir();

        File photoFile = new File(cacheDir, ImageSelectorProvider.PATH + "/" + System.currentTimeMillis() + ".jpg");
        if (!photoFile.getParentFile().exists()) {
            photoFile.getParentFile().mkdirs();
        }
        IntentImageSelector.setPhotoFile(photoFile);
        Uri contentUri = ImageSelectorProvider.getUriForFile(context, photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        return intent;
    }

}
