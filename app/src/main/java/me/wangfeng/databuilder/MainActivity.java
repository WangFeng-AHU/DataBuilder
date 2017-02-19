package me.wangfeng.databuilder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private Person createPerson() {
        return new Person$$Builder().id(1L).name("jack").age(20).build();
    }
}
