package com.technisat.radiotheque.radiodb;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.technisat.radiotheque.entity.Station;

public class JSONParser {

	public static List<Station> parseJsonToStationList(String json){
		List<Station> sList = new ArrayList<Station>();
		if (json != null){
			try {
				JSONObject jsonObj = new JSONObject(json);
				int count = jsonObj.getInt("count");
									
				String stationName = null;
				JSONObject stationObj;				
				Station station = null;
				String url = null;
				long id = -1;
				long langId = -1;
				String stationLogoLarge = null;
				String stationLogoMedium = null;
				String stationLogoSmall = null;
				
				for(int i = 0; i < count; i++){						
					stationObj = jsonObj.getJSONObject(Integer.toString(i));
					
					url = stationObj.getString("url");
					stationName = stationObj.getString("name");
					id = stationObj.getLong("id");
					stationLogoLarge =  stationObj.getString("logoLarge");
					stationLogoMedium =  stationObj.getString("logoMedium");
					stationLogoSmall =  stationObj.getString("logoSmall");
					
					if (stationLogoLarge != null && stationLogoLarge.equals(""))
						stationLogoLarge = null;
					if (stationLogoMedium != null && stationLogoMedium.equals(""))
						stationLogoMedium = null;
					if (stationLogoSmall != null && stationLogoSmall.equals(""))
						stationLogoSmall = null;
					
					langId = stationObj.getLong("langId");
					
					station = new Station(id, url, stationName);
					station.setStationLogoLarge(stationLogoLarge);
					station.setStationLogoMedium(stationLogoMedium);
					station.setStationLogoSmall(stationLogoSmall);
					
					station.setLanguageId(langId);					
					sList.add(station);
				}
			} catch (JSONException e) {
				//Log.e("Nexxoo","error:"+e.getLocalizedMessage());
			}
		}
		return sList;
			
	}
	
	public static List<String> parseJsonToStationListRandom(String json){
		List<String> sList = new ArrayList<String>();
		if (json != null){
			try {
				JSONObject jsonObj = new JSONObject(json);
				int count = jsonObj.getInt("count");
				
				for(int i = 0; i < count; i++){						
				
					sList.add(jsonObj.getString(Integer.toString(i)));
				}
			} catch (JSONException e) {
				//Log.e("Nexxoo","error:"+e.getLocalizedMessage());
			}
		}
		return sList;
			
	}
	
}
