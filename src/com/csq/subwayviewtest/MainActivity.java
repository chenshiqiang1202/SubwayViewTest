package com.csq.subwayviewtest;

import android.app.Activity;
import android.os.Bundle;
import com.csq.subwayviewtest.models.Subway;
import com.csq.subwayviewtest.models.SubwayStation;
import com.csq.subwayviewtest.utils.CsqBackgroundTask;
import com.csq.subwayviewtest.views.SubwayView;

public class MainActivity extends Activity {

    private SubwayView subwayView;

    private Subway subway = new Subway();

    private SubwayView.StationSelectListener stationSelectListener = new SubwayView.StationSelectListener() {
        @Override
        public void selectChanged(SubwayStation station) {

        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        subwayView = (SubwayView) findViewById(R.id.subwayView);
        subwayView.setStationSelectListener(stationSelectListener);
	}

    private boolean isFirstResume = true;
    @Override
    protected void onResume() {
        super.onResume();

        if(isFirstResume){
            loadData();
            isFirstResume = false;
        }
    }

    private void loadData(){
        new CsqBackgroundTask<Void>(this){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //showLoading("");
            }

            @Override
            protected Void onRun() {
                subway.loadData("shenzhen.json");
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                //dismissLoading();
            }

            @Override
            protected void onResult(Void result) {
                if(subway.getLines().isEmpty()){
                    //ToastHelper.showInfo("加载失败", false);
                    finish();

                }else{
                    dataLoaded();
                }
            }
        }.execute();

    }

    private void dataLoaded(){
        //居中
        subwayView.setSubway(subway);

        //......
    }
}
