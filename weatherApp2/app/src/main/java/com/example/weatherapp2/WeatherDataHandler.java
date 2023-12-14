package com.example.weatherapp2;// WeatherDataHandler.java
import android.content.Context;
import android.content.SharedPreferences;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherDataHandler {
    private static final String TAG = "WeatherDataHandler";
    private static final String API_KEY = "96c564b80cd676b133ac67db7c516934"; // Replace with your actual API key
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?";

    private Context context;
    private RequestQueue queue;
    private WeatherDataListener listener;

    public WeatherDataHandler(Context context, WeatherDataListener listener) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
        this.listener = listener;
    }

    public void fetchWeatherData(double latitude, double longitude) { //fetches weatherdata using coordinates
        String url = String.format(BASE_URL + "lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY + "&units=metric"); //update url for api-call
        StringRequest request = new StringRequest(Request.Method.GET, url, this::parseWeatherJson, this::handleError); //make api-call and call function to parse it

        queue.add(request);
    }

    private void parseWeatherJson(String response) { //parses Json and assings data to correct variables

        //loads preferences for displaying correct units
        SharedPreferences sharedPref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        boolean isFahrenheit = sharedPref.getBoolean("isFahrenheit", false);
        boolean is12HourClock = sharedPref.getBoolean("is12HourClock", false);
        boolean isMilesPerHour = sharedPref.getBoolean("isMilesPerHour", false);

        //start the parsing
        try {
            JSONObject weatherJSON = new JSONObject(response);

            String city = weatherJSON.getString("name");
            String weather = weatherJSON.getJSONArray("weather").getJSONObject(0).getString("main");
            String weatherIcon = weatherJSON.getJSONArray("weather").getJSONObject(0).getString("icon");
            double temperature = weatherJSON.getJSONObject("main").getDouble("temp");
            double feelsLike = weatherJSON.getJSONObject("main").getDouble("feels_like");
            double tempMin = weatherJSON.getJSONObject("main").getDouble("temp_min");
            double tempMax = weatherJSON.getJSONObject("main").getDouble("temp_max");
            int humidity = weatherJSON.getJSONObject("main").getInt("humidity");
            double windSpeed = weatherJSON.getJSONObject("wind").getDouble("speed");
            int windDegrees = weatherJSON.getJSONObject("wind").getInt("deg");

            //formats time to correct format
            SimpleDateFormat sdf;
            if (is12HourClock) {
                sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            } else {
                sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            }

            long sunriseUnix = weatherJSON.getJSONObject("sys").getLong("sunrise");
            Date sunriseDate = new Date(sunriseUnix * 1000L); // Convert seconds to milliseconds
            String sunrise = sdf.format(sunriseDate);

            long sunsetUnix = weatherJSON.getJSONObject("sys").getLong("sunset");
            Date sunsetDate = new Date(sunsetUnix * 1000L); // Convert seconds to milliseconds
            String sunset = sdf.format(sunsetDate);

            //converts to fahrenheit if app is set to fahrenheit
            if(isFahrenheit) {
                temperature = (1.8 * temperature + 32);
                tempMin = (1.8 * tempMin + 32);
                tempMax = (1.8 * tempMax + 32);
                feelsLike = (1.8 * feelsLike + 32);
            }

            //converts to mph if app is set to mph
            if(isMilesPerHour) {
                windSpeed = windSpeed * 2.237;
            }

            //makes new weatherData class where all the information is stored
            WeatherData weatherData = new WeatherData(
                    weather,
                    (int) temperature,
                    (int) feelsLike,
                    (int) windSpeed,
                    city,
                    weatherIcon,
                    windDegrees,
                    sunrise,
                    sunset,
                    (int) tempMin,
                    (int) tempMax,
                    humidity
            );
            listener.onWeatherDataUpdate(weatherData);
        } catch (JSONException e) {
        }
    }

    private void handleError(VolleyError error) {
    }

    public interface WeatherDataListener {
        void onWeatherDataUpdate(WeatherData weatherData);
    }
}