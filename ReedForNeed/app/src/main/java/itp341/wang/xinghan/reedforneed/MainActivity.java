package itp341.wang.xinghan.reedforneed;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import layout.Fragment_MainActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //load main activity fragment
        FragmentManager fm = getSupportFragmentManager();
        Fragment mainFragment = fm.findFragmentById(R.id.mainActivityFragment);
        if(mainFragment == null) {
            mainFragment = new Fragment_MainActivity();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.mainActivityFragment, mainFragment);
            transaction.commit();
        }
    }
}
