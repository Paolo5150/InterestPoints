package com.blogspot.androidcanteen.interestpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlaceDetailsJsonObject
{
    private String key_icon = "icon";
    private String key_phone = "international_phone_number";
    private String key_rating = "rating";
    private String key_name = "name";
    private String key_result = "result";
    private String key_form_address = "formatted_address";
    private String key_website = "website";
    private String key_types = "types";
    private String key_opening_hours = "opening_hours";
    private String key_open_now = "open_now";
    private String key_weekday_text = "weekday_text";
    private String key_photo = "photos";
    private String key_photo_ref = "photo_reference";


    public String rating;
    public String name;
    public  String phone;
    public String formattedAddress ;
    public String website ;
    public boolean openNow;
    public String openingHours = "";
    public String types = "";

    JSONArray allTypes;
    String[] timesDay;
    String days = "";

    JSONArray photosObjs;
    public String[] photosRefs;


    public PlaceDetailsJsonObject(String taskResult)
    {

        JSONObject obj = null;
                JSONObject result = null;
        try {
            obj = new JSONObject(taskResult);
            result = obj.getJSONObject(key_result);

        } catch (JSONException e) {
            GlobalVariables.LogWithTag("JSon result exception");
        }

        if(result==null)
            GlobalVariables.LogWithTag("Result is null");

        //Name
        try {
            name = result.getString(key_name);
        } catch (JSONException e) {
            GlobalVariables.LogWithTag("Name exception");
            name = "Not available";
        }

        //Ratings
        try {
            rating = result.getString(key_rating);
        } catch (JSONException e) {
            GlobalVariables.LogWithTag("Ratings exception");
            rating = "Not available";
        }

        //Photos
        try {
            photosObjs = result.getJSONArray(key_photo);

            GlobalVariables.LogWithTag("PHOTOS: " + photosObjs.length());

            photosRefs = new String[photosObjs.length()];

            for(int i=0; i<photosRefs.length;i++)
            {
              JSONObject photo = photosObjs.getJSONObject(i);

              photosRefs[i] = photo.getString(key_photo_ref);

            }



        } catch (JSONException e) {
            GlobalVariables.LogWithTag("Photos exception:" + e.getMessage());
        }

        //Phone number
        try {
            phone = result.getString(key_phone);
        } catch (JSONException e) {
            GlobalVariables.LogWithTag("Phone number exception");
            phone = "Not available";
        }

        //Address
        try {
            formattedAddress = result.getString(key_form_address);
        } catch (JSONException e) {
            GlobalVariables.LogWithTag("Address exception");
            formattedAddress = "Not available";
        }

        //Website
        try {
            website = result.getString(key_website);
        } catch (JSONException e) {
            GlobalVariables.LogWithTag("Website exception");
            website = "Not available";
        }

        //Type
        try {
            allTypes = result.getJSONArray(key_types);

            types = (String) allTypes.getString(0);


            String holder = types.substring(1,types.length());

            types = types.toUpperCase();

            types = types.charAt(0) + holder;
          /*  for(int i=0; i< allTypes.length();i++)
            {
                types+= (String) allTypes.getString(i);

                if(i!= allTypes.length()-1)
                    types+=", ";
            }*/

        } catch (JSONException e) {
            GlobalVariables.LogWithTag("Type exception");
        }

    //Open now
        JSONObject opening = null;
        try {
            opening = result.getJSONObject(key_opening_hours);
            openNow = opening.getBoolean(key_open_now);


            //Times
            JSONArray times = null;

            times = opening.getJSONArray(key_weekday_text);

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
                // GlobalVariables.LogWithTag("Time " + i + " -> " + timesDay[i]);
            }



        } catch (JSONException e) {
            GlobalVariables.LogWithTag("Open now exception");

            timesDay = new String[7];
            for(int i=0; i<7;i++) {
                timesDay[i] = "Not available";
            }
        }




    }

    public float getRatingsFloat()
    {

        float f = 0;

        if(!rating.equalsIgnoreCase("Not available"))
        f = Float.parseFloat(rating);

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
