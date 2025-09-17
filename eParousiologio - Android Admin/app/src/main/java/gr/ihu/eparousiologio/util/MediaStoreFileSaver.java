package gr.ihu.eparousiologio.util;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Objects;

public class MediaStoreFileSaver implements ExcelExporter.Output {

    private static final String MIME_XLSX =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String SUBDIR = "eParousiologio";

    private final Context ctx;
    private Uri outUri;
    private String lastName;

    public MediaStoreFileSaver(Context ctx) {
        this.ctx = ctx.getApplicationContext();
    }

    @Override
    public OutputStream open(String suggestedFileName) throws Exception {
        if (suggestedFileName != null && !suggestedFileName.toLowerCase(Locale.US).endsWith(".xlsx")) {
            suggestedFileName = suggestedFileName + ".xlsx";
        }

        lastName = suggestedFileName;
        ContentResolver cr = ctx.getContentResolver();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

            ContentValues cv = new ContentValues();
            cv.put(MediaStore.MediaColumns.DISPLAY_NAME, suggestedFileName);
            cv.put(MediaStore.MediaColumns.MIME_TYPE, MIME_XLSX);
            cv.put(MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + File.separator + SUBDIR);

            outUri = cr.insert(collection, cv);
            if (outUri == null) {
                throw new IllegalStateException("Αποτυχία δημιουργίας αρχείου (Android 10+).");
            }

            OutputStream os;
            try {
                os = cr.openOutputStream(outUri, "w");
                if (os == null) {
                    throw new IllegalStateException("Αποτυχία ανοίγματος ροής εγγραφής (Android 10+).");
                }
                return os;
            } catch (Exception e) {
                try { cr.delete(outUri, null, null); } catch (Exception ignore) {}
                outUri = null;
                throw e;
            }

        } else {
            int w = ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (w != PackageManager.PERMISSION_GRANTED) {
                throw new SecurityException("Λείπει η άδεια WRITE_EXTERNAL_STORAGE (Android 9). Δώσε την άδεια πριν το export.");
            }

            String state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state)) {
                throw new IllegalStateException("Το external storage δεν είναι διαθέσιμο: " + state);
            }

            File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (downloads == null) {
                throw new IllegalStateException("Το Downloads directory είναι null.");
            }
            if (!downloads.exists() && !downloads.mkdirs()) {
                throw new IllegalStateException("Αποτυχία δημιουργίας φακέλου Downloads: " + downloads.getAbsolutePath());
            }

            File appDir = new File(downloads, SUBDIR);
            if (!appDir.exists() && !appDir.mkdirs()) {
                throw new IllegalStateException("Αποτυχία δημιουργίας φακέλου: " + appDir.getAbsolutePath());
            }

            File target = new File(appDir, Objects.requireNonNull(suggestedFileName));
            if (target.exists() && !target.delete()) {
                throw new IllegalStateException("Αποτυχία αντικατάστασης υπάρχοντος αρχείου: " + target.getAbsolutePath());
            }

            outUri = Uri.fromFile(target);
            return new FileOutputStream(target, false);
        }
    }

    @Override
    public void close(OutputStream os) throws Exception {
        if (os != null) {
            os.flush();
            os.close();
        }

        if (Build.VERSION.SDK_INT == 28 && outUri != null && "file".equals(outUri.getScheme())) {
            android.media.MediaScannerConnection.scanFile(
                    ctx,
                    new String[]{ new File(Objects.requireNonNull(outUri.getPath())).getAbsolutePath() },
                    new String[]{ MIME_XLSX },
                    null
            );
        }
    }

    @Override
    public String getLastFilePathOrName() {
        return lastName;
    }
}
