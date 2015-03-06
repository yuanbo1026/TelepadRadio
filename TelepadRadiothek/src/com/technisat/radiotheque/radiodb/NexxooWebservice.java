package com.technisat.radiotheque.radiodb;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.technisat.radiotheque.http.post.NexxooHttpJsonRequest;

/**
 * Static class that provides an interface for our webservice to get radio db
 * related information such as genres, languages and of course statons.
 * @author d.fredrich
 *
 */
public class NexxooWebservice {
	
	private static final String URL = "http://www.radioportal1.de/radiolist/php/radiothek/webservice.php";	
	private static final String TOKEN = "";
	
	protected static final int WEBTASK_GETRANDOMSTATION = 0;
	protected static final int WEBTASK_GETGENRELIST = 1;	
	protected static final int WEBTASK_GETSTATIONBYGENRE = 2;
	protected static final int WEBTASK_GETCOUNTRYLIST = 3;
	protected static final int WEBTASK_GETSTATIONBYCOUNTRY = 4;
	protected static final int WEBTASK_GETSTATIONLIST = 5;
	protected static final int WEBTASK_GETSTATIONSFOREACHGENRE = 6;
	protected static final int WEBTASK_GETSTATIONSFOREACHCOUNTRY = 7;
	protected static final int WEBTASK_SEARCHSTATION = 8;
	protected static final int WEBTASK_GETMORESTATIONSOFGENRE = 9;
	protected static final int WEBTASK_SETSTATIONLOGO = 10;
	protected static final int WEBTASK_TRACKDEVICESTARTS = 11;
	
	private static String doWebCall(int task, List<NameValuePair> additionalParams){
		NexxooHttpJsonRequest request = new NexxooHttpJsonRequest();
		return request.performJsonRequest(URL, task, TOKEN, additionalParams);
	}
	
	/**
	 * Returns stations randomly chosen stations.
	 * @param count Number of stations we want to have. My get less.
	 * @return Number of stations in JSON formatted String.
	 */
	public static String getRandomStations(int count){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("count", Integer.toString(count)));
		return doWebCall(WEBTASK_GETRANDOMSTATION, params);
	}
	
	/**
	 * Returns the Genres the db knows.
	 * @param lang Language code (ISO) in which language the Genres shall be.
	 * @return Genre list as JSON formatted String.
	 */
	public static String getGenreList(String lang){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("lang", lang));
		return doWebCall(WEBTASK_GETGENRELIST, params);
	}
	
	/**
	 * Returns stations that match the given Genre.
	 * @param genreId The genre that has to be matched.
	 * @param count Number of stations we want to have.
	 * @return JSON formatted String with up to requested number of stations.
	 */
	public static String getStationsByGenre(Long genreId, int count){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("genreId", Long.toString(genreId)));
		params.add(new BasicNameValuePair("count", Integer.toString(count)));
		return doWebCall(WEBTASK_GETSTATIONBYGENRE, params);
	}
	
	/**
	 * Returns the Country list of the db.
	 * @return JSON formatted String with all countries the db knows.
	 */
	public static String getCountryList(){
		return doWebCall(WEBTASK_GETCOUNTRYLIST, null);
	}
		
	/**
	 * Returns up to the wanted number stations that match the given country.
	 * @param langIso Country code of the station origin we want to have.
	 * @param count Number of stations we want to have.
	 * @return JSON formatted String with up to the requested number of stations.
	 */
	public static String getStationsByCountry(String langIso, int count){		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("lang", langIso));
		params.add(new BasicNameValuePair("count", Integer.toString(count)));
		return doWebCall(WEBTASK_GETSTATIONBYCOUNTRY, params);
	}
	
	/**
	 * Returns up to <code>count</code> stations per genre.
	 * @param langIso The language the genres should be named in.
	 * @param count The number of stations we want per genre.
	 * @return JSON formatted String with up to the requested number of stations per genre.
	 */
	public static String getStationsForAllGenres(String langIso, int count){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("lang", langIso));
		params.add(new BasicNameValuePair("count", Integer.toString(count)));
		return doWebCall(WEBTASK_GETSTATIONSFOREACHGENRE, params);
	}
	
	/**
	 * Returns up to <code>count</code> stations per country.
	 * @param langIso The language the genres should be named in.
	 * @param count The number of stations we want per country.
	 * @return JSON formatted String with up to the requested number of stations per country.
	 */
	public static String getStationsForAllCountries(String langIso, int count){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("lang", langIso));
		params.add(new BasicNameValuePair("count", Integer.toString(count)));
		return doWebCall(WEBTASK_GETSTATIONSFOREACHCOUNTRY, params);
	}
	
	/**
	 * Returns up to <code>until</code> stations that match <code>query</code> as 
	 * <code>LIKE %query%</code>.
	 * @param query The query string.
	 * @param start Where to start.
	 * @param until Up to what match we want the stations.
	 * @return JSON formatted String with up to the requested number of stations.
	 */
	public static String getStationsBySearchQuery(String query, int start, int until){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("query", query));
		params.add(new BasicNameValuePair("start", Integer.toString(start)));
		params.add(new BasicNameValuePair("end", Integer.toString(until)));
		return doWebCall(WEBTASK_SEARCHSTATION, params);
	}
	
	/**
	 * Returns up to 20 stations that match the given query.
	 * Convenience method for {@link #getStationsBySearchQuery(String, int, int)}
	 * @param query The query string.
	 * @return JSON formatted String with up to 20 stations.
	 */
	public static String getStationsBySearchQuery(String query){
		return getStationsBySearchQuery(query, 0, 20);
	}
	
	/**
	 * Returns more station of the same genre as the given station id is.
	 * @param stationId The station id of the station you want to have similiar stations of.
	 * @param count The maximal number of stations you want to get. May be less.
	 * @return JSON formatted String with up to <code>count</code> stations.
	 */
	public static String getMoreStationsOfSameGenre(long stationId, int count){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("count", Integer.toString(count)));
		params.add(new BasicNameValuePair("stationId", Long.toString(stationId)));
		return doWebCall(WEBTASK_GETMORESTATIONSOFGENRE, params);
	}
	
	/**
	 * Tracks the app start for the TechniSat Pre-Installed version of the app.
	 * @param deviceId The device id, hashed and salted.
	 * @return JSON formatted String with result
	 */
	public static String trackAppStart(String deviceId){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("device", deviceId));
		return doWebCall(WEBTASK_TRACKDEVICESTARTS, params);
	}

}
