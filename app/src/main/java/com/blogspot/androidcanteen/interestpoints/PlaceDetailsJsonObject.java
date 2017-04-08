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
    private String key_opening_hours = "opening_hours";
    private String key_open_now = "open_now";
    private String key_weekday_text = "weekday_text";


    public String rating;
    public  String phone;
    public String formattedAddress ;
    public String website ;
    public boolean openNow;
    public String openingHours = "";

    JSONArray allTypes;
    String[] timesDay;
    String days = "";


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


            JSONObject opening = result.getJSONObject(key_opening_hours);

            openNow = opening.getBoolean(key_open_now);



            JSONArray times = opening.getJSONArray(key_weekday_text);

            timesDay = new String[7];


            for(int i=0; i<7;i++) {
                timesDay[i] = (String) times.get(i);

                int index = timesDay[i].indexOf('y') +1;
                days += timesDay[i].substring(0,index) + ":";


               int indexToCut =  timesDay[i].indexOf("y: ") + 3;



                timesDay[i] = timesDay[i].substring(indexToCut,timesDay[i].length());
                openingHours+= timesDay[i];
               // GlobalVariables.LogWithTag("String " + timesDay[i]);
                if(i!=6) {
                    openingHours += "\n\n";
                    days += "\n\n";
                }
           //     GlobalVariables.LogWithTag("Time " + i + " -> " + timesDay[i]);
            }








        } catch (JSONException e) {
            GlobalVariables.LogWithTag("Caught something: " + e.getMessage());
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
