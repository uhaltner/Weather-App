package com.example.weatherapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;

import static android.widget.Toast.LENGTH_SHORT;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText cityInput;
    private ImageButton cityButton;
    private TextView cityNameText;
    private TextView currentTempText;
    private TextView maxTempText;
    private TextView minTempText;
    private TextView mainWeatherText;
    private TextView descriptionText;
    private TextView humidityText;
    private TextView cloudsText;
    private ImageView imageView;
    private LinearLayout screen;
    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityButton = findViewById(R.id.buttonCity);
        cityInput = findViewById(R.id.lblCityEdit);
        cityNameText = findViewById(R.id.textCityName);
        currentTempText = findViewById(R.id.textTemperature);
        maxTempText = findViewById(R.id.textMaxTemp);
        minTempText = findViewById(R.id.textMinTemp);
        mainWeatherText = findViewById(R.id.textMainWeather);
        descriptionText= findViewById(R.id.textDescription);
        humidityText = findViewById(R.id.textHumidityValue);
        cloudsText = findViewById(R.id.textCloudsValue);
        imageView = findViewById(R.id.weatherIconImage);
        screen = findViewById(R.id.screenBackground);

         cityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String city = cityInput.getText().toString().toLowerCase();


                if(city.isEmpty()) {

                    Toast.makeText(getApplicationContext(), "Can not find city",Toast.LENGTH_SHORT).show();

                }else{

                    String country = "ca";
                    String q = city + "," + country;

                    final String url = "http://api.openweathermap.org/data/2.5/weather?q="+q+"&APPID=" + getString(R.string.weather_api_key);

                    Log.d(TAG, "onClick: URL is " + url);


                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            getWeather(url);
                        }
                    };

                    //retrieve data on separate thread
                    Thread thread = new Thread(null, runnable, "background");
                    thread.start();

                    //create a copy of the input/keyboard
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    //hide the keyboard when pressing the submit button
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }
        });


    }


    public void getWeather(String url){

        Log.d(TAG, "getWeather: called");
        
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {
                //handle response

                Log.e(TAG, "onResponse: JSON " + response.toString());

                try {                  


                    String city = response.getString("name");
                    cityNameText.setText(city);


                    JSONObject main = response.getJSONObject("main");

                    double currentTemp = main.getDouble("temp");
                    double minTemp = main.getDouble("temp_min");
                    double maxTemp = main.getDouble("temp_max");
                    int humidity = main.getInt("humidity");

                    currentTempText.setText(Integer.toString(convertKelvinToCelsius(currentTemp))+"°C");
                    minTempText.setText(Integer.toString(convertKelvinToCelsius(minTemp))+"°C");
                    maxTempText.setText(Integer.toString(convertKelvinToCelsius(maxTemp))+"°C");
                    humidityText.setText(humidity + "%");

                    JSONObject clouds = response.getJSONObject("clouds");

                    int cloudRate = clouds.getInt("all");
                    cloudsText.setText(cloudRate + "%");

                    JSONArray weather = response.getJSONArray("weather");

                    String mainDesc = weather.getJSONObject(0).getString("main");
                    String description = weather.getJSONObject(0).getString("description");
                    int id = weather.getJSONObject(0).getInt("id");
                    String icon = weather.getJSONObject(0).getString("icon");

                    setIcon(id, icon);

                    mainWeatherText.setText(mainDesc);
                    descriptionText.setText(description);

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                // handle error


                Toast.makeText(getApplicationContext(), "response failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onErrorResponse: JSON Error Response ");

            }

        });

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }


    /**
     * Convert Kelvin to Celsius where C = K - 273.15
     * @param k
     * @return
     */
    public int convertKelvinToCelsius(double k){
        return  (int)Math.round(k - 273.15);
    }


    /**
     * Displays the correct weather icon based on the id given from API JSON.
     * There is a bug where a clear sky returns icon for cloudy sky.
     * https://openweathermap.org/weather-conditions
     * @param id, icon
     */
    public void setIcon(int id, String icon){

        //get the d or n denoting day or night
        String lastChar = icon.substring(icon.length()-1);


        //change the background gradient depending on if it is day or night
        if(lastChar.equals("d")){

            screen.setBackgroundResource(R.drawable.gradient_day);

        }else if(lastChar.equals("n")){

            screen.setBackgroundResource(R.drawable.gradient_night);
        }



        //check for the edge case where clear sky id has a cloudy icon in the JSON
        if( id == 800 ){

            //if d make it clear day icon
            if(lastChar.equals("d")){
                imageView.setImageResource(R.drawable.ic_01d);

            //if n make it clear night icon
            }else if(lastChar.equals("n")){
                imageView.setImageResource(R.drawable.ic_01n);
            }

            return;

        }


        switch(icon){
            case "01d":
                imageView.setImageResource(R.drawable.ic_01d); break;
            case "01n":
                imageView.setImageResource(R.drawable.ic_01n); break;
            case "02d":
                imageView.setImageResource(R.drawable.ic_02d); break;
            case "02n":
                imageView.setImageResource(R.drawable.ic_02n); break;
            case "03d":
            case "03n":
                imageView.setImageResource(R.drawable.ic_03); break;
            case "04d":
            case "04n":
                imageView.setImageResource(R.drawable.ic_04); break;
            case "09d":
            case "09n":
                imageView.setImageResource(R.drawable.ic_09); break;
            case "10d":
                imageView.setImageResource(R.drawable.ic_10d); break;
            case "10n":
                imageView.setImageResource(R.drawable.ic_10n); break;
            case "11d":
            case "11n":
                imageView.setImageResource(R.drawable.ic_11); break;
            case "13d":
            case "13n":
                imageView.setImageResource(R.drawable.ic_13); break;
            case "50d":
            case "50n":
                imageView.setImageResource(R.drawable.ic_50); break;
            default:
                imageView.setImageResource(R.drawable.ic_cloud_off_white_24dp); break;

        }
    }


}
