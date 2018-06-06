package com.hydbest.iocandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hydbest.iocandroid.annotation.ContentView;
import com.hydbest.iocandroid.annotation.OnClick;
import com.hydbest.iocandroid.annotation.ViewInject;
import com.hydbest.iocandroid.util.ViewInjectUtils;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity{

    @ViewInject(R.id.tv)
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);

        tv.setText("already inject activity !!");
    }

    @OnClick(R.id.btn)
    void click(View view){
        Log.i("csz",(view == null) +"   hahah ");
        Toast.makeText(this,"hello world !",Toast.LENGTH_SHORT).show();
    }
}
