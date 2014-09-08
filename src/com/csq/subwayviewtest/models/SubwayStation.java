/**
 * description : 
 * Created by csq E-mail:csqwyyx@163.com
 * 2014/8/24.
 * Created with IntelliJ IDEA.
 */

package com.csq.subwayviewtest.models;

import android.graphics.Rect;
import org.json.JSONObject;

public class SubwayStation {

    // ------------------------ Constants ------------------------


    // ------------------------- Fields --------------------------

    public String stationName;
    public String stationId;
    public String crossLine;
    public double lon, lat;
    public String textPosition, textOrientation;
    public int x, y;

    public Rect clickRect;

    // ----------------------- Constructors ----------------------


    // -------- Methods for/from SuperClass/Interfaces -----------

    @Override
    public String toString() {
        return "SubwayStation{" +
                "stationName='" + stationName + '\'' +
                ", stationId='" + stationId + '\'' +
                ", crossLine='" + crossLine + '\'' +
                ", lon=" + lon +
                ", lat=" + lat +
                ", textPosition='" + textPosition + '\'' +
                ", textOrientation='" + textOrientation + '\'' +
                '}';
    }


    // --------------------- Methods public ----------------------

    public static SubwayStation parse(JSONObject data) {
        try {
            SubwayStation ret = new SubwayStation();

            ret.stationName = data.getString("stationName");
            if(ret.stationName.endsWith("站")){
                ret.stationName = ret.stationName.substring(0, ret.stationName.length()-1);
            }
            ret.stationId = data.getString("stationId");
            ret.lat = data.getDouble("lat");
            ret.lon = data.getDouble("lon");

            ret.crossLine = data.optString("crossLine");
            ret.textPosition = data.optString("textPosition");
            ret.textOrientation = data.optString("textOrientation");
            ret.x = data.optInt("x");
            ret.y = data.optInt("y");

            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // --------------------- Methods private ---------------------


    // --------------------- Getter &amp; Setter -----------------


    // --------------- Inner and Anonymous Classes ---------------


}
