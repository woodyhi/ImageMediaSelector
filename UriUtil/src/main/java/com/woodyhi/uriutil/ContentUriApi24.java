package com.woodyhi.uriutil;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ContentUriApi24 {

    public static String getPath(Context context, Uri contentUri) {
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            File destFile = new File(context.getExternalCacheDir(), fileName);
            if (destFile.exists()) {
                destFile.delete();
            }
            copyFile(context, contentUri, destFile);
            return destFile.getAbsolutePath();
        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String path = uri.getPath();
        if (path == null) return null;
        String fileName = null;
        int idx = path.lastIndexOf('/');
        if (idx > -1) {
            fileName = path.substring(idx + 1);
        }
        return fileName;
    }


    public static void copyFile(Context context, Uri srcUri, File destFile) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getContentResolver().openInputStream(srcUri);
            if (is == null) return;
            os = new FileOutputStream(destFile);
            transfer(is, os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
            }
            try {
                is.close();
            } catch (IOException e) {
            }
        }
    }

    public static void transfer(InputStream is, OutputStream os) throws IOException {
        final int BUFFER_SIZE = 1024;
        BufferedInputStream bis = new BufferedInputStream(is, BUFFER_SIZE);
        BufferedOutputStream bos = new BufferedOutputStream(os, BUFFER_SIZE);
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int count = 0;
            while ((count = bis.read(buffer)) > 0) {
                bos.write(buffer, 0, count);
            }
            bos.flush();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
            }
            try {
                bis.close();
            } catch (IOException e) {
            }
        }
    }
}
