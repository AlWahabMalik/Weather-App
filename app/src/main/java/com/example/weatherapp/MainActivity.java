package com.example.weatherapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Date;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    SearchView searchView;
    LottieAnimationView lottieAnimationView;
    TextView CityName, Weather, Temperature, MaxTemp, MinTemp, Day, Date, Humidity, Wind, Condition, Sunrise, Sunset, Sea;

    private OkHttpClient client = new OkHttpClient();
    private String apiKey = "0303d25b4ca44d066fa05971f01bf6e8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link XML elements
        searchView = findViewById(R.id.searchView);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);

        CityName = findViewById(R.id.CityName);
        Weather = findViewById(R.id.weather);
        Temperature = findViewById(R.id.temperature);
        MaxTemp = findViewById(R.id.MaxTemp);
        MinTemp = findViewById(R.id.MinTemp);
        Day = findViewById(R.id.day);
        Date = findViewById(R.id.date);
        Humidity = findViewById(R.id.humidity);
        Wind = findViewById(R.id.wind);
        Condition = findViewById(R.id.condition);
        Sunrise = findViewById(R.id.sunrise);
        Sunset = findViewById(R.id.sunset);
        Sea = findViewById(R.id.sea);

        // Set default animation
        lottieAnimationView.setAnimation(R.raw.sun);

        // Restore last weather state if available
        restoreLastWeatherState();

        // Set search query listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchWeatherData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        updateDayAndDate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save the last searched city and weather info
        saveLastWeatherState();
    }

    private void updateDayAndDate() {
        // Get current date
        Calendar calendar = Calendar.getInstance();

        // Format day of the week (e.g., Monday)
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String day = dayFormat.format(calendar.getTime());

        // Format date (e.g., 15 September 2024)
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String date = dateFormat.format(calendar.getTime());

        // Update TextViews
        Day.setText(day);
        Date.setText(date);
    }

    private void fetchWeatherData(String cityName) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey + "&units=metric";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("WeatherApp", "Failed to fetch data", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();

                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(jsonData);

                            // Extract weather condition
                            String weatherCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");

                            // Update UI and animation based on weather
                            updateWeatherUI(weatherCondition, jsonObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    // If city is not found or there is an error, show a toast message
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "City not found, please try again", Toast.LENGTH_SHORT).show();
                    });
                }
            }

        });
    }

    private void updateWeatherUI(String weatherCondition, JSONObject jsonObject) throws JSONException {
        // Extract and update other weather info
        String cityName = jsonObject.getString("name");
        String temperature = jsonObject.getJSONObject("main").getString("temp");
        String maxTemp = jsonObject.getJSONObject("main").getString("temp_max");
        String minTemp = jsonObject.getJSONObject("main").getString("temp_min");
        String humidity = jsonObject.getJSONObject("main").getString("humidity");
        String windSpeed = jsonObject.getJSONObject("wind").getString("speed");
        String seaLevel = jsonObject.getJSONObject("main").getString("sea_level");

        // Convert and display sunrise and sunset times
        long sunriseTime = jsonObject.getJSONObject("sys").getLong("sunrise");
        long sunsetTime = jsonObject.getJSONObject("sys").getLong("sunset");

        // Set text to TextViews
        CityName.setText(cityName);
        Weather.setText(weatherCondition);
        Temperature.setText(temperature + " °C");
        MaxTemp.setText("Max: " + maxTemp + " °C");
        MinTemp.setText("Min: " + minTemp + " °C");
        Sea.setText(seaLevel);
        Condition.setText(weatherCondition);
        Humidity.setText(humidity + "%");
        Wind.setText(windSpeed + " m/s");

        Sunrise.setText(convertUnixToTime(sunriseTime));
        Sunset.setText(convertUnixToTime(sunsetTime));

        // Change Lottie animation based on weather condition
        switch (weatherCondition.toLowerCase()) {
            case "rain":
                lottieAnimationView.setAnimation(R.raw.rain);
                break;
            case "clear":
                lottieAnimationView.setAnimation(R.raw.sun);
                break;
            case "clouds":
                lottieAnimationView.setAnimation(R.raw.cloud);
                break;
            case "snow":
                lottieAnimationView.setAnimation(R.raw.snow);
                break;
            default:
                lottieAnimationView.setAnimation(R.raw.sun); // default to sun
                break;
        }

        // Play the animation
        lottieAnimationView.playAnimation();
    }

    private String convertUnixToTime(long unixSeconds) {
        // Convert seconds to milliseconds
        Date date = new Date(unixSeconds * 1000L);

        // Format time to "hh:mm a" (e.g., "06:45 AM")
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return timeFormat.format(date);
    }

    // Save the last searched city and weather info using SharedPreferences
    private void saveLastWeatherState() {
        SharedPreferences sharedPreferences = getSharedPreferences("WeatherApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("last_city", CityName.getText().toString());
        editor.putString("last_weather", Weather.getText().toString());
        editor.putString("last_temperature", Temperature.getText().toString());
        editor.putString("last_max_temp", MaxTemp.getText().toString());
        editor.putString("last_min_temp", MinTemp.getText().toString());
        editor.putString("last_day", Day.getText().toString());
        editor.putString("last_date", Date.getText().toString());
        editor.putString("last_sunrise", Sunrise.getText().toString());
        editor.putString("last_sunset", Sunset.getText().toString());
        editor.putString("last_humidity", Humidity.getText().toString());
        editor.putString("last_wind", Wind.getText().toString());
        editor.putString("last_condition", Condition.getText().toString());
        editor.putString("last_sea_level", Sea.getText().toString());

        // Save the animation resource ID
        editor.putInt("last_animation", getAnimationResourceId(Weather.getText().toString()));

        editor.apply();


    }

    // Restore the last saved state from SharedPreferences
    private void restoreLastWeatherState() {
        SharedPreferences sharedPreferences = getSharedPreferences("WeatherApp", MODE_PRIVATE);

        String lastCity = sharedPreferences.getString("last_city", null);
        String lastWeather = sharedPreferences.getString("last_weather", null);
        String lastTemperature = sharedPreferences.getString("last_temperature", null);
        String lastMaxTemp = sharedPreferences.getString("last_max_temp", null);
        String lastMinTemp = sharedPreferences.getString("last_min_temp", null);
        String lastDay = sharedPreferences.getString("last_day", null);
        String lastDate = sharedPreferences.getString("last_date", null);
        String lastSunrise = sharedPreferences.getString("last_sunrise", null);
        String lastSunset = sharedPreferences.getString("last_sunset", null);
        String lastHumidity = sharedPreferences.getString("last_humidity", null);
        String lastWind = sharedPreferences.getString("last_wind", null);
        String lastCondition = sharedPreferences.getString("last_condition", null);
        String lastSeaLevel = sharedPreferences.getString("last_sea_level", null);


        // Set default animation;

        // If data is available, set it to the respective TextViews
        if (lastCity != null) {
            CityName.setText(lastCity);
            Weather.setText(lastWeather);
            Temperature.setText(lastTemperature);
            MaxTemp.setText(lastMaxTemp);
            MinTemp.setText(lastMinTemp);
            Day.setText(lastDay);
            Date.setText(lastDate);
            Sunrise.setText(lastSunrise);
            Sunset.setText(lastSunset);
            Humidity.setText(lastHumidity);
            Wind.setText(lastWind);
            Condition.setText(lastCondition);
            Sea.setText(lastSeaLevel);

            // Restore the animation
            int animationResId = sharedPreferences.getInt("last_animation", R.raw.sun);
            lottieAnimationView.setAnimation(animationResId);
            lottieAnimationView.playAnimation();
        }
    }
        private int getAnimationResourceId(String weatherCondition) {
            switch (weatherCondition.toLowerCase()) {
                case "rain":
                    return R.raw.rain;
                case "clear":
                    return R.raw.sun;
                case "clouds":
                    return R.raw.cloud;
                case "snow":
                    return R.raw.snow;
                default:
                    return R.raw.sun; // default to sun
            }
        }

}
