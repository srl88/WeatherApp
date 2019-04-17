package com.example.wheatherforecast;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.content.Context;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    /**
     * Error variables.
     */
    private  final int NUM_ERROR = -9999999;
    private final String STR_ERROR = "N/A";

    /**
     * Ui elements
     */
    RadioButton met;
    RadioButton far;
    RadioGroup units;
    TextView cityName;
    TextView currentTemp;
    TextView maxTemp;
    TextView minTemp;
    TextView type;
    TextView description;
    TextView humidity;
    TextView clouds;
    TextView error;
    AutoCompleteTextView cityFind;
    Button btn;
    Runnable runnable;

    /**
     * Object to hold the current data
     */
    WheatherItem currentData;

    /**
     * OnCreate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * Initialize UI elemnts
         */
        cityName = findViewById(R.id.cityName);
        currentTemp = findViewById(R.id.temp);
        maxTemp = findViewById(R.id.maxTemp);
        minTemp = findViewById(R.id.minTemp);
        type= findViewById(R.id.type);
        description =  findViewById(R.id.descp);
        humidity = findViewById(R.id.hum);
        clouds = findViewById(R.id.clouds);
        error = findViewById(R.id.errorText);
        cityFind = findViewById(R.id.findCity);
        btn = findViewById(R.id.btnFind);
        met = findViewById(R.id.meters);
        far = findViewById(R.id.farem);
        units = findViewById(R.id.radioGroup);

        /**
         * Initialize the current data holder
         */
        currentData = new WheatherItem();
        /**
         * Text Editor options = event listener and autofill.
         */
        //SET THE INITIAL UI LOOK
        hideUIelements();
        hideError();

        //SET THE AUTO COMPLETE FOR THE EDIT TEXT
        String[] autofill = getResources().getStringArray(R.array.autofill);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, autofill);
        cityFind.setAdapter(adapter);

        //CLICK LISTENER FOR THE BTN
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hide input key board in case
                InputMethodManager inMan = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inMan.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                /**
                 * INPUT CHECK!
                 */
                String inputString = cityFind.getText().toString().trim();
                //make sure the city is not empty
                if(inputString.isEmpty()){
                    hideUIelements();
                    errorMessage("Please make sure you entered a city");
                    return;
                }

                //make sure no numbers in the city name
                if(stringFormat(inputString)){
                    hideUIelements();
                    errorMessage("A city name should not have numbers");
                    return;
                }

                String [] tempArr = inputString.split(",");
                //Make sure the input is correct=> city,Country OR city
                if(tempArr.length>2||tempArr.length==0){
                    hideUIelements();
                    errorMessage(" Please make sure your input is correct: \n city,country or city\n eg, Halifax,CA or Halifax");
                    return;
                }
                //there is a country!
                else if(tempArr.length==2){
                    //get rid of spaces just in case
                    String tempCountry = tempArr[1].replaceAll("\\s", "");
                    inputString = tempArr[0].trim()+","+tempCountry;
                }

                // if Input is ok, call the API
                final String cityToFind = inputString;
                final boolean isMetric = met.isChecked();
                //create runnable object to call the API method in the background
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        getWheather(cityToFind, isMetric);
                    }
                };

                //create thread with the runnable object and start it
                Thread th = new Thread(null, runnable, "background");
                th.start();
               loadingRequest(true);
            }
        });

        //click listeners for radio
        units.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //before we modify any data we check if the data is visible.
                if(currentTemp.getVisibility()==View.VISIBLE){
                    if(checkedId==R.id.meters){
                        modifyUnitsFromFtoC();
                    }else if(checkedId==R.id.farem){
                        modifyUnitsFromCtoF();
                    }
                }
            }
        });

    }

    /**
     * BELOW ARE THE REQUEST METHODS
     */

    /**
     * Method to make a API call and populate the UI. Takes the city name as parameter.
     * @param cityToFind
     */
    private void getWheather(String cityToFind, Boolean metric){
        //build the URL
        String tokeURL = "&units=metric&appid=71af0255d2de84d73f0d88f0ad1dccc7";
        if(!metric){
            tokeURL = "&units=imperial&appid=71af0255d2de84d73f0d88f0ad1dccc7";
        }
        String mainUrl = "http://api.openweathermap.org/data/2.5/weather?q=";

        final String baseURL = mainUrl+cityToFind+tokeURL;

        //make the call
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                baseURL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        /**
                         * Create the object and populate from the response
                         */

                        currentData.set_name(response.optString("name", STR_ERROR));

                        // JSON objects and arrays from response
                        JSONObject temp;
                        try{
                            temp = response.getJSONObject("main");
                        }catch (JSONException e){
                            temp = response;
                        }

                        currentData.set_temp(temp.optDouble("temp", NUM_ERROR));
                        currentData.set_minTemp(temp.optDouble("temp_min", NUM_ERROR));
                        currentData.set_maxTemo(temp.optDouble("temp_max", NUM_ERROR));
                        currentData.set_humidity(temp.optInt("humidity", NUM_ERROR));

                        //Clouds object from the response.
                        try{
                            temp = response.getJSONObject("clouds");
                        }catch (JSONException e){
                            temp = response;
                        }

                        currentData.set_clouds(temp.optInt("all", NUM_ERROR));

                        // wheather object from response
                        try{
                            temp =  response.getJSONArray("weather").getJSONObject(0);
                        }catch(JSONException e){
                            temp = response;
                        }

                        currentData.set_type(temp.optString("main", STR_ERROR));
                        currentData.set_description(temp.optString("description", STR_ERROR));;

                        //update UI
                       loadingRequest(false);
                       setUIelements(currentData);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String er = "Oops, something went wrong...";
                        if(error instanceof TimeoutError){
                            er = "The connection took to long, please try again";
                        }else if(error instanceof NoConnectionError){
                            er = "Your device is not currently connected. Please connect it";
                        }
                        else if(error instanceof ServerError) {
                            er = "We could not find a city by the name you give us.";
                        }
                        //update UI
                        loadingRequest(false);
                        hideUIelements();
                        errorMessage(er);
                    }
                }
        );

        //CALL IT!
        try{
            RequestSingletonPattern.getIstance(getApplicationContext()).addToRequestQueue(req);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * BELOW THE UNIT CHANGE METHOS USING THE UTILITY CLASS
     */

    /**
     * Method to display the temperature from C to F.
     */
    private void modifyUnitsFromCtoF(){
        //First make sure there is data to displayed, if so update the data structure and then modify it.
        if(!STR_ERROR.equals(maxTemp.getText().toString())){
            currentData.set_maxTemo(Utility.fromCelsiousToFahrenheit(currentData.get_maxTemo()));
            maxTemp.setText("Max: "+(int)currentData.get_maxTemo()+"°F");
        }
        if(!STR_ERROR.equals(minTemp.getText().toString())){
            currentData.set_minTemp(Utility.fromCelsiousToFahrenheit(currentData.get_minTemp()));
            minTemp.setText("Min: "+(int)currentData.get_minTemp()+"°F");
        }
        if(!STR_ERROR.equals(currentTemp.getText().toString())){
            currentData.set_temp(Utility.fromCelsiousToFahrenheit(currentData.get_temp()));
            currentTemp.setText((int)currentData.get_temp()+"°F");
        }
    }

    /**
     * Method to display the temperature fro F to C.
     */
    private void modifyUnitsFromFtoC(){
        //If the data is available, modify the data structure and update the UI from it.
        if(!STR_ERROR.equals(maxTemp.getText().toString())){
            currentData.set_maxTemo(Utility.fromFahrenheitToCelsious(currentData.get_maxTemo()));
            maxTemp.setText("Max: "+(int)currentData.get_maxTemo()+"°C");
        }
        if(!STR_ERROR.equals(minTemp.getText().toString())){
            currentData.set_minTemp(Utility.fromFahrenheitToCelsious(currentData.get_minTemp()));
            minTemp.setText("Min: "+(int)currentData.get_minTemp()+"°C");
        }
        if(!STR_ERROR.equals(currentTemp.getText().toString())){
            currentData.set_temp(Utility.fromFahrenheitToCelsious(currentData.get_temp()));
            currentTemp.setText((int)currentData.get_temp()+"°C");
        }
    }


    /**
     * BELOW ALL UI METHODS AND ERROR HANDELING METHODS
     */

    /**
     * Method to check if a string is properly format. In this case, if it contains numbers
      * @param str
     * @return true if the string contains forbidden character, faltse otherwise
     */
    private Boolean stringFormat(String str){
        return Pattern.compile("[0-9]").matcher(str).find();
    }

    /**
     *  Method to display the API response store into the WheatherItem object
     * @param W
     */
    private void setUIelements(WheatherItem W){
        //set information into their corresponding UI element
        cityName.setText(W.get_name());
        type.setText(W.get_type());
        description.setText(W.get_description());

        String degrees = "°C";
        if(!met.isChecked()){
            degrees = "°F";
        }

        String temp = W.get_temp() == NUM_ERROR ? STR_ERROR : (int)W.get_temp()+degrees ;
        currentTemp.setText(temp);
        temp = W.get_maxTemo() == NUM_ERROR ? STR_ERROR : "Max: "+(int)W.get_maxTemo()+degrees;
        maxTemp.setText(temp);
        temp = W.get_minTemp() == NUM_ERROR ? STR_ERROR : "Min: "+(int)W.get_minTemp()+degrees ;
        minTemp.setText(temp);
        temp = W.get_humidity() == NUM_ERROR ? STR_ERROR : "Humidity: "+W.get_humidity()+"%" ;
        humidity.setText(temp);
        temp = W.get_clouds() == NUM_ERROR ? STR_ERROR : "Clouds: "+W.get_clouds()+"%" ;
        clouds.setText(temp);

        //display the final look
        hideError();
        showUIelements();
    }

    /**
     * Method to hide UI elemnents that do not required to be displayed until match has been found
     */
    private void hideUIelements(){
        cityName.setVisibility(View.INVISIBLE);
        currentTemp.setVisibility(View.INVISIBLE);
        maxTemp.setVisibility(View.INVISIBLE);
        minTemp.setVisibility(View.INVISIBLE);
        type.setVisibility(View.INVISIBLE);
        description.setVisibility(View.INVISIBLE);
        humidity.setVisibility(View.INVISIBLE);
        clouds.setVisibility(View.INVISIBLE);
    }

    /**
     * Method to show UI elements that required to be displayed after match has been found
     */
    private void showUIelements(){
        cityName.setVisibility(View.VISIBLE);
        currentTemp.setVisibility(View.VISIBLE);
        maxTemp.setVisibility(View.VISIBLE);
        minTemp.setVisibility(View.VISIBLE);
        type.setVisibility(View.VISIBLE);
        description.setVisibility(View.VISIBLE);
        humidity.setVisibility(View.VISIBLE);
        clouds.setVisibility(View.VISIBLE);
    }

    /**
     * Method to show an error message
     * @param err
     */
    private void errorMessage(String err){
        //set the text and show it
        error.setTextColor(Color.RED);
        error.setText(err);
        error.setVisibility(View.VISIBLE);
    }

    /**
     * Method to hide the error message textview
     */
    private void hideError(){
        error.setVisibility(View.INVISIBLE);
    }

    /**
     * Method to allow the user know that the call is in progress and prevent any further calls.
     * @param request
     */
    private void loadingRequest(Boolean request){
        if(request){
                hideUIelements();
                btn.setClickable(false);
                String str = "Loading the data...";
                error.setText(str);
                error.setTextColor(Color.BLACK);
                error.setVisibility(View.VISIBLE);
        }else{
            btn.setClickable(true);
            hideError();
        }
    }
}
