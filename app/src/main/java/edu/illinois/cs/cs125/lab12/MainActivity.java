package edu.illinois.cs.cs125.lab12;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

/**
 * Main class for our UI design lab.
 */
public final class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "Lab12:Main";

    /** Request queue for our API requests. */
    private static RequestQueue requestQueue;

    /**
     * Rover whose camera we are viewing.
     */
    private String selectedRover;
    /**
     * Camera selected to view.
     */
    private String selectedCamera;

    /**
     * Last date at which rover has picture.
     */
    private String maxDate;
    /**
     * Last sol at which rover has picture.
     */
    private String maxSol;

    /**
     * Run when this activity comes to the foreground.
     *
     * @param savedInstanceState unused
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the queue for our API requests
        requestQueue = Volley.newRequestQueue(this);

        setContentView(R.layout.activity_main);

        Spinner dropdownRover = findViewById(R.id.rover);

        String[] itemsRovers = new String[] {"Curiosity", "Opportunity", "Spirit"};
        ArrayAdapter<String> adapterRover = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsRovers);
        dropdownRover.setAdapter(adapterRover);

        dropdownRover.setOnItemSelectedListener(this);



        final Button getImage = findViewById(R.id.get_image);
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Start API button clicked");

                startAPICall();
            }
        });
    }
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Log.d(TAG, "Item selected");
        String selectedItem = (String) parent.getItemAtPosition(pos);
        if (selectedItem.equals("Curiosity") ||
                selectedItem.equals("Opportunity") ||
                selectedItem.equals("Spirit")) {
            selectedRover = selectedItem.toLowerCase();
            getMaxDate();

            if (selectedItem.equals("Curiosity")) {
                Spinner dropdownCamera = findViewById(R.id.cameraType);
//create a list of items for the spinner.
                String[] itemsCamera = new String[]{"Front Hazard Avoidance Camera", "Rear Hazard Avoidance Camera", "Mast Camera",
                        "Chemistry and Camera Complex", "Mars Hand Lens Imager", "Mars Descent Imager",
                        "Navigation Camera"};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
                ArrayAdapter<String> adapterCamera = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsCamera);

//set the spinners adapter to the previously created one.
                dropdownCamera.setAdapter(adapterCamera);
                dropdownCamera.setOnItemSelectedListener(this);
                dropdownCamera.setVisibility(View.VISIBLE);
            } else if (selectedItem.equals("Opportunity") || selectedItem.equals("Spirit")) {
                Spinner dropdownCamera = findViewById(R.id.cameraType);
//create a list of items for the spinner.
                String[] itemsCamera = new String[]{"Front Hazard Avoidance Camera", "Rear Hazard Avoidance Camera",
                        "Navigation Camera", "Panoramic Camera", "Mini-TES"};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
                ArrayAdapter<String> adapterCamera = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsCamera);

//set the spinners adapter to the previously created one.
                dropdownCamera.setAdapter(adapterCamera);
                dropdownCamera.setOnItemSelectedListener(this);
                dropdownCamera.setVisibility(View.VISIBLE);
            }
        } else {
            if (selectedItem.equals("Front Hazard Avoidance Camera")) {
                selectedCamera = "fhaz";
            } else if (selectedItem.equals("Rear Hazard Avoidance Camera")) {
                selectedCamera = "rhaz";
            } else if (selectedItem.equals("Mast Camera")) {
                selectedCamera = "mast";
            } else if (selectedItem.equals("Chemistry and Camera Complex")) {
                selectedCamera = "chemcam";
            } else if (selectedItem.equals("Mars Hand Lens Imager")) {
                selectedCamera = "mahli";
            } else if (selectedItem.equals("Mars Descent Imager")) {
                selectedCamera = "mardi";
            } else if (selectedItem.equals("Navigation Camera")) {
                selectedCamera = "navcam";
            } else if (selectedItem.equals("Panoramic Camera")) {
                selectedCamera = "pancam";
            } else if (selectedItem.equals("Mini-TES")) {
                selectedCamera = "minites";
            }
        }
    }
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        Log.d(TAG, "Nothing Selected");
    }
    /**
     * Run when this activity is no longer visible.
     */
    @Override
    protected void onPause() {
        super.onPause();
    }
    void getMaxDate() {
        try {

            String roverName;
            if (selectedRover.equals("curiosity")) {
                roverName = "Curiosity";
            } else if (selectedRover.equals("opportunity")) {
                roverName = "Opportunity";
            } else {
                roverName = "Spirit";
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "https://api.nasa.gov/mars-photos/api/v1/manifests/"
                            + roverName
                            + "?api_key="
                            + BuildConfig.API_KEY,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            try {
                                //Log.d(TAG, response.toString(2));
                                JSONObject root = response.getJSONObject("photo_manifest");
                                maxDate  = root.getString("max_date");
                                Log.d(TAG, maxDate);
                                maxSol = root.getString("max_sol");
                                Log.d(TAG, maxSol);
                                JSONArray photos = root.getJSONArray("photos");
                                JSONObject lastElement = photos.getJSONObject(photos.length() - 1);
                                Log.d(TAG, lastElement.toString(2));
                                JSONArray listCameras = lastElement.getJSONArray("cameras");
                                String[] spinnerCameras = new String[listCameras.length()];
                                //for (int i = 0; i < listCameras.length(); i++) {
                                    //spinnerCameras[i] = J
                                //}
                            } catch (JSONException ignored) { }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Log.e(TAG, error.toString());
                }
            });


            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Make a call to NASA API to get image.
     */
    void startAPICall() {
        try {
            int month = Calendar.MONTH;
            int day = Calendar.DAY_OF_MONTH;

            String roverName = selectedRover;
            String date = "2015-6-3";
            String camera = selectedCamera;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "https://api.nasa.gov/mars-photos/api/v1/rovers/"
                            + roverName
                            + "/photos?earth_date="
                            + maxDate + "&camera="
                            + camera + "&api_key="
                            + BuildConfig.API_KEY,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            try {

                                //Log.d(TAG, response.toString(2));

                                JSONArray root = response.getJSONArray("photos");
                                if (root.length() == 0) {
                                    Log.d(TAG, "No Photos Available");
                                    Toast.makeText(MainActivity.this, "No Photos Available. Choose Different Rover or Camera.", Toast.LENGTH_LONG).show();
                                }
                                JSONObject photo = root.getJSONObject(0);
                                String url = photo.getString("img_src");
                                Log.d(TAG, url);
                                ImageView iv = findViewById(R.id.imageView);
                                new DownLoadImageTask(iv).execute(url);
                                iv.setVisibility(View.VISIBLE);
                            } catch (JSONException ignored) { }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(final VolleyError error) {
                            Log.e(TAG, error.toString());
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     /*
        AsyncTask enables proper and easy use of the UI thread. This class
        allows to perform background operations and publish results on the UI
        thread without having to manipulate threads and/or handlers.
     */

    /*
        final AsyncTask<Params, Progress, Result>
            execute(Params... params)
                Executes the task with the specified parameters.
     */
    private static class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        private ImageView imageView;

        private DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }
}
