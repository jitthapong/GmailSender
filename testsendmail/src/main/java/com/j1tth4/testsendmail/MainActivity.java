package com.j1tth4.testsendmail;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.j1tth4.gmailsender.GMailSender;

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

    private void send(){
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

    private Observable<Void> getMailSenderObservable(){
        return Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                GMailSender.Builder builder = new GMailSender.Builder("jitthapong@gmail.com", "4rjs4l33")
                        .subject("Test Send")
                        .sender("Jitthapong")
                        .recipients("jitthapong@synaturegroup.com,jitthapong@gmail.com,toy_overdrive@hotmail.com")
                        .body("Hello world");
                GMailSender sender = builder.create();
                sender.sendMail();
                return null;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }
}
