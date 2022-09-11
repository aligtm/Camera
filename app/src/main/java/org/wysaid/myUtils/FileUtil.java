package org.wysaid.myUtils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileUtil {
    public static final String LOG_TAG = "libCGE_java";
    //public static final File externalStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    public static final File externalStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    private static String mDefaultFolder = "Techkiii";
    private static String mDefaultFolder1 = "Beauty_Cam";
    public static String packageFilesDirectory = null;
    public static String storagePath = null;
    public static String storagePath1 = null;

    public static void setDefaultFolder(String defaultFolder) {
        mDefaultFolder = defaultFolder;
    }

    public static String getPath() {
        return getPath(null);
    }

    public static String getPath(Context context) {
        if (storagePath == null) {
            storagePath = externalStorageDirectory.getAbsolutePath() + "/" + mDefaultFolder;
            File file = new File(storagePath);
            if (!(file.exists() || file.mkdirs())) {
                storagePath = getPathInPackage(context, true);
            }
        }
        return storagePath;
    }

    public static String getPath1() {
        return getPath1(null);
    }

    public static String getPath1(Context context) {
        if (storagePath1 == null) {
            storagePath1 = externalStorageDirectory.getAbsolutePath() + "/" + mDefaultFolder1;
            File file = new File(storagePath1);
            if (!(file.exists() || file.mkdirs())) {
                storagePath1 = getPathInPackage(context, true);
            }
        }
        return storagePath1;
    }

    public static String getPathInPackage(Context context, boolean grantPermissions) {
        if (context == null || packageFilesDirectory != null) {
            return packageFilesDirectory;
        }
        String path = context.getFilesDir() + "/" + mDefaultFolder;
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("libCGE_java", "在pakage目录创建CGE临时目录失败!");
                return null;
            } else if (grantPermissions) {
                if (file.setExecutable(true, false)) {
                    Log.i("libCGE_java", "Package folder is executable");
                }
                if (file.setReadable(true, false)) {
                    Log.i("libCGE_java", "Package folder is readable");
                }
                if (file.setWritable(true, false)) {
                    Log.i("libCGE_java", "Package folder is writable");
                }
            }
        }
        packageFilesDirectory = path;
        return packageFilesDirectory;
    }

    public static void saveTextContent(String text, String filename) {
        Log.i("libCGE_java", "Saving text : " + filename);
        try {
            FileOutputStream fileout = new FileOutputStream(filename);
            fileout.write(text.getBytes());
            fileout.flush();
            fileout.close();
        } catch (Exception e) {
            Log.e("libCGE_java", "Error: " + e.getMessage());
        }
    }

    public static String getTextContent(String filename) {
        Log.i("libCGE_java", "Reading text : " + filename);
        if (filename == null) {
            return null;
        }
        String content = "";
        byte[] buffer = new byte[256];
        try {
            FileInputStream filein = new FileInputStream(filename);
            while (true) {
                int len = filein.read(buffer);
                if (len <= 0) {
                    return content;
                }
                content = content + new String(buffer, 0, len);
            }
        } catch (Exception e) {
            Log.e("libCGE_java", "Error: " + e.getMessage());
            return null;
        }
    }
}
