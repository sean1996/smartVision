package layout;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import itp341.wang.xinghan.reedforneed.Model.Record;
import itp341.wang.xinghan.reedforneed.Model.customAdapter;
import itp341.wang.xinghan.reedforneed.ObjectSerializer;
import itp341.wang.xinghan.reedforneed.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_History extends Fragment {

    private ArrayList<Record> history;
    private ListView historyListView;

    public static final String HISTORY_ARRAYLIST_KEY = "itp341.wang.xinghan.reedforneed.HISTORY_ARRAYLIST_KEY";
    private static final String PREF_FILENAME = "itp341.wang.xinghan.reedforneed";

    private TextToSpeech textToSpeech;

    public Fragment_History() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        historyListView = (ListView)v.findViewById(R.id.historyListView);

        //load arrayList and desrialize it from shared Preference
        SharedPreferences prefs = getActivity().getSharedPreferences(PREF_FILENAME, Activity.MODE_PRIVATE);
        try {
            history = (ArrayList<Record>) ObjectSerializer.deserialize(prefs.getString(HISTORY_ARRAYLIST_KEY, ObjectSerializer.serialize(new ArrayList<Record>())));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //create custom listviewAdapter
        customAdapter adapter = new customAdapter(getActivity(), history);
        historyListView.setAdapter(adapter);

        //initialize text to speech
        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH); //intialize language
                    textToSpeech.setSpeechRate(0.75f);
                    textToSpeech.speak("You're at History record screen, click back to go to main screen", TextToSpeech.QUEUE_FLUSH, null); //speak
                }
            }
        });


        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(textToSpeech!=null){
            textToSpeech.shutdown();
        }
    }
}
