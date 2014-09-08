/**
 * description : 
 * Created by csq E-mail:csqwyyx@163.com
 * 2014/8/24.
 * Created with IntelliJ IDEA.
 */

package com.csq.subwayviewtest.models;

import android.graphics.Color;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SubwayLine {

    // ------------------------ Constants ------------------------


    // ------------------------- Fields --------------------------

    public String lineName;
    public String lineId;
    public int lineColor;
    public final ArrayList<SubwayStation> stations = new ArrayList<SubwayStation>();


    // ----------------------- Constructors ----------------------


    // -------- Methods for/from SuperClass/Interfaces -----------

    @Override
    public String toString() {
        return "SubwayLine{" +
                "lineName='" + lineName + '\'' +
                ", lineId='" + lineId + '\'' +
                ", stations=" + stations +
                '}';
    }


    // --------------------- Methods public ----------------------

    public static SubwayLine parse(JSONObject data) {
        try {
            SubwayLine ret = new SubwayLine();
            ret.lineName = data.getString("lineName");
            ret.lineId = data.getString("lineId");
            ret.lineColor = Color.parseColor("#" + data.getString("lineColor"));

            JSONArray ss = data.getJSONArray("list");
            for(int i = 0, num = ss.length(); i < num; i++) {
                JSONObject sObject = ss.getJSONObject(i);
                SubwayStation s = SubwayStation.parse(sObject);
                if(s != null){
                    ret.stations.add(s);
                }
            }

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
