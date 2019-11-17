package com.example.androidassignments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import android.graphics.BitmapFactory;
import android.widget.Spinner;
import android.widget.TextView;

import javax.net.ssl.HttpsURLConnection;

public class WeatherForecast extends AppCompatActivity {
    private static final String ACTIVITY_NAME = "WeatherForecast";
    public ProgressBar progressBar;
    private String API_KEY = "232746053b5803ce56fadf93a2dca606";
    private String[] arraySpinner = new String[] {
            "Ottawa",
            "Toronto",
            "Montreal",
            "Vancouver",
            "Calgary",
            "Edmonton",
            "Winnipeg",
            "Hamilton",
            "Kitchener",
            "London",
            "Victoria",
            "Halifax",
            "Oshawa",
            "Windsor"
    };

    public TextView cityText;
    public String city = arraySpinner[0];
    public TextView currentTemperature;
    public TextView maxTemperature;
    public ImageView weatherImage;
    public TextView minTemperature;
    public Spinner dropdown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        currentTemperature = (TextView) findViewById(R.id.currentTemp);
        maxTemperature = (TextView) findViewById(R.id.maxTemp);
        minTemperature = (TextView) findViewById(R.id.minTemp);
        weatherImage = (ImageView) findViewById(R.id.weatherImage);

        cityText = (TextView) findViewById(R.id.textView10);
        cityText.setText(city);

        dropdown = (Spinner) findViewById(R.id.spinner);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!city.equals(arraySpinner[position])){
                    city = arraySpinner[position];
                    cityText.setText(city);
                    new ForecastQuery().execute(getAPIUrl());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner );
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        new ForecastQuery().execute(this.getAPIUrl());
    }

    private String transformCity(String city){
        return city.toLowerCase() + ",ca";
    }

    private String getAPIUrl(){
        return "https://api.openweathermap.org/data/2.5/weather?q=" + transformCity(city) + "&APPID=" + API_KEY + "&mode=xml&units=metric";
    }

    class Forecast {

        public String minTemp;
        public String maxTemp;
        public String currentTemp;

        @NonNull
        @Override
        public String toString() {
            return "Min: " + this.minTemp + " Max: " + this.maxTemp + " Current: " + this.currentTemp;
        }

        public void setCurrentTemp(String currentTemp) {
            this.currentTemp = currentTemp;
        }

        public void setMaxTemp(String maxTemp) {
            this.maxTemp = maxTemp;
        }

        public void setMinTemp(String minTemp) {
            this.minTemp = minTemp;
        }

        public Forecast() {
            this.minTemp = null;
            this.maxTemp = null;
            this.currentTemp = null;
        }

        public Forecast(String minTemp, String maxTemp, String currentTemp) {
            this.maxTemp = maxTemp;
            this.minTemp = minTemp;
            this.currentTemp = currentTemp;
        }
    }
    public boolean fileExistance(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }
    public class ForecastQuery extends AsyncTask<String, Integer, String> {
        private final String ns = null;
        public Forecast forecast = new Forecast();
        Bitmap currentWeatherImage;

        private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new IllegalStateException();
            }
            int depth = 1;
            while (depth != 0) {
                switch (parser.next()) {
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("SUCCESS")){
                currentTemperature.setText(forecast.currentTemp);
                minTemperature.setText(forecast.minTemp);
                maxTemperature.setText(forecast.maxTemp);
                weatherImage.setImageBitmap(currentWeatherImage);
            }else{
                currentTemperature.setText("-");
                minTemperature.setText(s);
                maxTemperature.setText("-");
            }
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setProgress(0);
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                URL url = new URL(strings[0]);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                InputStream in = conn.getInputStream();
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
                parser.nextTag();

                parser.require(XmlPullParser.START_TAG, ns, "current");
                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }

                    String name = parser.getName();
                    Log.d(ACTIVITY_NAME, "parser name: " + name);
                    if (name.equals("temperature")) {
                        forecast.setMinTemp(parser.getAttributeValue(ns, "min"));
                        publishProgress(25);
                        forecast.setMaxTemp(parser.getAttributeValue(ns, "max"));
                        publishProgress(50);
                        forecast.setCurrentTemp(parser.getAttributeValue(ns, "value"));
                        publishProgress(75);
                    }else if(name.equals("weather")){
                        String iconName = parser.getAttributeValue(ns, "icon");
                        String imageFileName = iconName + ".png";
                        if( fileExistance(imageFileName)){
                            Log.d(ACTIVITY_NAME, "File exists: " + imageFileName);
                            FileInputStream fis = null;
                            try{
                                fis = openFileInput(imageFileName);
                            } catch (FileNotFoundException e){
                                e.printStackTrace();
                            }
                            currentWeatherImage = BitmapFactory.decodeStream(fis);
                        }else{
                            Log.d(ACTIVITY_NAME, "File does not exist: " + imageFileName);
                            String iconURL = "https://openweathermap.org/img/w/" + imageFileName;
                            currentWeatherImage = HttpUtils.getImage(iconURL);
                            FileOutputStream outputStream = openFileOutput(imageFileName, Context.MODE_PRIVATE);
                            currentWeatherImage.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                            outputStream.flush();
                            outputStream.close();
                        }
                        publishProgress(100);
                    }else{
                        skip(parser);
                    }
                }
            } catch (IOException e){
                Log.e(ACTIVITY_NAME, e.toString());
                return "Connection Error";
            } catch (XmlPullParserException e){
                Log.e(ACTIVITY_NAME, e.toString());
                return "XML Pull Parse Exception";
            }
            return "SUCCESS";
        }
    }
}


class HttpUtils {
    public static Bitmap getImage(URL url) {
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                return BitmapFactory.decodeStream(connection.getInputStream());
            } else
                return null;
        } catch (Exception e) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    public static Bitmap getImage(String urlString) {
        try {
            URL url = new URL(urlString);
            return getImage(url);
        } catch (MalformedURLException e) {
            return null;
        }
    }

}
