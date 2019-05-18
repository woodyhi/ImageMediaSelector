package com.woodyhi.imageselector;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import java.io.File;

public class ImageSelectorProvider extends FileProvider {
    public static final String PATH = "imageselector/img";
    public static final String AUTHORITY_SUFFIX = ".image.selector";

    public static Uri getUriForFile(@NonNull Context context, @NonNull File file) {
        String authority = context.getPackageName() + AUTHORITY_SUFFIX;
        return FileProvider.getUriForFile(context, authority, file);
    }

}
