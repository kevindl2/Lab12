package edu.illinois.cs.cs125.lab12;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;



public class RoverSpinner implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "Lab12:Main";
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Log.d(TAG, "Item selected");
        String selectedItem = (String) parent.getItemAtPosition(pos);
    }
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        Log.d(TAG, "Nothing Selected");
    }
}
