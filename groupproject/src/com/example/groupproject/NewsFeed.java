package com.example.newsfeed;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.webkit.WebView;

public class NewsFeed extends Activity {

  WebView webkit;
  List<Item> entries = new ArrayList<Item>();
  URL url;

  public static class Item 
  {
    public final String title, link, description;

    Item(String title, String link, String description)
    {
      this.title = title;
      this.link = link;
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

    // public final String ns = null;
    public List<Item> parse(InputStream in) 
        throws XmlPullParserException, IOException
        {
      try
      {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(in, null);
        //parser.nextTag();
        return readFeed(parser);
      }
      catch(XmlPullParserException x)
      {

        entries.add(new Item(x.toString(),"x","x"));
        return entries;
      }
      catch(IOException x)
      {

        entries.add(new Item(x.toString(),"i","i"));
        return entries;
      }
      catch (NullPointerException e)
      {
        entries.add(new Item(e.toString(),"i","i"));
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
      XmlParser parser = new XmlParser();

      try
      {
        stream = downloadUrl(url);
        entries = parse(stream);
      }
      catch (NullPointerException e)
      {
        return e.toString();

      }
      catch (IOException e )
      {
        return e.toString() ;

      }
      catch ( XmlPullParserException e)
      {
        return e.toString() ;

      }
      finally
      {
        if(stream != null)
          stream.close();
        else 
          return "NULL STREAM";
      }
      /*  
      StringBuilder code = new StringBuilder();
      for (Item item : entries)
      {
        code.append(item.title);
      }
      return code.toString();*/
      if(entries.size() == 0)
        entries.add(new Item("empty","",""));
      return entries.get(0).title;
        }

    private List<Item> readFeed(XmlPullParser parser) 
        throws XmlPullParserException, IOException
        {
      List<Item> entries = new ArrayList<Item>();

      //parser.require(XmlPullParser.START_TAG,  null,  "item");

      /* while (parser.next() != XmlPullParser.END_TAG)
      {
        if (parser.getEventType() != XmlPullParser.START_TAG)
          continue;

        String name = parser.getName();

        if (name.equalsIgnoreCase("location"))
          entries.add(readItem(parser));
      }*/
      int eventType = 0;

      while (eventType != XmlPullParser.END_DOCUMENT)
      {
        eventType = parser.next();
        if (eventType == XmlPullParser.START_TAG)
          if (parser.getName().equalsIgnoreCase("description"))
            entries.add(new Item(parser.nextText(),"",""));

      }
      return entries;
        }



    private String readTitle(XmlPullParser parser)
        throws XmlPullParserException, IOException
        {
      parser.require(XmlPullParser.START_TAG, null, "title");
      String title = readText(parser);
      parser.require(XmlPullParser.END_TAG,  null,  "title");
      return title;
        }
    private String readLink(XmlPullParser parser)
        throws XmlPullParserException, IOException
        {
      parser.require(XmlPullParser.START_TAG, null, "link");
      String link = readText(parser);
      parser.require(XmlPullParser.END_TAG,  null,  "link");
      return link;
        }
    private String readDescription(XmlPullParser parser)
        throws XmlPullParserException, IOException
        {
      parser.require(XmlPullParser.START_TAG, null, "description");
      String description = readText(parser);
      parser.require(XmlPullParser.END_TAG,  null,  "description");
      return description;
        }

    private String readText(XmlPullParser parser)
        throws XmlPullParserException, IOException
        {
      String result = "";
      if (parser.next() == XmlPullParser.TEXT)
      {
        result = parser.getText();
        parser.nextTag();
      }
      return result;
        }

    private void skip(XmlPullParser parser)
        throws XmlPullParserException, IOException
        {
      if (parser.getEventType() != XmlPullParser.START_TAG)
        throw new IllegalStateException();

      int depth = 1;
      while (depth != 0)
      {
        switch (parser.next())
        {
        case XmlPullParser.END_TAG:
          depth--;
          break;
        case XmlPullParser.START_TAG:
          depth++;
          break;
        }
      }
        }

    private Item readItem(XmlPullParser parser)
        throws XmlPullParserException, IOException
        {
      parser.require(XmlPullParser.START_TAG,  null, "item");
      String title = "";
      String link = "";
      String description = "";
      while (parser.next() != XmlPullParser.END_TAG)
      {
        if (parser.getEventType() != XmlPullParser.START_TAG)
          continue;
        String name = parser.getName();
        if (name.equals("title"))
          title = readTitle(parser);
        else if (name.equals("link"))
          link = readLink(parser);
        else if (name.equals("description"))
          description = readDescription(parser);
        else
          skip(parser);
      }
      return new Item(title, link, description);
        }//readItem

    @Override
    protected String doInBackground(String... urls) {
      try{
        return loadXml(urls[0]);
      }
      catch (IOException e )
      {
        return e.toString() + "\ndoInBG - IO";

      }
      catch ( XmlPullParserException e)
      {
        return e.toString() + "\ndoInBG";

      }

    }

    protected void onPostExecute(String code)
    {
      setContentView(R.layout.activity_main);
      webkit = (WebView)findViewById(R.id.webkit);
      webkit.loadData(code,  "text/html", "UTF-8");
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

  public String Xml (XmlPullParser xpp) throws XmlPullParserException, IOException
  {
    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
    factory.setNamespaceAware(true);
    //XmlPullParser 
    xpp = factory.newPullParser();

    xpp.setInput( new StringReader ( "<description></description>" ) );
    int eventType = xpp.getEventType();
    while (eventType != XmlPullParser.END_DOCUMENT) {
      if(eventType == XmlPullParser.START_DOCUMENT) {
        return("Start document");
      } else if(eventType == XmlPullParser.START_TAG) {
        if (xpp.getName().equalsIgnoreCase("description"))
        {
          eventType = xpp.next();
          if (eventType == XmlPullParser.TEXT)
            return xpp.getText();
        }   
        return("Start tag "+xpp.getName());
      } else if(eventType == XmlPullParser.END_TAG) {
        return("End tag "+xpp.getName());
      } else if(eventType == XmlPullParser.TEXT) {
        return("Text "+xpp.getText());
      }
      eventType = xpp.next();
    }
    return("End document");

  }

}
