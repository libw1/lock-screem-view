package com.example.libowen.lockscreenviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LockScreenViewGroup lockScreen = findViewById(R.id.lock_screen_view);
        int[] answer = {1,2,5,8,9};
        lockScreen.setAnswer(answer);
    }
}
