package com.technisat.radiotheque.splash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

public class NexxooHttpJsonRequest {
	
	public String performJsonRequest(String url, String json){
		try {
	        HttpClient httpclient = createHttpClient(); //new DefaultHttpClient();
	        HttpPost httpPostRequest = new HttpPost(url);

	        StringEntity se;
	        se = new StringEntity(json);

	        httpPostRequest.setEntity(se);
	        httpPostRequest.setHeader("Accept", "application/json");
	        httpPostRequest.setHeader("Content-type", "application/json");
	        //httpPostRequest.setHeader("Accept-Encoding", "gzip"); 

	        long t = System.currentTimeMillis();
	        HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
	        Log.i("Nexxoo", "HTTPResponse received in [" + (System.currentTimeMillis()-t) + "ms]");

	        HttpEntity entity = response.getEntity();

	        if (entity != null) {
	            InputStream instream = entity.getContent();	            
	            Header contentEncoding = response.getFirstHeader("Content-Encoding");
	            if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
	                instream = new GZIPInputStream(instream);
	            }

	            String resultString = convertStreamToString(instream);
	            instream.close();
	            Log.i("Nexxoo","Response: " + resultString);

	            return resultString;
	        } 

	    }
	    catch (Exception e)
	    {
	        Log.e("Exception", "Exception: " + e.getMessage());
	    }
		
	    return null;
	}
	
	private HttpClient createHttpClient()
	{
	    HttpParams params = new BasicHttpParams();
	    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	    HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
	    HttpProtocolParams.setUseExpectContinue(params, true);

	    SchemeRegistry schReg = new SchemeRegistry();
	    schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	    schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
	    ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

	    return new DefaultHttpClient(conMgr, params);
	}
	
	private String convertStreamToString(InputStream is) {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
}
