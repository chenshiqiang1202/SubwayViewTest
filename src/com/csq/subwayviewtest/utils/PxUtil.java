/**
 * description : 像素转换工具
 * Created by csq E-mail:csqwyyx@163.com
 * 2014/9/8
 * Created with IntelliJ IDEA
 */
package com.csq.subwayviewtest.utils;

import com.csq.subwayviewtest.App;

public class PxUtil {
	
	public static int dip2px(float dipValue) {
		final float scale = 
				App.instance.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(float pxValue) {
		final float scale = 
				App.instance.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
