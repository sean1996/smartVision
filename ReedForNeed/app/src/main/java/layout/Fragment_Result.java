package layout;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import itp341.wang.xinghan.reedforneed.Model.Record;
import itp341.wang.xinghan.reedforneed.ObjectSerializer;
import itp341.wang.xinghan.reedforneed.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Result extends Fragment {

    public static final String SOURCE_LOCALE_KEY = "itp341.wang.xinghan.reedforneed.Slocalekey";
    public static final String DESTINATION_LOCALE_KEY  = "itp341.wang.xinghan.reedforneed.Dlocalekey";
    public static final String TRANSLATED_TEXT_KEY = "itp341.wang.xinghan.reedforneed.TRANSLATED_TEXT_KEY";
    public static final String HISTORY_ARRAYLIST_KEY = "itp341.wang.xinghan.reedforneed.HISTORY_ARRAYLIST_KEY";
    private static final String PREF_FILENAME = "itp341.wang.xinghan.reedforneed";

    private TextView resultTV;
    private Button restartButton;

    private TextToSpeech textToSpeech;
    private String destination_locale;
    private String translated_text;

    public Fragment_Result() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_fragment__result, container, false);

        //get extra
        Intent i = getActivity().getIntent();
        translated_text = i.getStringExtra(TRANSLATED_TEXT_KEY);
        String source_locale = i.getStringExtra(SOURCE_LOCALE_KEY);
        destination_locale = i.getStringExtra(DESTINATION_LOCALE_KEY);

        //populate the view
        resultTV = (TextView)v.findViewById(R.id.resultTextView);
        resultTV.setText(translated_text);

        restartButton = (Button)v.findViewById(R.id.readAgainButton);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        //initialize text to speech
        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(new Locale(destination_locale)); //intialize language
                    textToSpeech.setSpeechRate(0.75f);
                    textToSpeech.speak(translated_text, TextToSpeech.QUEUE_FLUSH, null); //speak
                }
            }
        });



        //create a record in history
        Record record = new Record();
        record.setDestinationLanguage(destination_locale);
        record.setOriginLanguage(source_locale);
        record.setContentDestinationLanguage(translated_text);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        record.setCreationDate(date);


        //load and desrialize history arraylist from Shared pref
        ArrayList<Record> history = new ArrayList<Record>();
        SharedPreferences prefs = getActivity().getSharedPreferences(PREF_FILENAME, Activity.MODE_PRIVATE);
        try {
            history = (ArrayList<Record>) ObjectSerializer.deserialize(prefs.getString(HISTORY_ARRAYLIST_KEY, ObjectSerializer.serialize(new ArrayList<Record>())));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        history.add(record);


        //serilize history arraylist and put back into Shared Pref
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString(HISTORY_ARRAYLIST_KEY, ObjectSerializer.serialize(history));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();



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
