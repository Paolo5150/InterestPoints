package com.blogspot.androidcanteen.interestpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlaceDetailsJsonObject
{
    private String key_icon = "icon";
    private String key_phone = "international_phone_number";
    private String key_rating = "rating";
    private String key_result = "result";
    private String key_form_address = "formatted_address";
    private String key_website = "website";
    private String key_types = "types";


    public String rating;
    public  String phone;
    public String formattedAddress ;
    public String website ;
    JSONArray allTypes;


    public PlaceDetailsJsonObject(String taskResult)
    {
        try {
            JSONObject obj = new JSONObject(taskResult);

            JSONObject result = obj.getJSONObject(key_result);

            rating = result.getString(key_rating);
            phone = result.getString(key_phone);
            formattedAddress = result.getString(key_form_address);
            website = result.getString(key_website);

            allTypes = result.getJSONArray(key_types);







        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public float getRatingsFloat()
    {
        float f = Float.parseFloat(rating);
        return f;
    }

    public String[] typesString()
    {
        String[] res = new String[allTypes.length()];

        for(int i=0; i<res.length;i++)
        {
            try {
                res[i] = allTypes.get(i).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return res;
    }
}
