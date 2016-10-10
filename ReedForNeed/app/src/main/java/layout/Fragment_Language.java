package layout;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Locale;

import itp341.wang.xinghan.reedforneed.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Language extends Fragment {

    private static final String PREF_FILENAME = "itp341.wang.xinghan.reedforneed";
    private static final String PREF_KEY_DESTINATION_LANGUAGE = "itp341.wang.xinghan.reedforneed.destinationlanguage";
    private TextToSpeech textToSpeech;

    private Spinner spinner;
    private SharedPreferences prefs;

    public Fragment_Language() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fragment_language, container, false);

        //initialize text to speech
        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH); //intialize language
                    textToSpeech.setSpeechRate(0.75f);
                    textToSpeech.speak("Please choose your language", TextToSpeech.QUEUE_FLUSH, null); //speak
                }
            }
        });
        spinner = (Spinner)v.findViewById(R.id.languageSpinner);

        prefs = getActivity().getSharedPreferences(
                PREF_FILENAME, Activity.MODE_PRIVATE
        );

        //set spinner selected index from System Preference
        String destination = prefs.getString(PREF_KEY_DESTINATION_LANGUAGE,"ENGLISH");
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(destination)){
                index = i;
                break;
            }
        }
        spinner.setSelection(index);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String destination = spinner.getSelectedItem().toString();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(PREF_KEY_DESTINATION_LANGUAGE, destination);
                textToSpeech.speak("You have changed the destination language to: " + destination, TextToSpeech.QUEUE_FLUSH, null); //speak
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(textToSpeech!= null){
            textToSpeech.shutdown();
        }
    }
}
