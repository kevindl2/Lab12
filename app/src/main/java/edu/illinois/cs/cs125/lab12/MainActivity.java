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


import java.util.Arrays;

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
     * Last date at which Curiosity rover has picture.
     */
    private String maxDateCuriosity;

    /**
     * Last date at which Opportunity rover has picture.
     */
    private String maxDateOpportunity;

    /**
     * List of available cameras on Curiosity rover.
     */

    private String[] camerasCuriosity;

    /**
     * List of available cameras on Opportunity rover.
     */
    private String[] camerasOpportunity;

    /**
     * Index of chosen photo in list.
     */
    private int photoIndex;

    /**
     * List of
     */
    private String[] photoIndices;

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
        getMaxDateCuriosity();
        getMaxDateOpportunity();

        Spinner dropdownRover = findViewById(R.id.rover);

        String[] itemsRovers = new String[] {"", "Curiosity", "Opportunity"};
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
        if (selectedItem.equals("")) {
            return;
        }
        if (selectedItem.equals("Curiosity") ||
                selectedItem.equals("Opportunity")) {
            selectedRover = selectedItem.toLowerCase();
            Spinner dropdownCamera = findViewById(R.id.cameraType);
            ArrayAdapter<String> adapterCamera;
            if (selectedItem.equals("Curiosity")) {
                adapterCamera = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, camerasCuriosity);
            } else {
                adapterCamera = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, camerasOpportunity);
            }
            dropdownCamera.setAdapter(adapterCamera);
            dropdownCamera.setOnItemSelectedListener(this);
            dropdownCamera.setVisibility(View.VISIBLE);

            /*
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
        */
        } else {
            if (selectedItem.equals("Front Hazard Avoidance Camera")) {
                selectedCamera = "fhaz";
                setPhotoIndexRange();
            } else if (selectedItem.equals("Rear Hazard Avoidance Camera")) {
                selectedCamera = "rhaz";
                setPhotoIndexRange();
            } else if (selectedItem.equals("Mast Camera")) {
                selectedCamera = "mast";
                setPhotoIndexRange();
            } else if (selectedItem.equals("Chemistry and Camera Complex")) {
                selectedCamera = "chemcam";
                setPhotoIndexRange();
            } else if (selectedItem.equals("Mars Hand Lens Imager")) {
                selectedCamera = "mahli";
                setPhotoIndexRange();
            } else if (selectedItem.equals("Mars Descent Imager")) {
                selectedCamera = "mardi";
                setPhotoIndexRange();
            } else if (selectedItem.equals("Navigation Camera")) {
                selectedCamera = "navcam";
                setPhotoIndexRange();
            } else if (selectedItem.equals("Panoramic Camera")) {
                selectedCamera = "pancam";
                setPhotoIndexRange();
            } else if (selectedItem.equals("Mini-TES")) {
                selectedCamera = "minites";
                setPhotoIndexRange();
            } else {
                photoIndex = Integer.parseInt(selectedItem);
                Button update = findViewById(R.id.get_image);
                update.setVisibility(View.VISIBLE);
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

    /**
     *
     */
    void getMaxDateCuriosity() {
        try {



            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "https://api.nasa.gov/mars-photos/api/v1/manifests/"
                            + "Curiosity"
                            + "?api_key="
                            + BuildConfig.API_KEY,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            try {
                                //Log.d(TAG, response.toString(2));
                                JSONObject root = response.getJSONObject("photo_manifest");
                                maxDateCuriosity  = root.getString("max_date");
                                Log.d(TAG, maxDateCuriosity);

                                JSONArray photos = root.getJSONArray("photos");
                                JSONObject lastElement = photos.getJSONObject(photos.length() - 1);
                                Log.d(TAG, lastElement.toString(2));
                                JSONArray listCameras = lastElement.getJSONArray("cameras");
                                camerasCuriosity = new String[listCameras.length()];
                                for (int i = 0; i < listCameras.length(); i++) {
                                    if (listCameras.getString(i).equals("FHAZ")){
                                        camerasCuriosity[i] = "Front Hazard Avoidance Camera";
                                    } else if (listCameras.getString(i).equals("RHAZ")) {
                                        camerasCuriosity[i] = "Rear Hazard Avoidance Camera";
                                    } else if (listCameras.getString(i).equals("MAST")) {
                                        camerasCuriosity[i] = "Mast Camera";
                                    } else if (listCameras.getString(i).equals("CHEMCAM")) {
                                        camerasCuriosity[i] = "Chemistry and Camera Complex";
                                    } else if (listCameras.getString(i).equals("MAHLI")) {
                                        camerasCuriosity[i] = "Mars Hand Lens Imager";
                                    } else if (listCameras.getString(i).equals("MARDI")) {
                                        camerasCuriosity[i] = "Mars Descent Imager";
                                    } else if (listCameras.getString(i).equals("NAVCAM")) {
                                        camerasCuriosity[i] = "Navigation Camera";
                                    } else if (listCameras.getString(i).equals("PANCAM")) {
                                        camerasCuriosity[i] = "Panoramic Camera";
                                    } else if (listCameras.getString(i).equals("MINITES")) {
                                        camerasCuriosity[i] = "Mini-TES";
                                    }

                                }
                                Log.d(TAG, "Curiosity's Cameras: " + Arrays.toString(camerasCuriosity));


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
    void getMaxDateOpportunity() {
        try {



            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "https://api.nasa.gov/mars-photos/api/v1/manifests/"
                            + "Opportunity"
                            + "?api_key="
                            + BuildConfig.API_KEY,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            try {
                                //Log.d(TAG, response.toString(2));
                                JSONObject root = response.getJSONObject("photo_manifest");
                                maxDateOpportunity  = root.getString("max_date");
                                Log.d(TAG, maxDateOpportunity);

                                JSONArray photos = root.getJSONArray("photos");
                                JSONObject lastElement = photos.getJSONObject(photos.length() - 1);
                                Log.d(TAG, lastElement.toString(2));
                                JSONArray listCameras = lastElement.getJSONArray("cameras");
                                camerasOpportunity = new String[listCameras.length()];
                                for (int i = 0; i < listCameras.length(); i++) {
                                    if (listCameras.getString(i).equals("FHAZ")){
                                        camerasOpportunity[i] = "Front Hazard Avoidance Camera";
                                    } else if (listCameras.getString(i).equals("RHAZ")) {
                                        camerasOpportunity[i] = "Rear Hazard Avoidance Camera";
                                    } else if (listCameras.getString(i).equals("MAST")) {
                                        camerasOpportunity[i] = "Mast Camera";
                                    } else if (listCameras.getString(i).equals("CHEMCAM")) {
                                        camerasOpportunity[i] = "Chemistry and Camera Complex";
                                    } else if (listCameras.getString(i).equals("MAHLI")) {
                                        camerasOpportunity[i] = "Mars Hand Lens Imager";
                                    } else if (listCameras.getString(i).equals("MARDI")) {
                                        camerasOpportunity[i] = "Mars Descent Imager";
                                    } else if (listCameras.getString(i).equals("NAVCAM")) {
                                        camerasOpportunity[i] = "Navigation Camera";
                                    } else if (listCameras.getString(i).equals("PANCAM")) {
                                        camerasOpportunity[i] = "Panoramic Camera";
                                    } else if (listCameras.getString(i).equals("MINITES")) {
                                        camerasOpportunity[i] = "Mini-TES";
                                    }

                                }
                                Log.d(TAG, "Opportunity's Cameras: " + Arrays.toString(camerasOpportunity));


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


            String roverName = selectedRover;

            String camera = selectedCamera;
            String maxDate;
            if (selectedRover.equals("curiosity")) {
                maxDate = maxDateCuriosity;
            } else {
                maxDate = maxDateOpportunity;
            }

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

                                JSONObject photo = root.getJSONObject(photoIndex - 1);
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
    void setPhotoIndexRange() {
        try {


            String roverName = selectedRover;

            String camera = selectedCamera;
            String maxDate;
            if (selectedRover.equals("curiosity")) {
                maxDate = maxDateCuriosity;
            } else {
                maxDate = maxDateOpportunity;
            }

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
                                Spinner choosePhoto = findViewById(R.id.photoNumber);
                                photoIndices = new String[root.length()];
                                for (int i = 0; i < root.length(); i++) {
                                    Integer index = (Integer) (i + 1);
                                    photoIndices[i] = index.toString();
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, photoIndices);
                                choosePhoto.setAdapter(adapter);
                                choosePhoto.setOnItemSelectedListener(MainActivity.this);
                                choosePhoto.setVisibility(View.VISIBLE);

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
