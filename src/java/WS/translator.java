/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WS;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author carlosrodf
 */
public class translator {
    
    private final String url;

    public translator() {
        this.url = "https://maps.googleapis.com/maps/api/geocode/json";
    }

    public String translate(String latlng) {
        try {
            Client client = Client.create();
            WebResource webResource = client
                    .resource(this.url + "?latlng=" + latlng);

            ClientResponse response = webResource.accept("application/json")
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {
                return latlng;
            }

            JSONObject obj = new JSONObject(response.getEntity(String.class));
            JSONArray arr = obj.getJSONArray("results");
            
            if(arr.length() > 1){
                return arr.getJSONObject(1).getString("formatted_address");
            }else{
                return arr.getJSONObject(0).getString("formatted_address");
            }
        } catch (UniformInterfaceException | ClientHandlerException | JSONException e) {
            return latlng;
        }
    }
}
