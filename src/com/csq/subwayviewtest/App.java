/**
 * description :
 * Created by csq E-mail:csqwyyx@163.com
 * 2014/9/8
 * Created with IntelliJ IDEA
 */

package com.csq.subwayviewtest;

import android.app.Application;

public class App extends Application {

    // ------------------------ Constants ------------------------


    // ------------------------- Fields --------------------------

    public static App instance = null;


    // ----------------------- Constructors ----------------------


    // -------- Methods for/from SuperClass/Interfaces -----------

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }


    // --------------------- Methods public ----------------------


    // --------------------- Methods private ---------------------


    // --------------------- Getter & Setter -----------------


    // --------------- Inner and Anonymous Classes ---------------


}
