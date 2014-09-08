/**
 * description :
 * Created by csq E-mail:csqwyyx@163.com
 * 2014/9/8
 * Created with IntelliJ IDEA
 */

package com.csq.subwayviewtest.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.TextView;

public class ViewUtils {

    // ------------------------ Constants ------------------------
    private static final int DEFAULT_MIN_TEXT_SIZE = 8;

    // ------------------------- Fields --------------------------


    // ----------------------- Constructors ----------------------


    // -------- Methods for/from SuperClass/Interfaces -----------


    // --------------------- Methods public ----------------------

    /**
     * @description: 获取要画的文字高度，top--ascent--baseline_descent--buttom
     * 经测试，文字竖直中心点距离baseline的距离为此返回值的0.3左右
     * @return 文字实际显示的高度
     */
    public static int getDrawTextHeight(Paint paint){
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent);
    }

    /**
     * @description: 根据文字垂直中心点坐标获取DrawText参数Y值，即再加上中心点距离baseline的距离
     * @author: chenshiqiang E-mail:csqwyyx@163.com
     * @param textCenterY
     * @param paint
     * @return
     */
    public static int getDrawTextY(int textCenterY,Paint paint){
        return (int) (textCenterY + 0.3*getDrawTextHeight(paint));
    }

    /**
     * @description: 获取要画的文字宽度，不准确
     * @return
     */
    public static int getDrawTextWidth(Paint paint,String text){
        return (int) paint.measureText(text);
    }

    /**
     * @description: 文字宽度与context有关
     * @return
     */
    public static float getTextWidth(String text,float textSize,Context context) {
        TextView textView = new TextView(context);
        textView.setTextSize(textSize);
        Paint paint = textView .getPaint();
        return paint.measureText(text);
    }

    /**
     * 靠右绘制文字
     * @param context
     * @param canvas
     * @param textPaint
     * @param text  要绘制的文字
     * @param centerY  文字竖直中点
     * @param right 文字最右边坐标
     */
    public static void drawTextAlignRight(Context context, Canvas canvas, Paint textPaint,
                                          String text, int centerY, int right)
    {
        textPaint.setTextAlign(Paint.Align.RIGHT);

        canvas.drawText(text,
                right,
                getDrawTextY(centerY,textPaint),
                textPaint);
    }

    /**
     * 靠左绘制文字
     * @param context
     * @param canvas
     * @param textPaint
     * @param text  要绘制的文字
     * @param centerY  文字竖直中点
     * @param left 文字最左边坐标
     */
    public static void drawTextAlignLeft(Context context, Canvas canvas, Paint textPaint,
                                         String text, int centerY, int left)
    {
        textPaint.setTextAlign(Paint.Align.LEFT);

        canvas.drawText(text,
                left,
                getDrawTextY(centerY,textPaint),
                textPaint);
    }

    /**
     * 居中绘制文字
     * @param context
     * @param canvas
     * @param textPaint
     * @param text  要绘制的文字
     * @param centerY  文字竖直中点
     */
    public static void drawTextAlignCenter(Context context, Canvas canvas, Paint textPaint,
                                           String text, int centerY, int centerX)
    {
        textPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(text,
                centerX,
                getDrawTextY(centerY,textPaint),
                textPaint);
    }


    // --------------------- Methods private ---------------------


    // --------------------- Getter & Setter -----------------


    // --------------- Inner and Anonymous Classes ---------------


}
