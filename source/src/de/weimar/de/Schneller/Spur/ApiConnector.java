package de.weimar.de.Schneller.Spur;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;


public class ApiConnector {

	private static String SERVERIP="192.168.2.15";
	HttpResponse httpResponse;
	HttpEntity httpEntity=null;
	JSONArray jsonArray = null;
	
	public JSONArray GetAllProducts(String current_gps)
    {
        // URL for getting all customers
 	        String url = "http://"+SERVERIP+"/buysell/products.php?gps_add="+current_gps;
 	       return getMyJson(url);
    }


    public JSONArray getproductdetail(int Pid) {
    	
    	 // URL for getting all customers
    	String url = "http://"+SERVERIP+"/buysell/products_detail.php?Pid="+Pid;
        return getMyJson(url);    	
    }
    
    public JSONArray getseachItems(String item){
    	
    	 // URL for getting all customers
        String url = "http://"+SERVERIP+"/buysell/search.php?item="+item;
        return getMyJson(url);
        }
    
    
    public JSONArray GetAllPurchases(String userid)
    {
        // URL for getting all customers
    	String url = "http://"+SERVERIP+"/buysell/getpurchases.php?userid="+userid;
        return getMyJson(url);
    }
    
    public JSONArray GetAllRequests(String userid)
    {
        // URL for getting all customers
    	String url = "http://"+SERVERIP+"/buysell/getrequests.php?userid="+userid;
        return getMyJson(url);
        }
    
    
    public JSONArray getMyJson(String url)
    {
    	  try
          {
              DefaultHttpClient httpClient = new DefaultHttpClient();  // Default HttpClient
              HttpGet httpGet = new HttpGet(url);
              httpResponse = httpClient.execute(httpGet);
              
          } catch (ClientProtocolException e) {
               e.printStackTrace();
              //Log Errors Here
          } catch (IOException e) {
              e.printStackTrace();
          }
    	  httpEntity = httpResponse.getEntity();
    	  
    	  if (httpEntity != null) {
              try {
                  String entityResponse = EntityUtils.toString(httpEntity);

                  Log.e("Entity Response  : ", entityResponse);

                  jsonArray = new JSONArray(entityResponse);

              } catch (JSONException e) {
                  e.printStackTrace();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
          return jsonArray;  
    }
}
