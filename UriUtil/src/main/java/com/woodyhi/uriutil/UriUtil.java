package com.woodyhi.uriutil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

public class UriUtil {

    @SuppressLint("NewApi")
    public static String getPath(Context context, Uri uri) {
        String path = ContentUriApi19.getPath(context, uri);
        if (path == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                path = ContentUriApi24.getPath(context, uri);
            }
        }
        return path;
    }

}
