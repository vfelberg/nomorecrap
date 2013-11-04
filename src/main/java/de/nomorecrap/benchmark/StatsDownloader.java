package de.nomorecrap.benchmark;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.httpclient.methods.GetMethod;

import java.net.URL;

public class StatsDownloader {

  public static final float NO_RESULT = -99.0f;
  String host;

  public StatsDownloader() {
    this("http://www.crap4j.org/benchmark/");
//    this("http://localhost:3000/");
  }
  
  public StatsDownloader(String host) {
    if (host == null)
      throw new IllegalArgumentException("Need valid host");
    this.host = host;
  }
  
  public GlobalStats getAverage(boolean downloadAverages) {
    if (!downloadAverages) {
      return GlobalStats.NULL_STATS;
    }
    URL statsUrl;
    try {
      statsUrl = new URL(host+"stats/average_crap/1");// 1 is useless
      HttpClient httpclient = new HttpClient();
      httpclient.setConnectionTimeout(5000);
      
      GetMethod httpget = new GetMethod(statsUrl.toString());
      httpget.addRequestHeader("Accept", "text/xml");
      try { 
        httpclient.executeMethod(httpget);
        StatusLine resultStatus = httpget.getStatusLine();
        if (resultStatus.getStatusCode() == 200) {
          String responseBodyAsString = httpget.getResponseBodyAsString();
//          System.out.println(responseBodyAsString);    
          return new GlobalStats(Float.parseFloat(responseBodyAsString), -1, -1, -1); // right now we only get and only use the average.
        }
      } finally {
        httpget.releaseConnection();
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      //e.printStackTrace();
    }
    return GlobalStats.NULL_STATS;
  }
  
  public static void main(String[] args) {
    new StatsDownloader("http://localhost:3000/").getAverage(true);
  }
}
