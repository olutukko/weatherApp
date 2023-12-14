package com.example.weatherapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity implements LocationHandler.LocationUpdateListener, WeatherDataHandler.WeatherDataListener {
    private LocationHandler locationHandler;

    private static final String TAG = "MainActivity";


    private TextView cityTextView;
    private TextView weatherTextView;
    private TextView temperatureTextView;
    private TextView feelsLikeTextView;
    private TextView windTextView;
    private TextView tempMinTextView;
    private TextView tempMaxTextView;
    private TextView humidityTextView;
    private TextView sunsetTextView;
    private TextView sunriseTextView;
    private ImageView weatherIconView;
    private ImageView windDegreeArrow;

    private boolean shouldFetchWeatherData = false;

    private boolean isFahrenheit;
    private boolean isMilesPerHour;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews(); //assigns views to variable in order to update them later
        loadPreferences(); //loads preferences for units such as temperature and wind speed
        loadLastWeather(); //loads latest available weather and updates activity with it.

        shouldFetchWeatherData = true; //if true, updated location results to api-call
        locationHandler = new LocationHandler(this, this);
        locationHandler.checkLocationPermission(); //checks for permission to use location and starts location listening
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences(); //updates settings in case they were changed in the settings menu
        fetchWeatherData(); //makes api call to update units after change in settings
        shouldFetchWeatherData = true; //ensures correct location if app has been running in the background while moving a long distance
        locationHandler.checkLocationPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationHandler.stopLocationUpdates();
    }


    @Override
    public void onLocationUpdate(double latitude, double longitude) {

        if (shouldFetchWeatherData){ //if true, makes a new api call using current coordinates
            WeatherDataHandler weatherDataHandler = new WeatherDataHandler(this, this);
            weatherDataHandler.fetchWeatherData(latitude, longitude);
            shouldFetchWeatherData = false;
        }

        //saves coordinates for later use
        SharedPreferences preferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("last_latitude", (float) latitude);
        editor.putFloat("last_longitude", (float) longitude);
        editor.apply();
    }
    @Override
    public void onWeatherDataUpdate(WeatherData weatherData) { //updates activity with new weatherdata and saves it for later use. works by listening if weather data updates

        cityTextView.setText(weatherData.getCity());
        weatherTextView.setText(weatherData.getWeather());

        //sets correct units based on settings
        String temperatureUnit = getString(isFahrenheit ? R.string.temperature_unit_fahrenheit : R.string.temperature_unit_celsius);
        String speedUnit = getString(isMilesPerHour ? R.string.speed_unit_mph : R.string.speed_unit_mps);
        String humidityUnit = getString(R.string.humidity_unit);

        temperatureTextView.setText(String.format(getString(R.string.temperature_format), weatherData.getTemperature(), temperatureUnit));
        feelsLikeTextView.setText(String.format(getString(R.string.temperature_format), weatherData.getFeelsLike(), temperatureUnit));
        windTextView.setText(String.format(getString(R.string.speed_format), weatherData.getWindSpeed(), speedUnit));
        tempMinTextView.setText(String.format(getString(R.string.temperature_format), weatherData.getTempMin(), temperatureUnit));
        tempMaxTextView.setText(String.format(getString(R.string.temperature_format), weatherData.getTempMax(), temperatureUnit));
        humidityTextView.setText(String.format(getString(R.string.humidity_format), weatherData.getHumidity(), humidityUnit));
        sunsetTextView.setText(weatherData.getSunset() + "");
        sunriseTextView.setText(weatherData.getSunrise() + "");
        windDegreeArrow.setRotation(weatherData.getWindDegrees());

        Picasso.with(this)
                .load(WeatherData.getWeatherIconURL()) //make api-call to load the correct icon for weather
                .resize(600, 600)
                .into(weatherIconView);

        SharedPreferences preferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("last_city", weatherData.getCity());
        editor.putString("last_weather", weatherData.getWeather());
        editor.putInt("last_temperature", weatherData.getTemperature());
        editor.putInt("last_feels_like", weatherData.getFeelsLike());
        editor.putInt("last_wind_speed", weatherData.getWindSpeed());
        editor.putInt("last_wind_degrees", weatherData.getWindDegrees());
        editor.putInt("last_temp_min", weatherData.getTempMin());
        editor.putInt("last_temp_max", weatherData.getTempMax());
        editor.putInt("last_humidity", weatherData.getHumidity());
        editor.putString("last_sunset", weatherData.getSunset());
        editor.putString("last_sunrise", weatherData.getSunrise());
        editor.putString("last_icon_url", weatherData.getWeatherIconURL());
        editor.apply();
    }

    private void fetchWeatherData() { //used to update units after change in settings even if location hasn't changed
        SharedPreferences preferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        float lastLatitude = preferences.getFloat("last_latitude", 0);
        float lastLongitude = preferences.getFloat("last_longitude", 0);

        WeatherDataHandler weatherDataHandler = new WeatherDataHandler(this, this);
        weatherDataHandler.fetchWeatherData(lastLatitude, lastLongitude);
    }

    private void initializeViews() { //separate function for intializing views to reduce duplicated code
        cityTextView = findViewById(R.id.cityTextView);
        weatherTextView = findViewById(R.id.weatherTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        feelsLikeTextView = findViewById(R.id.feelsLikeTextView);
        windTextView = findViewById(R.id.windSpeedTextView);
        tempMinTextView = findViewById(R.id.tempMinTextView);
        tempMaxTextView = findViewById(R.id.tempMaxTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        sunsetTextView = findViewById(R.id.sunsetTextView);
        sunriseTextView = findViewById(R.id.sunriseTextView);
        weatherIconView = findViewById(R.id.weatherIconView);
        windDegreeArrow = findViewById(R.id.windDegreeArrow);

        Button changeActivityButton = findViewById(R.id.settingsButton);
        changeActivityButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void loadPreferences() { //loads settings about units used to display weather data
        SharedPreferences preferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        isFahrenheit = preferences.getBoolean("isFahrenheit", false);
        isMilesPerHour = preferences.getBoolean("isMilesPerHour", false);
    }

    protected void loadLastWeather(){ //loads latest available weather data that is visible before new api call has been made
        SharedPreferences preferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String lastCity = preferences.getString("last_city", "");
        String lastWeather = preferences.getString("last_weather", "");
        int lastTemperature = preferences.getInt("last_temperature", 0);
        int lastFeelsLike = preferences.getInt("last_feels_like", 0);
        int lastWindSpeed = preferences.getInt("last_wind_speed", 0);
        int lastWindDeg = preferences.getInt("last_wind_degrees", 0);
        int lastTempMin = preferences.getInt("last_temp_min", 0);
        int lastTempMax = preferences.getInt("last_temp_max", 0);
        int lastHumidity = preferences.getInt("last_humidity", 0);
        String lastSunset = preferences.getString("last_sunset", "");
        String lastSunrise = preferences.getString("last_sunrise", "");
        String lastIconUrl = preferences.getString("last_icon_url", "");

        String temperatureUnit = getString(isFahrenheit ? R.string.temperature_unit_fahrenheit : R.string.temperature_unit_celsius);
        String speedUnit = getString(isMilesPerHour ? R.string.speed_unit_mph : R.string.speed_unit_mps);
        String humidityUnit = getString(R.string.humidity_unit);

        cityTextView.setText(lastCity);
        weatherTextView.setText(lastWeather);
        temperatureTextView.setText(String.format(getString(R.string.temperature_format), lastTemperature, temperatureUnit));
        feelsLikeTextView.setText(String.format(getString(R.string.temperature_format), lastFeelsLike, temperatureUnit));
        windTextView.setText(String.format(getString(R.string.speed_format), lastWindSpeed, speedUnit));
        tempMinTextView.setText(String.format(getString(R.string.temperature_format), lastTempMin, temperatureUnit));
        tempMaxTextView.setText(String.format(getString(R.string.temperature_format), lastTempMax, temperatureUnit));
        humidityTextView.setText(String.format(getString(R.string.humidity_format), lastHumidity, humidityUnit));
        sunsetTextView.setText(lastSunset);
        sunriseTextView.setText(lastSunrise);
        windDegreeArrow.setRotation(lastWindDeg);
        Picasso.with(this)
                .load(lastIconUrl)
                .resize(600, 600)
                .into(weatherIconView);
    };
}