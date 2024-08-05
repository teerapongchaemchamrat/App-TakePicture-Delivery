package com.example.btc_delivery_v2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class Zxing_scanner extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(Zxing_capture_Activity.class);
        integrator.setOrientationLocked(true);
        integrator.initiateScan();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
            } else {
                String scannedData = result.getContents();
                if(scannedData.contains("/")){
                    String[] inputArr = scannedData.split("/");
                    String co_num = inputArr[0];
                    String co_line = inputArr[1];

                    Intent intent = new Intent();
                    intent.putExtra("co_num", co_num);
                    intent.putExtra("co_line", co_line);
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        }

    }
}
