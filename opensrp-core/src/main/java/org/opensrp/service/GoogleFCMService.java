package org.opensrp.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleFCMService {

	public void SendPushNotification(JSONObject msg, JSONArray to,boolean toFacilityUsers){
		try {
			String androidFcmKey;
			if(toFacilityUsers) {
				//Facility client app FCM server key
				androidFcmKey = "AAAATCjcWhU:APA91bEit29ckqT15O-xmdEwHi2B0t6aP87qC7blWbJ1PEmC0pZ1q7h6dN6EIcwY2QWKVlnZd9fnriXuFNW4z7_8alawRPDyrUWSnBG_oS5ri4PBkAbb6vRyZCt8d56crY7Az3LUiM_1";
			}else{
				//CHW app FCM server key

				//staging server key
//				androidFcmKey = "AAAA1dpcyn4:APA91bFMMs4j70dcm9G914cSxEGnVPMREOb9eqwqPYFq4mX6Bqp-GpiIBPGLcJUS0Ux1DzukFCdRQQe6_gcIzdlkObQw9jOeO681QYYnpoCfeTTVou17tG7dv83hiT38zBXOkKcpQw9D";

				//Live server key
				androidFcmKey = "AAAAwVxsHOQ:APA91bFFGOI2MZbKmQynkq-BPjG01_3c6GSQwPcO2DpzFUfUnZT4T3bgU7LuylfQmc2pDQeyyv0H3H9rehuM051mdc1OI7ycCt8k4IQ8_H26c00JkVmsqFjlVGvETi1bv2PEWiD4Ty0r";

			}
			String androidFcmUrl="https://fcm.googleapis.com/fcm/send";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.set("Authorization", "key=" + androidFcmKey);
			httpHeaders.set("Content-Type", "application/json");

			JSONObject json = new JSONObject();

			json.put("data", msg);
			json.put("registration_ids", to);

			System.out.println("FCM Data:"+json.toString());

			HttpEntity<String> httpEntity = new HttpEntity<String>(json.toString(),httpHeaders);
			String response = restTemplate.postForObject(androidFcmUrl,httpEntity,String.class);
			System.out.println("FCM:"+response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
