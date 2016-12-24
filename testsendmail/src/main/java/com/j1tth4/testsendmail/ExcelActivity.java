package com.j1tth4.testsendmail;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.j1tth4.excelcreator.ExcelCreator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelActivity extends AppCompatActivity {

    public static final String TAG = "ExcelActivity";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excel);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excel_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_export_excel){
            exportToExcel();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            exportToExcel();
        }
    }

    private void exportToExcel() {
        if(isStoragePermissionGranted()) {
            List<Label> labels = new ArrayList<>();
            for(int row = 0; row < 100; row++){
                for(int col = 0; col < 10; col++){
                    labels.add(new Label(col, row, "row " + row + " col " + col));
                }
            }

            File sdcard = Environment.getExternalStorageDirectory();
            File xls = new File(sdcard, "file.xls");
            ExcelCreator.createExcel(xls, "Sheet1", labels);

//            final String fileName = "TodoList.xls";
//
//            //Saving file in external storage
//            File sdCard = Environment.getExternalStorageDirectory();
//
//            //file path
//            File file = new File(sdCard, fileName);
//
//            WorkbookSettings wbSettings = new WorkbookSettings();
//            wbSettings.setLocale(new Locale("en", "EN"));
//            WritableWorkbook workbook;
//
//            try {
//                workbook = Workbook.createWorkbook(file, wbSettings);
//                //Excel sheet name. 0 represents first sheet
//                WritableSheet sheet = workbook.createSheet("MyShoppingList", 0);
//
//                try {
//                    sheet.addCell(new Label(0, 0, "Subject")); // column and row
//                    sheet.addCell(new Label(1, 0, "Description"));
////                if (cursor.moveToFirst()) {
////                    do {
////                        String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TODO_SUBJECT));
////                        String desc = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TODO_DESC));
////
////                        int i = cursor.getPosition() + 1;
////                        sheet.addCell(new Label(0, i, title));
////                        sheet.addCell(new Label(1, i, desc));
////                    } while (cursor.moveToNext());
////                }
////                //closing cursor
////                cursor.close();
//                } catch (RowsExceededException e) {
//                    e.printStackTrace();
//                } catch (WriteException e) {
//                    e.printStackTrace();
//                }
//                workbook.write();
//                try {
//                    workbook.close();
//                } catch (WriteException e) {
//                    e.printStackTrace();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Excel Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.j1tth4.testsendmail/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Excel Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.j1tth4.testsendmail/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
