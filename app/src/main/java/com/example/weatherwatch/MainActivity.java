package com.example.weatherwatch;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.ClientError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    EditText etCity;
    TextView result;
    private final String url = "http://api.openweathermap.org/data/2.5/weather";
    private String appID;
    DecimalFormat df = new DecimalFormat("#.##");

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etCity = findViewById(R.id.etCity);
        result = findViewById(R.id.etResult);
        appID = getString(R.string.app_id);
    }

    public void getWeatherDetails(View view) {
        result.setText("");
        Context context = MainActivity.this;
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        String tempUrl = "";
        String city = etCity.getText().toString().trim();
        if(city.equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("City Required")
                    .setMessage("Please enter a city name.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }
        else {
            appID = getString(R.string.app_id);
            tempUrl = url + "?q=" + city + "&appid=" + appID;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String output = "";
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String desc = jsonObjectWeather.getString("description");
                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        double temp = jsonObjectMain.getDouble("temp") - 273.15;
                        double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                        float pressure = jsonObjectMain.getInt("pressure");
                        int humidity = jsonObjectMain.getInt("humidity");
                        JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                        String wind = jsonObjectWind.getString("speed");
                        JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                        String clouds = jsonObjectClouds.getString("all");
                        int cloudsPercentage = Integer.parseInt(clouds.replaceAll("[^0-9]", ""));
                        JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                        String country = jsonObjectSys.getString("country");
                        String cityName = jsonResponse.getString("name");
                        output += "Current weather of " + cityName + ", " + country
                                + "\n Temp: " + df.format(temp) + " °C"
                                + "\n Feels Like: " + df.format(feelsLike) + " °C"
                                + "\n Humidity: " + humidity + "%"
                                + "\n Description: " + desc
                                + "\n Wind Speed: " + wind + " m/s"
                                + "\n Cloudiness: " + clouds + "%"
                                + "\n Pressure: " + pressure + " hPa";
                        Intent intent = new Intent(MainActivity.this, Weather.class);
                        if(temp < 15) {
                            intent.putExtra("temp", "chilly");
                        }
                        else if(cloudsPercentage >= 85) {
                            intent.putExtra("temp", "cloudy");
                        }
                        else {
                            intent.putExtra("temp", "sunny");
                        }
                        intent.putExtra("result", output);
                        startActivity(intent);
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errorMessage;

                    if (error instanceof NoConnectionError) {
                        errorMessage = "No internet connection. Please check your network settings.";
                    } else if (error instanceof NetworkError || error instanceof TimeoutError) {
                        errorMessage = "Unable to connect to the server. Please try again later.";
                    } else if (error instanceof ClientError) {
                        int statusCode = error.networkResponse.statusCode;
                        if (statusCode == 404) {
                            errorMessage = "The city does not exist.";
                        } else {
                            errorMessage = "A client error occurred.";
                        }
                    } else if (error instanceof ServerError) {
                        errorMessage = "Server error. Please try again later.";
                    } else {
                        errorMessage = error.toString().trim();
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Error")
                            .setMessage(errorMessage)
                            .setPositiveButton("OK", null)
                            .show();
                }

            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}
