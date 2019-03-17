package com.example.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private Button cityButton;

    private TextView cityNameText;
    private TextView currentTempText;
    private TextView maxTempText;
    private TextView minTempText;
    private TextView mainWeatherText;
    private TextView descriptionText;
    private TextView humidityText;
    private TextView cloudsText;

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

                    //Log.e(TAG, "onResponse: JSON response: " + response.getJSONObject("main").toString());

                    JSONObject main = response.getJSONObject("main");

                    //Log.e(TAG, "onResponse: JSON main: " + main.toString());

                    double currentTemp = main.getDouble("temp");
                    double minTemp = main.getDouble("temp_min");
                    double maxTemp = main.getDouble("temp_max");
                    int humidity = main.getInt("humidity");

                    Log.e(TAG, "onResponse: JSON temp: " + Double.toString(currentTemp));

                    currentTempText.setText(Integer.toString(convertKelvinToCelsius(currentTemp))+"°C");
                    minTempText.setText("Min. " + Integer.toString(convertKelvinToCelsius(minTemp))+"°C");
                    maxTempText.setText("Max. " + Integer.toString(convertKelvinToCelsius(maxTemp))+"°C");
                    humidityText.setText(humidity + "%");

                    JSONObject clouds = response.getJSONObject("clouds");

                    int cloudRate = clouds.getInt("all");
                    cloudsText.setText(cloudRate + "%");

                    JSONArray weather = response.getJSONArray("weather");

                    String mainDesc = weather.getJSONObject(0).getString("main");
                    String description = weather.getJSONObject(0).getString("description");
                    String icon = weather.getJSONObject(0).getString("icon");


                    mainWeatherText.setText(mainDesc);
                    descriptionText.setText(description);


                    

//                    String temperature = main.getJSONObject("temp").toString();
//
//
//                    temperature = Double.toString(convertKelvinToCelsius(Double.parseDouble(temperature)))+"°C";

//                    Log.e(TAG, "onResponse: Temperature is " + temperature );

                }catch (JSONException e){
                    e.printStackTrace();
                }



                //Toast.makeText(getApplicationContext(), "response: "+response.toString(), Toast.LENGTH_SHORT).show();

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

}
