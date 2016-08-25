package com.j1tth4.testsendmail;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.j1tth4.gmailsender.GMailSender;

import java.io.File;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initInstances();
    }

    private void initInstances(){
        mTextView = (TextView) findViewById(R.id.textView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_send){
            send();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
            send();
        }
    }

    private void send(){
        if(isStoragePermissionGranted()) {
            getMailSenderObservable().subscribe(new Observer<Void>() {
                @Override
                public void onCompleted() {
                    mTextView.setText("send complete");
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "send fail " + e.getMessage());
                    mTextView.setText("send fail " + e.getMessage());
                }

                @Override
                public void onNext(Void aVoid) {
                    mTextView.setText("send success");
                }
            });
        }
    }

    private Observable<Void> getMailSenderObservable(){
        final String body = "<pre>         FU.5 Coffee The Street         \n" +
                "               TEST SHOP               \n" +
                "Print Date: 8/22/16 3:07 PM\n" +
                "Receipt No.: RC22082016/0001\n" +
                "Staff: System Administrator\n" +
                "-----------------------------------------\n" +
                "1  H. Espresso เอสเพรสโซ่            90.00\n" +
                "1  H. Cappuccino คาปูซิโนร้อน         110.00\n" +
                "1  H. Latte ลาเต้ร้อน                115.00\n" +
                "1  I. Latte ลาเต้เย็น                120.00\n" +
                "1  H. Mocha มอคค่าร้อน               120.00\n" +
                "1  B. Chocolate ช็อคโกเเลตปั่น        145.00\n" +
                "1  H. Milk Tea ชานมร้อน              95.00\n" +
                "1  I. Green Tea ชาเขียวเย็น          135.00\n" +
                "1  Spaghetti Cobonara สปาเก็ตตี้...   160.00\n" +
                "1  Summer has begun ซัมเมอร์ เเอ...  150.00\n" +
                "1  Berry Fizz เบอร์รี่ ฟีส             120.00\n" +
                "1  Berry Fizz เบอร์รี่ ฟีส             120.00\n" +
                "-----------------------------------------\n" +
                "Items 12                         1,480.00\n" +
                "Total.........                   1,480.00\n" +
                "Cash 1,500.00                Change 20.00\n" +
                "=========================================\n" +
                "VAT 7%                              96.82\n" +
                "Sale before VAT                  1,383.18\n" +
                "              VAT INCLUDED              \n" +
                "               THANK YOU               \n" +
                "</pre>";
        return Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                File dir = Environment.getExternalStorageDirectory();
                File attachFile = new File(dir, "knox.pdf");
                GMailSender.Builder builder = new GMailSender.Builder("jitthapong@gmail.com", "4rjs4l33")
                        .subject("Bill")
                        .sender("Toi")
                        .recipients("jitthapong@synaturegroup.com,jitthapong@gmail.com,toy_overdrive@hotmail.com")
                        .body(body)
                        .contentType("text/html; charset=utf-8")
                        .attachFile(attachFile);
                GMailSender sender = builder.create();
                sender.sendMail();
                return null;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }
}
