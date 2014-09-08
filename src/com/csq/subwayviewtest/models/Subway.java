/**
 * description : 
 * Created by csq E-mail:csqwyyx@163.com
 * 2014/8/24.
 * Created with IntelliJ IDEA.
 */

package com.csq.subwayviewtest.models;

import android.text.TextUtils;
import com.csq.subwayviewtest.App;
import com.csq.subwayviewtest.utils.RectD;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Subway {

    // ------------------------ Constants ------------------------


    // ------------------------- Fields --------------------------
    public int canvasWidth, canvasHeight;
    private final List<SubwayLine> lines = new ArrayList<SubwayLine>();

    /**
     * key -- lineName
     */
    private final HashMap<String, SubwayLine> lineMap = new HashMap<String, SubwayLine>();

    // ----------------------- Constructors ----------------------


    // -------- Methods for/from SuperClass/Interfaces -----------

    @Override
    public String toString() {
        return "Subway{" +
                "lines=" + lines +
                '}';
    }


    // --------------------- Methods public ----------------------

    /**
     * 加载数据并初始化
     * "shenzhen.json"
     */
    public void loadData(String assetPath){
        InputStream is = null;
        try {
            is = App.instance.getAssets().open(assetPath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sTotalString = new StringBuilder();
            String sCurrentLine = "";
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                sTotalString.append(sCurrentLine);
            }
            String result = sTotalString.toString();

            JSONObject subway = new JSONObject(result);
            canvasWidth = subway.getInt("canvasWidth");
            canvasHeight = subway.getInt("canvasHeight");
            JSONArray ls = subway.getJSONArray("lines");
            for(int i = 0, num = ls.length(); i < num; i++) {
                JSONObject lineObject = ls.getJSONObject(i);
                SubwayLine line = SubwayLine.parse(lineObject);
                if(line != null){
                    lines.add(line);
                    lineMap.put(line.lineName, line);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    // --------------------- Methods private ---------------------


    // --------------------- Getter &amp; Setter -----------------

    public List<SubwayLine> getLines() {
        return lines;
    }

    public SubwayLine getStations(String lineName){
        return lineMap.get(lineName);
    }

    public SubwayStation getSubwayStation(String lineName, String stationName){
        if(TextUtils.isEmpty(lineName)){
            for(SubwayLine line : lineMap.values()){
                for(SubwayStation ss : line.stations){
                    if(ss.stationName.endsWith(stationName)){
                        return ss;
                    }
                }
            }

        }else{
            SubwayLine sl = getStations(lineName);
            if(sl != null){
                for(SubwayStation ss : sl.stations){
                    if(ss.stationName.endsWith(stationName)){
                        return ss;
                    }
                }
            }
        }

        return null;
    }

    public List<SubwayStation> keySearch(String key){
        Map<String, SubwayStation> ms = new LinkedHashMap<String, SubwayStation>();
        if(!TextUtils.isEmpty(key)){
            for(SubwayLine line : lineMap.values()){
                for(SubwayStation ss : line.stations){
                    if(ss.stationName.contains(key)){
                        ms.put(ss.stationName, ss);
                    }
                }
            }
        }
        List<SubwayStation> ss = new ArrayList<SubwayStation>();
        ss.addAll(ms.values());
        return ss;
    }

    /**
     * 获得站点的经纬度范围
     * @return
     */
    public RectD getLatLonSpan(){
        double latMin = 0, latMax = 0, lonMin = 0, lonMax = 0;
        for(SubwayLine line : lines){
            for(SubwayStation s : line.stations){
                if(latMin == 0){
                    latMin = s.lat;
                    latMax = s.lat;
                    lonMin = s.lon;
                    lonMax = s.lon;
                }else{
                    if(latMin > s.lat){
                        latMin = s.lat;
                    }
                    if(latMax < s.lat){
                        latMax = s.lat;
                    }
                    if(lonMin > s.lon){
                        lonMin = s.lon;
                    }
                    if(lonMax < s.lon){
                        lonMax = s.lon;
                    }
                }
            }
        }
        return new RectD(lonMin, latMax, lonMax, latMin);
    }


    private RectD latLonBound;
    /**
     * 获得地图上显示的经纬度范围getLatLonSpan*1.2
     * @return
     */
    public RectD getLatLonBound(){
        if(latLonBound == null){
            RectD i = getLatLonSpan();
            double latCenter = i.centerY();
            double lonCenter = i.centerX();
            double halfLatSpan = i.height() * 0.6d;
            double halfLonSpan = i.width() * 0.6f;

            latLonBound = new RectD(lonCenter - halfLonSpan,
                    latCenter + halfLatSpan,
                    lonCenter + halfLonSpan,
                    latCenter - halfLatSpan);
        }
        return latLonBound;
    }


    // --------------- Inner and Anonymous Classes ---------------


}
