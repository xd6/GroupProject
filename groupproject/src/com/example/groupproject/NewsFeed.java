package com.example.newsfeed;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Xml;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.ImageView;

public class NewsFeed extends Activity {

  WebView webkit;
  List<Item> entries;
  URL url;

  public static class Item 
  {
    public final String description;
    public final ImageView img = null;

    Item(String description){
      this.description = description;
      
    }
  }



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    new XmlParser().execute("https://news.google.com/news/feeds?pz=1&cf=all&ned=us&hl=en&geo=49426&output=rss");
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }



  class XmlParser extends AsyncTask<String, Void, String>{
    public List<Item> parse(InputStream in) 
        throws XmlPullParserException, IOException
        {
      try
      {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(in, null);
        return readFeed(parser);
      }
      catch(Exception e)
      {
        entries.add(new Item("Error loading content."));
        return entries;
      }
      finally
      {
        in.close();
      }

        }

    private String loadXml(String url)
        throws XmlPullParserException, IOException
        {
      InputStream stream = null;
      List<Item> entries = null;

      try
      {
        stream = downloadUrl(url);
        entries = parse(stream);
      }
      catch(Exception e)
      {
        return "Error loading content.";
      }
      finally
      {
        if(stream != null)
          stream.close();
      }
      StringBuilder code = new StringBuilder("<html><body>");
      for (Item item : entries)
      {
        code.append(item.description );
      }
      code.append("</body></html>");
      
      return code.toString();
        }

    private List<Item> readFeed(XmlPullParser parser) 
        throws XmlPullParserException, IOException
        {
      List<Item> entries = new ArrayList<Item>();
      int eventType = 0;

      while (eventType != XmlPullParser.END_DOCUMENT)
      {
        eventType = parser.next();
        if (eventType == XmlPullParser.START_TAG)
          if (parser.getName().equalsIgnoreCase("description"))
            entries.add(new Item(parser.nextText()));

      }
      return entries;
        }


    @Override
    protected String doInBackground(String... urls) {
      try{
        return loadXml(urls[0]);
      }
      catch (Exception e )
      {
        return "Error loading content.";

      }

    }

    protected void onPostExecute(String code)
    {
      setContentView(R.layout.activity_main);
      webkit = (WebView)findViewById(R.id.webkit);
      webkit.loadDataWithBaseURL(null, code, "text/html", "utf-8", null);
      
    }

    private InputStream downloadUrl(String urlString) 
        throws IOException
        {
      URL url = new URL(urlString);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setReadTimeout(10000 );
      conn.setConnectTimeout(15000);
      conn.setRequestMethod("GET");
      conn.setDoInput(true);


      conn.connect();
      return conn.getInputStream();
        }

  }


}
