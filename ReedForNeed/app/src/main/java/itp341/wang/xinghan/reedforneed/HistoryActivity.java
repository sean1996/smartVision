package itp341.wang.xinghan.reedforneed;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import layout.Fragment_History;
import layout.Fragment_Language;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //load History activity fragment
        FragmentManager fm = getSupportFragmentManager();
        Fragment historyFragment = fm.findFragmentById(R.id.historyFragment);
        if(historyFragment == null) {
            historyFragment = new Fragment_History();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.historyFragment, historyFragment);
            transaction.commit();
        }
    }
}
