package com.woodyhi.imageselector;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.io.File;
import java.util.List;

public class IntentImageCrop {
    /** 裁剪输出文件 */
    static File corpedFile = null;

    private static void grantUriPermission(Context context, Intent intent, Uri dest) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            grantUriPermission_v22(context, intent, dest);
        }
    }

    public static void grantUriPermission_v22(Context context, Intent intent, Uri uri) {
        List<ResolveInfo> resInfoList = context.getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(
                    packageName,
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
    }

    public static boolean startActivityForCorp(@NonNull Activity activity,
                                               @NonNull String path,
                                               int width,
                                               int height,
                                               int requestCode) {
        corpedFile = createSaveCropedFile(activity);
        Uri src = getImageContentUri(activity, new File(path));
        Uri dest = Uri.fromFile(corpedFile);
        Intent intent = createCorpIntent(src, dest, width, height);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            grantUriPermission(activity, intent, dest);
            activity.startActivityForResult(intent, requestCode);
            return true;
        }
        return false;
    }

    public static boolean startActivityForCorp(@NonNull Fragment fragment,
                                               @NonNull String path,
                                               int width,
                                               int height,
                                               int requestCode) {
        Context context = fragment.getContext();
        corpedFile = createSaveCropedFile(context);
        Uri src = getImageContentUri(context, new File(path));
        Uri dest = Uri.fromFile(corpedFile);
        Intent intent = createCorpIntent(src, dest, width, height);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            grantUriPermission(context, intent, dest);
            fragment.startActivityForResult(intent, requestCode);
            return true;
        }
        return false;
    }

    // 保存路径
    static File createSaveCropedFile(Context context) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) cacheDir = context.getCacheDir();
        File parentFile = cacheDir;
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File photoFile = new File(parentFile, fileName);
        return photoFile;
    }

    public static Intent createCorpIntent(Uri src, Uri dest, int width, int height) {
        Intent intent = new Intent();
        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(src, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, dest);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        return intent;
    }

    public static void onHandleResult(int resultCode, OnResultCallback callback) {
        Uri uri = null;
        String path = null;
        try {
            if (resultCode == Activity.RESULT_OK) {
                uri = Uri.fromFile(corpedFile);
                path = uri.getPath();
            }
        } finally {
            if (corpedFile != null) corpedFile = null;
        }
        if (callback != null)
            callback.onResult(uri, path);
    }

    /**
     * 将图片文件插入到MediaStore.Images.Media.EXTERNAL_CONTENT_URI
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        Uri uri = null;
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Media._ID},
                        MediaStore.Images.Media.DATA + "=? ",
                        new String[]{filePath},
                        null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                uri = Uri.withAppendedPath(baseUri, "" + id);
            }
            cursor.close();
        }

        if (uri == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, filePath);
            uri = context.getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
        return uri;
    }
}
