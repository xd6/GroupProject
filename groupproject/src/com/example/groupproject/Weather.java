package com.example.groupproject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.LocationClient;

public class Weather extends Activity {
	
	ImageView weather_1_image;
	TextView weather_1_text;
	ImageView weather_2_image;
	TextView weather_2_text;
	ImageView weather_3_image;
	TextView weather_3_text;
	ImageView weather_4_image;
	TextView weather_4_text;
	ImageView weather_5_image;
	TextView weather_5_text;
	
	LocationClient location_client;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather);

		weather_1_image = (ImageView)findViewById(R.id.weather_1_image);
		weather_1_text = (TextView)findViewById(R.id.weather_1_text);
		weather_2_image = (ImageView)findViewById(R.id.weather_2_image);
		weather_2_text = (TextView)findViewById(R.id.weather_2_text);
		weather_3_image = (ImageView)findViewById(R.id.weather_3_image);
		weather_3_text = (TextView)findViewById(R.id.weather_3_text);
		weather_4_image = (ImageView)findViewById(R.id.weather_4_image);
		weather_4_text = (TextView)findViewById(R.id.weather_4_text);
		weather_5_image = (ImageView)findViewById(R.id.weather_5_image);
		weather_5_text = (TextView)findViewById(R.id.weather_5_text);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/* Any time the activity of the app changes at all, update the weather information because
	 * the location may have changed.
	 */
	@Override
	public void onResume() {
		super.onResume();
		String url = String.format("http://api.openweathermap.org/data/2.5/forecast/daily?lat=%s&lon=%s&cnt=5&mode=xml&units=imperial", MainActivity.latitude, MainActivity.longitude);
		loadPage(url);
	}

	/********************************** PARSE **********************************/
	
	/* Class used to return a set of data from the parser */
	public static class Entry {
	    public final String weather_desc;
	    public final double min_temp;
	    public final double max_temp;
	    
	    private Entry(String weather_desc, double min_temp, double max_temp) {
	        this.weather_desc = weather_desc;
	        this.min_temp = min_temp;
	        this.max_temp = max_temp;
	    }
	}

    /* Use this API to download and parse the given url */
    public void loadPage(String url) {  
            new DownloadXmlTask().execute(url);
    }

	/* Class to download and parse an XML document at given url */
	private class DownloadXmlTask extends AsyncTask<String, Void, List<Entry> > {

		/* In an asynchronous task, download and parse an XML document
		 * from given URL.
		 */
	    protected List<Entry> doInBackground(String... urls) {
	        try {
	            return loadXmlFromNetwork(urls[0]);
	        } catch (IOException e) {
	            //return "IOException";
	        	return null;
	        } catch (XmlPullParserException e) {
	            //return "XmlPullParserException";
	        	return null;
	        }
	    }

	    /* After the document has been downloaded and parsed, use the
	     * results to populate the weather information.
	     */
	    protected void onPostExecute(List<Entry> result) {
	    	Date dNow = new Date();
	        SimpleDateFormat ft = new SimpleDateFormat ("MM.dd");
	        Double minTemp = 0.0;
	        Double maxTemp = 0.0;
	        
	        Calendar cal = Calendar.getInstance();
			cal.setTime(dNow);
	        
	    	minTemp = result.get(0).min_temp;
	    	maxTemp = result.get(0).max_temp;
	    	setWeatherImage(weather_1_image, result.get(0).weather_desc);
	    	
			weather_1_text.setText(ft.format(dNow) + " Min Temp: " + String.valueOf(minTemp.intValue()) + " Max Temp: " + String.valueOf(maxTemp.intValue()));
			
			minTemp = result.get(1).min_temp;
	    	maxTemp = result.get(1).max_temp;
			cal.add(Calendar.DAY_OF_YEAR, 1);
			dNow = cal.getTime();
			setWeatherImage(weather_2_image, result.get(1).weather_desc);
			weather_2_text.setText(ft.format(dNow) + " Min Temp: " + String.valueOf(minTemp.intValue()) + " Max Temp: " + String.valueOf(maxTemp.intValue()));
			
			minTemp = result.get(2).min_temp;
	    	maxTemp = result.get(2).max_temp;
			cal.add(Calendar.DAY_OF_YEAR, 1);
			dNow = cal.getTime();
			setWeatherImage(weather_3_image, result.get(2).weather_desc);
			weather_3_text.setText(ft.format(dNow) + " Min Temp: " + String.valueOf(minTemp.intValue()) + " Max Temp: " + String.valueOf(maxTemp.intValue()));
			
			minTemp = result.get(3).min_temp;
	    	maxTemp = result.get(3).max_temp;
			cal.add(Calendar.DAY_OF_YEAR, 1);
			dNow = cal.getTime();
			setWeatherImage(weather_4_image, result.get(3).weather_desc);
			weather_4_text.setText(ft.format(dNow) + " Min Temp: " + String.valueOf(minTemp.intValue()) + " Max Temp: " + String.valueOf(maxTemp.intValue()));
			
			minTemp = result.get(4).min_temp;
	    	maxTemp = result.get(4).max_temp;
			cal.add(Calendar.DAY_OF_YEAR, 1);
			dNow = cal.getTime();
			setWeatherImage(weather_5_image, result.get(4).weather_desc);
			weather_5_text.setText(ft.format(dNow) + " Min Temp: " + String.valueOf(minTemp.intValue()) + " Max Temp: " + String.valueOf(maxTemp.intValue()));
	    }
	    
	    void setWeatherImage(ImageView imgView, String weather) {
	    	if(weather.equals("broken clouds") || weather.equals("few clouds") || weather.equals("scattered clouds")) {
	    		imgView.setImageResource(R.drawable.part_cloud);
	    	}
	    	else if(weather.equals("overcast clouds")) {
	    		imgView.setImageResource(R.drawable.cloud);
	    	}
	    	else if(weather.equals("sky is clear")) {
	    		imgView.setImageResource(R.drawable.sun);
	    	}
	    	else if(weather.equals("light rain") || weather.equals("moderate rain") || weather.equals("heavy intensity rain")) {
	    		imgView.setImageResource(R.drawable.rain);
	    	}
	    }
	
		/* Download the URL, parse it, and return the parsed weather data */
		private List<Entry> loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
		    InputStream stream = null;
		    WeatherXmlParser WeatherXmlParser = new WeatherXmlParser();
		    List<Entry> entries = null;
		        
		    try {
		        stream = downloadUrl(urlString);        
		        entries = WeatherXmlParser.parse(stream);
		    // Makes sure that the InputStream is closed after the app is
		    // finished using it.
		    } finally {
		        if (stream != null) {
		            stream.close();
		        } 
		     }
	
		    //return htmlString.toString();
		    return entries;
		    //return stream;
		}
	
		/* Download the given URL */
		private InputStream downloadUrl(String urlString) throws IOException {
		    URL url = new URL(urlString);
		    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		    conn.setReadTimeout(10000 /* milliseconds */);
		    conn.setConnectTimeout(15000 /* milliseconds */);
		    conn.setRequestMethod("GET");
		    conn.setDoInput(true);
		    // Starts the query
		    conn.connect();
		    return conn.getInputStream();
		}
	}
	
	/************************ XML PARSING *******************************/
	
	public class WeatherXmlParser {
	   
		/* Parse an input XML stream to determine weather forecast */
	    public List<Entry> parse(InputStream in) throws XmlPullParserException, IOException {
	        try {
	            XmlPullParser parser = Xml.newPullParser();
	            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(in, null);

	            return readFeed(parser);
	        } finally {
	            in.close();
	        }
	    }
	
		private List<Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		    List<Entry> entries = new ArrayList<Entry>();
	
		    //parser.require(XmlPullParser.START_TAG, null, "forecast");
		    /* While there is valid data to be parsed */
		    while (parser.next() != XmlPullParser.END_DOCUMENT) {
		    	/* Only check start tags */
		        if (parser.getEventType() != XmlPullParser.START_TAG) {
		            continue;
		        }
		        
		        /* If the start tag name is 'time', we want to parse it, because
		         * it is forecast data.  If it's not 'time', we just continue
		         */
		        String name = parser.getName();
		        if (name.equals("time")) {
		            entries.add(readEntry(parser));
		        }
		    }  
		    return entries;
		}
		
		/* Parse a 'time' entry.  When we see 'symbol' or 'temperature' start tags, we need
		 * to do further processing to gather data
		 */
		private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
		    parser.require(XmlPullParser.START_TAG, null, "time");
		    
		    String weather = null;
		    double minTemp = 0;
		    double maxTemp = 0;
		    
		    while (parser.next() != XmlPullParser.END_TAG) {
		    	/* Only check start tags */
		        if (parser.getEventType() != XmlPullParser.START_TAG) {
		            continue;
		        }
		        
		        /* Read out symbol or temperature data if we've found that
		         * start tag
		         */
		        String name = parser.getName();
		        if (name.equals("symbol")) {
		        	weather = readSymbol(parser);
		        } else if (name.equals("temperature")) {
		        	minTemp = readMinTemp(parser);
		        	maxTemp = readMaxTemp(parser);
		        } else {
		            skip(parser);
		        }
		    }
		    
		    return new Entry(weather, minTemp, maxTemp);
		}
		
		/* Skip the current XML entry, and all of its children */
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
		
		/* Read the symbol XML element to extract weather information */
		private String readSymbol(XmlPullParser parser) throws IOException, XmlPullParserException {
			String name;
		    parser.require(XmlPullParser.START_TAG, null, "symbol");
		    name = parser.getAttributeValue(null, "name");
		    while(parser.next() != XmlPullParser.END_TAG) {}
		    parser.require(XmlPullParser.END_TAG, null, "symbol");
		    return name;
		}
		
		/* Read the temperature XML element to extract min temp information */
		private double readMinTemp(XmlPullParser parser) throws IOException, XmlPullParserException {
			String stemp;
		    parser.require(XmlPullParser.START_TAG, null, "temperature");
		    stemp = parser.getAttributeValue(null, "min");
		    return Double.parseDouble(stemp);
		}
		
		/* Read the temperature XML element to extract max temp information */
		private double readMaxTemp(XmlPullParser parser) throws IOException, XmlPullParserException {
			String stemp;
		    parser.require(XmlPullParser.START_TAG, null, "temperature");
		    stemp = parser.getAttributeValue(null, "max");
		    while(parser.next() != XmlPullParser.END_TAG) {}
		    parser.require(XmlPullParser.END_TAG, null, "temperature");
		    return Double.parseDouble(stemp);
		}
	}
}