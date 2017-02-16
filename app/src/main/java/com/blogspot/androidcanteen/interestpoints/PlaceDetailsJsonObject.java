package com.blogspot.androidcanteen.interestpoints;

import org.json.JSONException;
import org.json.JSONObject;

public class PlaceDetailsJsonObject
{
    private String key_icon = "icon";
    private String key_phone = "international_phone_number";
    private String key_rating = "rating";
    private String key_result = "result";
    private String key_form_address = "formatted_address";


    public String rating;
    public  String phone;
    public String formattedAddress ;

    public PlaceDetailsJsonObject(String taskResult)
    {
        try {
            JSONObject obj = new JSONObject(taskResult);

            JSONObject result = obj.getJSONObject(key_result);

            rating = result.getString(key_rating);
            phone = result.getString(key_phone);
            formattedAddress = result.getString(key_form_address);




        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
