package com.example.smsprovider;

import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifyCodeActivity extends AppCompatActivity {

    private static final String TAG = "VerifyCodeActivity";
    private EditText mPhoneNumEt;
    private Button mCountDownBtn;
    private EditText mVerifyCodeEt;
    private Button mCommitBtn;

    private static final int MATCH_CODE=1;
    private static UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI("sms","#",MATCH_CODE);
    }

    private CountDownTimer mCountDownTimer=new CountDownTimer(60*1000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            mCountDownBtn.setEnabled(false);
            mCountDownBtn.setText(String.format("重新获取(%d)",millisUntilFinished/1000));
        }

        @Override
        public void onFinish() {
            mCountDownBtn.setEnabled(true);
            mCountDownBtn.setText(String.format("获取验证码"));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);
        initView();
        initEvent();
        Uri uri=Uri.parse("content://sms/");
        getContentResolver().registerContentObserver(uri, true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange, @Nullable Uri uri) {
                Log.d(TAG, "selfChange-->"+selfChange);
                if (uriMatcher.match(uri)==MATCH_CODE) {
                    Log.d(TAG, "uri-->"+uri);
                    Cursor query = getContentResolver().query(uri, new String[]{"body"}, null, null, null);
                    if (query.moveToNext()) {
                        String body = query.getString(0);
                        Log.d(TAG, "body==="+body);
                        handlerBody(body);
                    }
                    query.close();
                }
            }
        });
    }

    private void handlerBody(String body) {
        if (!TextUtils.isEmpty(body)&&body.startsWith("判断短信内容")) {
            Pattern p=Pattern.compile("(?<![0-9])([0-9]{4})(?![0-9])");//4位验证码
            Matcher matcher=p.matcher(body);
            boolean contain=matcher.find();
            if (contain) {
                String group = matcher.group();
                Log.d(TAG, "verifyCode-->"+ group);
                mVerifyCodeEt.setText(group);
                mVerifyCodeEt.setFocusable(true);
            }

        }
    }

    private void initEvent() {
       mCountDownBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String phoneNum = mPhoneNumEt.getText().toString().trim();
               if (TextUtils.isEmpty(phoneNum)) {
                   Toast.makeText(VerifyCodeActivity.this,"手机号码不能为空",Toast.LENGTH_SHORT).show();
                   return;
               }
               // TODO: 向服务器请求发送验证码到手机
               mCountDownTimer.start();
           }
       });
       mCommitBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String phoneNum = mPhoneNumEt.getText().toString().trim();
               String verifyCode = mVerifyCodeEt.getText().toString().trim();
               if (TextUtils.isEmpty(phoneNum)||TextUtils.isEmpty(verifyCode)){

                   Toast.makeText(VerifyCodeActivity.this,"验证码和手机号都不能为空",Toast.LENGTH_SHORT).show();
                   return;
               }
               // TODO: 向服务器提交
           }
       });
    }

    private void initView() {
        mPhoneNumEt = this.findViewById(R.id.phone_num_et);
        mVerifyCodeEt = this.findViewById(R.id.verify_code_et);
        mCountDownBtn = this.findViewById(R.id.count_down_btn);
        mCommitBtn = this.findViewById(R.id.submit_btn);
    }


}
