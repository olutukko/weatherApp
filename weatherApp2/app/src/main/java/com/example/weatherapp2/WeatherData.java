package com.example.weatherapp2;

public class WeatherData {
    private String weather;
    private int temperature;
    private int feelsLike;
    private int windSpeed;
    private String city;
    private String weatherIcon;
    private static String weatherIconURL;
    private int windDegrees;
    private String sunrise;
    private String sunset;
    private int tempMin;
    private int tempMax;
    private int humidity;

    public WeatherData(String weather, int temperature, int feelsLike, int windSpeed, String city, String weatherIcon, int windDegrees, String sunrise, String sunset, int tempMin, int tempMax, int humidity) {
        this.weather = weather;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.windSpeed = windSpeed;
        this.city = city;
        this.weatherIcon = weatherIcon;
        this.weatherIconURL = "https://openweathermap.org/img/wn/" + weatherIcon + "@2x.png";
        this.windDegrees = windDegrees;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.humidity = humidity;
    }

    // Getters
    public String getWeather() {
        return weather;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getFeelsLike() {
        return feelsLike;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public String getCity() {
        return city;
    }


    public static String getWeatherIconURL() {
        return weatherIconURL;
    }

    public int getWindDegrees() {
        return windDegrees;
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public int getTempMin() {
        return tempMin;
    }

    public int getTempMax() {
        return tempMax;
    }

    public int getHumidity() {
        return humidity;
    }
}