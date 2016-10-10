package itp341.wang.xinghan.reedforneed;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import layout.Fragment_Language;
import layout.Fragment_Result;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //load Result activity fragment
        FragmentManager fm = getSupportFragmentManager();
        Fragment resultFragment = fm.findFragmentById(R.id.resultFragment);
        if(resultFragment == null) {
            resultFragment = new Fragment_Result();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.resultFragment, resultFragment);
            transaction.commit();
        }
    }
}
