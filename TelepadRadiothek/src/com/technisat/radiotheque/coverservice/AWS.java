package com.technisat.radiotheque.coverservice;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;

import com.technisat.radiotheque.entity.CoverUrls;

public class AWS {
	    
    /*
     * Your AWS Secret Key corresponding to the above ID, as taken from the AWS
     * Your Account page.
     */
    private static final String ENDPOINT = "ecs.amazonaws.com";
    public static final String IMAGESIZE_SMALL = "SmallImage";
    public static final String IMAGESIZE_MEDIUM = "MediumImage";
    public static final String IMAGESIZE_LARGE = "LargeImage";
    public static final String DEFAULT_PRODUCTPAGE = "DetailPageURL";
    
    private String s1 = "AKIAI";
    private String s4 = "DvI/XWf4m";
    private String s5 = "R+ZExJeKxDnMM";
    
    private String getAmazonEndpoint(){
    	String result = ENDPOINT;
    	
    	String iso = Locale.getDefault().getISO3Country();
//    	Log.d("Nexxoo", iso);
    	
    	if (iso != null && iso.equalsIgnoreCase("deu"))
    		return "ecs.amazonaws.de";
    	
    	if (iso != null && iso.equalsIgnoreCase("can"))
    		return "ecs.amazonaws.ca";
    	
    	if (iso != null && iso.equalsIgnoreCase("chn"))
    		return "ecs.amazonaws.cn";
    	
    	//did not work on 03-31-2014
//    	if (iso != null && iso.equalsIgnoreCase("esp"))
//    		return "ecs.amazonaws.es"; 
    	
    	if (iso != null && iso.equalsIgnoreCase("fra"))
    		return "ecs.amazonaws.fr"; 
    	
    	//did not work on 03-31-2014
//    	if (iso != null && iso.equalsIgnoreCase("ita"))
//    		return "ecs.amazonaws.it"; 
    	
    	if (iso != null && iso.equalsIgnoreCase("gbr"))
    		return "ecs.amazonaws.co.uk"; 
    	
    	if (iso != null && iso.equalsIgnoreCase("jpn"))
    		return "ecs.amazonaws.jp";
    	
    	return result;
    }
    
    private String getAccessKey(){
    	String s3 = "6JIQ";    	
    	String s2 = "6TKIQMCVAG6";
    	
    	return s1 + s2 + s3;    	
    }
   
    
    private String getSecretKey(){
    	String s6 = "19P/Z/FQY";
    	String s7 = "Hu8m+mhps";
    	return s4 + s5 + s6 + s7;
    }
    
	public CoverUrls getCover(String metadata) {
//		Log.d("Nexxoo", "AWS: " + metadata);
		if(metadata == null || metadata.length() < 5){
			Log.e("Nexxoo", "Keine Metadaten verfügbar");
			return null;			
		}else{
			/*
	         * Set up the signed requests helper 
	         */
	        SignedRequestsHelper helper;
	        try {
//	        	Log.d("Nexxoo", "Using locale: " + getAmazonEndpoint());
	            helper = SignedRequestsHelper.getInstance(getAmazonEndpoint(), getAccessKey(), getSecretKey());
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	        String requestUrl = null;
	        
	        /*
	         *  Request parameters are stored in a map.
	         */
	        Map<String, String> params2 = new HashMap<String, String>();
	        params2.put("Service", "AWSECommerceService");
	        params2.put("Operation", "ItemSearch");
	        params2.put("Keywords", metadata);
	        params2.put("ResponseGroup", "Images");
	        params2.put("ResponseGroup", "Medium");
	        params2.put("AssociateTag", "nexxoo-20");
	        params2.put("SearchIndex","MP3Downloads");
	
	        requestUrl = helper.sign(params2);
	        
	        //Logger.saveDataToLogFile("AWS requestURL: ;" + requestUrl);
//	        Log.d("Nexxoo", "RequestUrl: " + requestUrl);
	        
	        CoverUrls cu = new CoverUrls();
	        
			try {
				HttpGet uri = new HttpGet(requestUrl); 
				DefaultHttpClient client = new DefaultHttpClient();
		        HttpResponse resp = client.execute(uri);
		        
		        InputStream instream = resp.getEntity().getContent();
		        
		        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		        DocumentBuilder builder = factory.newDocumentBuilder();
		        Document doc = builder.parse(instream);
		        
		        Node items = doc.getFirstChild();
		        
		        NodeList nl = items.getChildNodes();
		        for (int i = 0; i < nl.getLength(); i++) {
					Node item = nl.item(i);				
					if (item != null && item.getNodeName().equals("Items")){
						NodeList itemChilds = item.getChildNodes();					
						for (int j = 0; j < itemChilds.getLength(); j++){
							Node subNode = itemChilds.item(j);
							if (subNode.getNodeName().equals("Item")){
								NodeList imageNodes = subNode.getChildNodes();
								for (int k = 0; k < imageNodes.getLength(); k++) {
									Node imageNode = imageNodes.item(k);
									if (imageNode != null && imageNode.getNodeName().equals(IMAGESIZE_LARGE))
										cu.setCoverLarge(imageNode.getFirstChild().getTextContent());
									if (imageNode != null && imageNode.getNodeName().equals(IMAGESIZE_MEDIUM))
										cu.setCoverMedium(imageNode.getFirstChild().getTextContent());
									if (imageNode != null && imageNode.getNodeName().equals(IMAGESIZE_SMALL))
										cu.setCoverSmall(imageNode.getFirstChild().getTextContent());
									if (imageNode != null && imageNode.getNodeName().equals(DEFAULT_PRODUCTPAGE)){
										cu.setBuyLink(imageNode.getFirstChild().getTextContent());
//										Log.d("nexxoo2", "Buylink: " + cu.getBuyLink());
									}
								}
								break;
							}
						}
					}
				}

			} catch (ClientProtocolException e) {
				Log.d("nexxoo2", "ClientProtocolException: " + e.getMessage());
			} catch (IOException e) {
				Log.d("nexxoo2", "IOException: " + e.getMessage());
			} catch (ParserConfigurationException e) {
				Log.d("nexxoo2", "ParserConfigurationException: " + e.getMessage());
			} catch (SAXException e) {
				Log.d("nexxoo2", "SAXException: " + e.getMessage());
			}
			
			return cu;
		}
	}
}