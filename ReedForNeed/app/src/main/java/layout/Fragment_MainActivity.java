package layout;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import itp341.wang.xinghan.reedforneed.HistoryActivity;
import itp341.wang.xinghan.reedforneed.LanguageActivity;
import itp341.wang.xinghan.reedforneed.MainActivity;
import itp341.wang.xinghan.reedforneed.PermissionUtils;
import itp341.wang.xinghan.reedforneed.R;
import itp341.wang.xinghan.reedforneed.ResultActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_MainActivity extends Fragment {
    //API constants
    private static final String CLOUD_VISION_API_KEY = "AIzaSyC1sMTL66etZSF6AFCcmTZH4MVvYbPbTJE";
    public static final int CAMERA_IMAGE_REQUEST = 3;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String FILE_NAME = "temp.jpg";

    //Shared Preference constants
    private static final String PREF_FILENAME = "itp341.wang.xinghan.reedforneed";
    private static final String PREF_KEY_DESTINATION_LANGUAGE = "itp341.wang.xinghan.reedforneed.destinationlanguage";
    private SharedPreferences prefs;

    private Button historyButton, startButton, settingButton;

    private Uri filePath;

    private String sourceLocale;
    private String sourceDescription;
    private String destinationLocale;
    private static final String TRANSLATE_URL_HEADER = "https://www.googleapis.com/language/translate/v2?";
    private TextToSpeech textToSpeech;

    public Fragment_MainActivity() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_fragment__main, container, false);
        historyButton = (Button)v.findViewById(R.id.historyButton);
        startButton = (Button)v.findViewById(R.id.startReadingButton);
        settingButton = (Button)v.findViewById(R.id.settingButton);

        //set on click listener,launch new intent
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),HistoryActivity.class);
                startActivity(i);
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start camera
                startCamera();
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LanguageActivity.class);
                startActivity(i);
            }
        });

        //initialize text to speech
        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH); //intialize language
                    textToSpeech.setSpeechRate(0.75f);
                    textToSpeech.speak("You're at main screen. Click upper half of " +
                            "the screen to take photos.", TextToSpeech.QUEUE_FLUSH, null); //speak
                }
            }
        });

        return v;
    }
    

    @Override
    public void onDestroy() {
        if(textToSpeech!=null){
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            //make a call to google vision
            uploadImage(Uri.fromFile(getCameraFile()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.permissionGranted(
                requestCode,
                CAMERA_PERMISSIONS_REQUEST,
                grantResults)) {
            startCamera();
        }
    }

    //get language from shared preference
    private String getDestinationLanguage(){
        prefs = getActivity().getSharedPreferences(PREF_FILENAME, Activity.MODE_PRIVATE);
        String destination = prefs.getString(PREF_KEY_DESTINATION_LANGUAGE,"ENGLISH");
        String destinationLocale = "";
        if(destination.equalsIgnoreCase("ENGLISH")){
            destinationLocale = "en";
        }else if(destination.equalsIgnoreCase("Korean")){
            destinationLocale = "ko";
        }else if(destination.equalsIgnoreCase("Mandarin")){
            destinationLocale = "zh";
        }else if(destination.equalsIgnoreCase("Japanese")){
            destinationLocale = "ja";
        }else if(destination.equalsIgnoreCase("French")){
            destinationLocale = "fr";
        }else if(destination.equalsIgnoreCase("Italian")){
            destinationLocale = "it";
        }else if(destination.equalsIgnoreCase("Germany")){
            destinationLocale = "de";
        }
        return  destinationLocale;
    }

    //camera helper
    public void startCamera() {
        if (PermissionUtils.requestPermission(
                getActivity(),
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }



    //helper function to resize bitmap image
    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        sourceDescription = response.getResponses().get(0).getTextAnnotations().get(0).getDescription();

        return sourceDescription;
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to 800px to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri),
                                1200);

                callCloudVision(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
        }
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {

        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(new
                            VisionRequestInitializer(CLOUD_VISION_API_KEY));
                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("TEXT_DETECTION");
                            labelDetection.setMaxResults(10);
                            add(labelDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }


            protected void onPostExecute(String result) {

                //when the call to vision api is finished, launch another asynctask to translate content into destination language
                // Instantiate the RequestQueue.

                RequestQueue queue = Volley.newRequestQueue(getActivity());

                //format destination conetnt with regular express
                Log.d("swag","sourceDescription: " + sourceDescription);
                String formattedCotent_newline = sourceDescription.replaceAll("\n", " ");
                String formattedCotent_space = formattedCotent_newline.replaceAll(" ", "%20");

                //format url for Google Translate api calls
                destinationLocale = getDestinationLanguage();
                Log.d("destinationLocale", destinationLocale);
                String request = "https://www.googleapis.com/language/translate/v2?key=AIzaSyC1sMTL66etZSF6AFCcmTZH4MVvYbPbTJE&" + "target=" + destinationLocale  + "&q=" + formattedCotent_space;
                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, request,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("swag","Response is: " + response.toString());
                                //parse the reposnse into JSON object and get tranlsted content with keys
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if(jsonObject.has("data")){
                                        JSONObject data = jsonObject.getJSONObject("data");
                                        if(data.has("translations")){
                                            JSONArray jsonArray = data.getJSONArray("translations");
                                            JSONObject firstElement = jsonArray.getJSONObject(0);
                                            if(firstElement.has("translatedText") && firstElement.has("detectedSourceLanguage")){
                                                String translatedText = firstElement.getString("translatedText");
                                                sourceLocale = firstElement.getString("detectedSourceLanguage");
                                                Log.d("swag","niqqqqaaaa: " + translatedText);
                                                //launch an intent to new activity
                                                Intent i = new Intent(getActivity(), ResultActivity.class);
                                                i.putExtra(Fragment_Result.SOURCE_LOCALE_KEY, sourceLocale);
                                                i.putExtra(Fragment_Result.TRANSLATED_TEXT_KEY, translatedText);
                                                i.putExtra(Fragment_Result.DESTINATION_LOCALE_KEY, destinationLocale);
                                                startActivity(i);
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("swag","That didn't work!");
                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        }.execute();
    }



}
