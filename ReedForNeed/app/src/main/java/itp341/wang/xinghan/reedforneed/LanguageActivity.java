package itp341.wang.xinghan.reedforneed;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import layout.Fragment_Language;
import layout.Fragment_MainActivity;

public class LanguageActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        //load Language activity fragment
        FragmentManager fm = getSupportFragmentManager();
        Fragment languageFragment = fm.findFragmentById(R.id.languageFragment);
        if(languageFragment == null) {
            languageFragment = new Fragment_Language();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.languageFragment, languageFragment);
            transaction.commit();
        }


    }
}
