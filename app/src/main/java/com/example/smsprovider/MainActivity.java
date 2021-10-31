package com.example.smsprovider;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE=1;
    private static final String TAG ="MainActivity" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkSmsReadPermission();
    }
    public void toVerityCodePage(View view){
        startActivity(new Intent(this,VerifyCodeActivity.class));
    }
    public void getSmsContent(View view){
        ContentResolver cr = getContentResolver();
        Uri uri=Uri.parse("content://sms/");
        Cursor query = cr.query(uri, null, null, null, null);
        String[] columnNames = query.getColumnNames();

        while (query.moveToNext()) {
            for (String columnName : columnNames) {
                Log.d(TAG, columnName+"===="+query.getString(query.getColumnIndex(columnName)));
            }
        }
        query.close();
    }

    private void checkSmsReadPermission() {
        int permissionResultCode = checkSelfPermission(Manifest.permission.READ_SMS);
        if (permissionResultCode!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_SMS},PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==PERMISSION_REQUEST_CODE){
            Log.d(TAG, "grantResults-->"+grantResults[0]);
        }
    }
}