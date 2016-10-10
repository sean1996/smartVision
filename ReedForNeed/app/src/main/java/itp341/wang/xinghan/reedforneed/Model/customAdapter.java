package itp341.wang.xinghan.reedforneed.Model;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import itp341.wang.xinghan.reedforneed.R;

/**
 * Created by Xinghan on 5/6/16.
 */
public class customAdapter extends ArrayAdapter<Record> {
    private static String[] colors = {"#BBE9DB","#AECCC6", "#9BA6A5", "#757A79", "#A9EEE6", "#F38181", "#625772", "#83E4B5"};
    private static int counter = 0;


    public customAdapter(Context context, ArrayList<Record> mRecords){
        super(context,0,mRecords);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Record record = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.customlistcell,parent,false);
        }
        TextView description = (TextView)convertView.findViewById(R.id.cellDescription);
        TextView fromTo = (TextView)convertView.findViewById(R.id.cellFromTo);
        TextView dateTime = (TextView)convertView.findViewById(R.id.cellDate);
        description.setText(record.getContentDestinationLanguage());
        String fromToString = " " + record.getOriginLanguage() + " -> " + record.getDestinationLanguage();
        fromTo.setText(fromToString);
        dateTime.setText(record.getCreationDate().toString());

        //randomize a color for each cell
        Random random = new Random();
        counter++;
        if(counter == 7){
            counter = 0;
        }
        convertView.setBackgroundColor(Color.parseColor(colors[counter]));

        return convertView;
    }
}
