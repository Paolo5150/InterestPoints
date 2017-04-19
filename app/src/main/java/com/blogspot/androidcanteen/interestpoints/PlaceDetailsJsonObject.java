package com.blogspot.androidcanteen.interestpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Locator;

public class PlaceDetailsJsonObject
{
    private String key_icon = "icon";
    private String key_phone = "international_phone_number";
    private String key_rating = "rating";
    private String key_name = "name";
    private String key_result = "result";
    private String key_status = "status";
    private String key_form_address = "formatted_address";
    private String key_website = "website";
    private String key_types = "types";
    private String key_opening_hours = "opening_hours";
    private String key_open_now = "open_now";
    private String key_weekday_text = "weekday_text";
    private String key_photo = "photos";
    private String key_photo_ref = "photo_reference";
    private String key_reviews = "reviews";


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

    JSONArray reviews;
    ReviewObject[] allReviews;


    public PlaceDetailsJsonObject()
    {
    }

    public boolean CreateJsonObject(String taskResult)
    {
        JSONObject obj = null;
        JSONObject result = null;
        JSONObject status = null;

        try {
            obj = new JSONObject(taskResult);
        } catch (JSONException e) {
            GlobalVariables.LogWithTag("Could not create jsonObject");
        }


        try {


            String stat = obj.getString(key_status);

            GlobalVariables.LogWithTag("STATUS: " + stat);



        } catch (JSONException e) {

        }

        try {
            result = obj.getJSONObject(key_result);
        } catch (JSONException e) {
            GlobalVariables.LogWithTag("JSon result exception: " + e.getMessage());
        }

        if(result==null) {

            return false;
        }

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
            rating = "N/A";
        }

        //Photos
        try {
            photosObjs = result.getJSONArray(key_photo);

            GlobalVariables.LogWithTag("PHOTOS: " + photosObjs.length());

            if(photosObjs.length()!=0) {
                photosRefs = new String[photosObjs.length()];

                for (int i = 0; i < photosRefs.length; i++) {
                    JSONObject photo = photosObjs.getJSONObject(i);

                    photosRefs[i] = photo.getString(key_photo_ref);

                }
            }
            else
                photosRefs = null;



        } catch (JSONException e) {
            photosRefs = new String[0];
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

        //Reviews

        try {
            reviews = result.getJSONArray(key_reviews);
          //  GlobalVariables.LogWithTag("Reviews: " + reviews.length());

            allReviews = new ReviewObject[reviews.length()];

            for(int i=0; i<reviews.length();i++)
            {
                allReviews[i] = new ReviewObject(reviews.getJSONObject(i));
            }

        } catch (JSONException e) {
            GlobalVariables.LogWithTag("Reviews exception");
            allReviews = new ReviewObject[0];
            e.printStackTrace();
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

        return true;

    }

    public float getRatingsFloat()
    {

        float f = 0;

        if(!rating.equalsIgnoreCase("N/A"))
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

class ReviewObject
{

    String key_rating = "rating";
    String key_author = "author_name";
    String key_author_url = "author_url";
    String key_time = "time";
    String key_text = "text";


    String rating = "";
    String author= "";
    String author_url= "";
    String text= "";
    String time= "";

    public ReviewObject(JSONObject reviewJSON)
    {
        try {
            rating = reviewJSON.getString(key_rating);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            author = reviewJSON.getString(key_author);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            author_url = reviewJSON.getString(key_author_url);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            time = reviewJSON.getString(key_time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            text = reviewJSON.getString(key_text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public float getRatingFloat()
    {
        float f = 0;

        if(rating!="")
        f = Float.parseFloat(rating);
        return f;
    }

    public String getTimeText()
    {
        long t = Long.parseLong(time);

        String ti = GlobalVariables.Epoch2DateString(t);

        return ti;


    }
}
