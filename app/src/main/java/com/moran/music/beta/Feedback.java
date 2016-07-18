package com.moran.music.beta;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.moran.music.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Denis on 01/04/2016.
 */
public class Feedback extends Activity implements AdapterView.OnItemSelectedListener{
    Spinner spinner;
    TextView set;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        spinner = (Spinner) findViewById(R.id.spinner);

        set = (TextView) findViewById(R.id.tvset);
        spinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<String>();
        categories.add("Feedback");
        categories.add("Report a bug");


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);


        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinner.setAdapter(dataAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item  = parent.getItemAtPosition(position).toString();
        if (item.equals("Feedback")){
            set.setText("Send us a suggestion");
        }else {
            set.setText("Report a problem");
        }
    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
