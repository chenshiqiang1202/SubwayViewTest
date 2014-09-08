/**
 * description :
 * Created by csq E-mail:csqwyyx@163.com
 * 2014/9/8
 * Created with IntelliJ IDEA
 */

package com.csq.subwayviewtest.views;

import android.content.Context;
import android.graphics.*;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import com.csq.subwayviewtest.models.Subway;
import com.csq.subwayviewtest.models.SubwayLine;
import com.csq.subwayviewtest.models.SubwayStation;
import com.csq.subwayviewtest.utils.PxUtil;
import com.csq.subwayviewtest.utils.ViewUtils;

public class SubwayView extends View {

    // ------------------------ Constants ------------------------


    // ------------------------- Fields --------------------------

    /**
     * 交叉站点颜色
     */
    private final int ColorCrossStation = 0xff727272;
    /**
     * 最近站点透明圆环
     */
    private final int ColorNearestStationTrans = 0x8076b1ff;

    /**
     * 原始地铁数据
     */
    private Subway subway;

    private Paint pPath, pStation, pText;

    private int width, height;

    /**
     * 点击选择的站点id
     */
    private String selectedStationId;
    /**
     * 最近的站点id
     */
    private String nearestStationName;
    /**
     * 站点点击范围
     */
    private int stationClickRadius = 30;

    /**
     * 画布宽度
     */
    private int canvasWidth = 0;
    /**
     * 画布高度
     */
    private int canvasHeight = 0;
    /**
     * 文字大小
     */
    private int textSizePx = PxUtil.dip2px(12);
    /**
     * 线宽
     */
    private int lineWidth = PxUtil.dip2px(5);
    /**
     * 文字距坐标的间隔 = 2*lineWidth
     */
    private int textMargin1 = PxUtil.dip2px(10);
    private int textMargin2 = PxUtil.dip2px(10);
    /**
     * 站点外环半径 = 1*lineWidth
     */
    private int rStationOut = PxUtil.dip2px(5);
    /**
     * 站点内环半径 = 0.7*lineWidth
     */
    private int rStationIn = PxUtil.dip2px(3.5f);

    private StationSelectListener stationSelectListener;


    //============================ 缩放 ===============================
    /**
     * 最大缩放级别
     */
    protected static final int MaxScale = 3;
    /**
     * 最小缩放级别
     */
    protected float MinScale = 1.0f;

    /**
     * 相对于中心点的偏移
     */
    private int xOffset, yOffset;
    /**
     * 某一缩放级别下，最大偏移量
     */
    private int minXOffset = 0, maxXOffset = 0, minYOffset = 0, maxYOffset = 0;
    /**
     * 当前缩放级别
     */
    protected float curScale = 1.0f;

    protected GestureDetector mGestureDetector;
    protected GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(curScale <= MinScale || subway == null){
                return false;
            }
            //左下滑动distanceX > 0, distanceY < 0

            xOffset -= distanceX;
            xOffset = checkXOffset(xOffset);

            yOffset -= distanceY;
            yOffset = checkYOffset(yOffset);

            postInvalidate();

            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if(subway != null){
                for(SubwayLine line : subway.getLines()){
                    for(SubwayStation ss : line.stations) {
                        if(ss.clickRect.contains((int) e.getX(), (int) e.getY())){
                            changeSelectedStation(ss);
                            break;
                        }
                    }
                }
            }
            return super.onSingleTapUp(e);
        }
    };

    protected ScaleGestureDetector mScaleDetector;
    protected ScaleGestureDetector.OnScaleGestureListener mScaleListener = new ScaleGestureDetector.SimpleOnScaleGestureListener(){
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale(detector);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            scale(detector);
        }

        private void scale(ScaleGestureDetector detector) {
            float thisScale = curScale * detector.getScaleFactor();
            if(thisScale > MaxScale){
                thisScale = MaxScale;
            }
            if(thisScale < MinScale){
                thisScale = MinScale;
                xOffset = 0;
                yOffset = 0;
            }
            changeScale(thisScale, detector.getFocusX(), detector.getFocusY());
        }
    };

    // ----------------------- Constructors ----------------------

    public SubwayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SubwayView(Context context) {
        super(context);
        initView(context);
    }


    // -------- Methods for/from SuperClass/Interfaces -----------

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.width = w;
        this.height = h;

        initViewProperty();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(subway == null || width < 1 || height < 1){
            return;
        }

        Point lastPoint = new Point();
        Point thisPoint = new Point();
        SubwayStation ss = null;

        //先画下面的线
        for(SubwayLine line : subway.getLines()){
            Path path = new Path();
            for(int i = 0, num = line.stations.size() ; i < num; i++){
                ss = line.stations.get(i);
                getStationPoint(ss, thisPoint);
                if(i == 0){
                    path.moveTo(thisPoint.x, thisPoint.y);
                }else{
                    path.lineTo(thisPoint.x, thisPoint.y);
                }
            }

            pPath.setColor(line.lineColor);
            canvas.drawPath(path, pPath);
        }


        for(SubwayLine line : subway.getLines()){
            for(int i = 0, num = line.stations.size() ; i < num; i++){
                ss = line.stations.get(i);
                getStationPoint(ss, thisPoint);
                lastPoint.set(thisPoint.x, thisPoint.y);

                //圆环
                pStation.setColor(line.lineColor);
                canvas.drawCircle(thisPoint.x, thisPoint.y, rStationOut, pStation);

                if(ss.crossLine.contains(",")){
                    pStation.setColor(ColorCrossStation);
                }else{
                    pStation.setColor(Color.WHITE);
                }
                canvas.drawCircle(thisPoint.x, thisPoint.y, rStationIn, pStation);

                //选择,画红环
                if(!TextUtils.isEmpty(selectedStationId)
                        && ss.stationId.equals(selectedStationId)){
                    pStation.setColor(Color.RED);
                    canvas.drawCircle(thisPoint.x, thisPoint.y, rStationOut*0.5f, pStation);
                }

                //最近的站点，透明圆环
                if(!TextUtils.isEmpty(nearestStationName)
                        && ss.stationName.equals(nearestStationName)){
                    pStation.setColor(ColorNearestStationTrans);
                    canvas.drawCircle(thisPoint.x, thisPoint.y, rStationOut*3f, pStation);
                }

                //文字
                char[] ns = ss.stationName.toCharArray();
                int tlong = ns.length * textSizePx;
                int tHalf = (int) (0.5f*textSizePx);
                //左上
                int tXCenterFrom = 0, tXDis = 0;
                int tYCenterFrom = 0, tYDis = 0;
                if(ss.textPosition.equals("上")){
                    if(ss.textOrientation.equals("水平")){
                        tXDis = textSizePx;
                        tYDis = 0;
                        tXCenterFrom = thisPoint.x - tlong/2 + tHalf;
                        tYCenterFrom = thisPoint.y - textMargin2 - tHalf;

                    }else{
                        tXDis = 0;
                        tYDis = textSizePx;
                        tXCenterFrom = thisPoint.x;
                        tYCenterFrom = thisPoint.y - textMargin2 - tlong + tHalf;
                    }

                }else if(ss.textPosition.equals("下")){
                    if(ss.textOrientation.equals("水平")){
                        tXDis = textSizePx;
                        tYDis = 0;
                        tXCenterFrom = thisPoint.x - tlong/2 + tHalf;
                        tYCenterFrom = thisPoint.y + textMargin2 + tHalf;

                    }else{
                        tXDis = 0;
                        tYDis = textSizePx;
                        tXCenterFrom = thisPoint.x;
                        tYCenterFrom = thisPoint.y + textMargin2 + tHalf;
                    }

                }else if(ss.textPosition.equals("左")){
                    if(ss.textOrientation.equals("水平")){
                        tXDis = textSizePx;
                        tYDis = 0;
                        tXCenterFrom = thisPoint.x - textMargin2 - tlong + tHalf;
                        tYCenterFrom = thisPoint.y;

                    }else{
                        tXDis = 0;
                        tYDis = textSizePx;
                        tXCenterFrom = thisPoint.x - textMargin2 - tHalf;
                        tYCenterFrom = thisPoint.y - tlong/2 + tHalf;
                    }

                }else if(ss.textPosition.equals("右")){
                    if(ss.textOrientation.equals("水平")){
                        tXDis = textSizePx;
                        tYDis = 0;
                        tXCenterFrom = thisPoint.x + textMargin2 + tHalf;
                        tYCenterFrom = thisPoint.y;

                    }else{
                        tXDis = 0;
                        tYDis = textSizePx;
                        tXCenterFrom = thisPoint.x + textMargin2 + tHalf;
                        tYCenterFrom = thisPoint.y - tlong/2 + tHalf;
                    }

                }else if(ss.textPosition.equals("右上")){
                    if(ss.textOrientation.equals("水平")){
                        tXDis = textSizePx;
                        tYDis = 0;
                        tXCenterFrom = thisPoint.x + textMargin1 + tHalf;
                        tYCenterFrom = thisPoint.y - textMargin1 - tHalf;

                    }else{
                        tXDis = 0;
                        tYDis = textSizePx;
                        tXCenterFrom = thisPoint.x + textMargin1 + tHalf;
                        tYCenterFrom = thisPoint.y - textMargin1 - tlong + tHalf;
                    }

                }else if(ss.textPosition.equals("右下")){
                    if(ss.textOrientation.equals("水平")){
                        tXDis = textSizePx;
                        tYDis = 0;
                        tXCenterFrom = thisPoint.x + textMargin1 + tHalf;
                        tYCenterFrom = thisPoint.y + textMargin1 + tHalf;

                    }else{
                        tXDis = 0;
                        tYDis = textSizePx;
                        tXCenterFrom = thisPoint.x + textMargin1 + tHalf;
                        tYCenterFrom = thisPoint.y + textMargin1 + tHalf;
                    }

                }else if(ss.textPosition.equals("左上")){
                    if(ss.textOrientation.equals("水平")){
                        tXDis = textSizePx;
                        tYDis = 0;
                        tXCenterFrom = thisPoint.x - textMargin1 - tlong + tHalf;
                        tYCenterFrom = thisPoint.y - textMargin1 - tHalf;

                    }else{
                        tXDis = 0;
                        tYDis = textSizePx;
                        tXCenterFrom = thisPoint.x - textMargin1 - tHalf;
                        tYCenterFrom = thisPoint.y - textMargin1 -tlong + tHalf;
                    }

                }else{
                    //左下
                    if(ss.textOrientation.equals("水平")){
                        tXDis = textSizePx;
                        tYDis = 0;
                        tXCenterFrom = thisPoint.x - textMargin1 - tlong + tHalf;
                        tYCenterFrom = thisPoint.y + textMargin1 + tHalf;

                    }else{
                        tXDis = 0;
                        tYDis = textSizePx;
                        tXCenterFrom = thisPoint.x - textMargin1 - tHalf;
                        tYCenterFrom = thisPoint.y + textMargin1 + tHalf;
                    }

                }

                for(int x = 0, l = ns.length ; x < l ; x++){
                    char c = ns[x];
                    canvas.drawText(c + "",
                            tXCenterFrom + x * tXDis,
                            ViewUtils.getDrawTextY(tYCenterFrom + x * tYDis, pText),
                            pText);
                }

                //点击范围
                ss.clickRect = new Rect(thisPoint.x - stationClickRadius,
                        thisPoint.y - stationClickRadius,
                        thisPoint.x + stationClickRadius,
                        thisPoint.y + stationClickRadius);
            }
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mScaleDetector.onTouchEvent( event );

        if ( !mScaleDetector.isInProgress() ) {
            //没有缩放
            mGestureDetector.onTouchEvent( event );
        }

        int action = event.getAction();
        switch ( action & MotionEvent.ACTION_MASK ) {
            case MotionEvent.ACTION_UP:

                break;
        }

        return true;
    }

    // --------------------- Methods public ----------------------

    public void changeSelectedStation(SubwayStation station){
        if(station == null){
            this.selectedStationId = null;
            postInvalidate();
            return;
        }
        this.selectedStationId = station.stationId;

        if(stationSelectListener != null){
            stationSelectListener.selectChanged(station);
        }

        postInvalidate();
    }

    // --------------------- Methods private ---------------------

    private void initView(Context context) {
        stationClickRadius = PxUtil.dip2px(16);

        pPath = new Paint();
        pPath.setAntiAlias(true);
        pPath.setStyle(Paint.Style.STROKE);
        pPath.setStrokeCap(Paint.Cap.ROUND);

        pStation = new Paint();
        pStation.setAntiAlias(true);
        pStation.setStyle(Paint.Style.FILL);

        pText = new Paint();
        pText.setAntiAlias(true);
        pText.setColor(Color.BLACK);
        pText.setTextAlign(Paint.Align.CENTER);

        mScaleDetector = new ScaleGestureDetector( getContext(), mScaleListener );
        mGestureDetector = new GestureDetector( getContext(), mGestureListener, null, true );
    }

    private void getStationPoint(SubwayStation station, Point point){
        //相对于中点的偏移
        int dx = (int) ((station.x - subway.canvasWidth*0.5f) * curScale);
        int dy = (int) ((station.y - subway.canvasHeight*0.5f) * curScale);
        point.x = (int) (width * 0.5f + dx + xOffset);
        point.y = (int) (height * 0.5f + dy + yOffset);
    }

    private void initViewProperty(){
        if(subway != null && width > 0 && height > 0){
            curScale = (float)width / subway.canvasWidth;
            MinScale = curScale;
            if(curScale < 1f){
                curScale = 1f;
            }
            scaleChanged();

            xOffset = 0;
            yOffset = 0;
        }
    }

    private void scaleChanged(){

        if(subway != null){
            canvasWidth = (int) (subway.canvasWidth * curScale);
            canvasHeight = (int) (subway.canvasHeight * curScale);
        }

        //文字大小
        textSizePx = (int) (PxUtil.dip2px(8)*curScale);
        if(textSizePx < 5){
            textSizePx = 5;
        }else if(textSizePx > PxUtil.dip2px(12)){
            textSizePx = PxUtil.dip2px(12);
        }
        pText.setTextSize(textSizePx);

        //线宽
        lineWidth = (int) (PxUtil.dip2px(5)*curScale);
        if(lineWidth < 3){
            lineWidth = 3;
        }else if(lineWidth > 9){
            lineWidth = 9;
        }
        pPath.setStrokeWidth(lineWidth);

        //文字距坐标的间隔 = 1.0*lineWidth
        textMargin1 = (int) (0.6f*lineWidth);
        textMargin2 = (int) (1.1f*lineWidth);
        //站点外环半径 = 1*lineWidth
        rStationOut = 1*lineWidth;
        //站点内环半径 = 0.7*lineWidth
        rStationIn = (int) (0.7f*lineWidth);

        if(width >= canvasWidth){
            minXOffset = 0;
            maxXOffset = 0;
        }else{
            minXOffset = (width - canvasWidth)/2;
            maxXOffset = (-width + canvasWidth)/2;
        }

        if(height >= canvasHeight){
            minYOffset = 0;
            maxYOffset = 0;
        }else{
            minYOffset = (height - canvasHeight)/2;
            maxYOffset = (-height + canvasHeight)/2;
        }
    }


    protected void changeScale(float newScale, float focusX, float focusY){
        //1.根据相对于中点的距离与总长度比例相等
        xOffset = (int) ((width*0.5f + xOffset - focusX)*newScale/curScale + focusX - width*0.5f);
        yOffset = (int) ((height*0.5f + yOffset - focusY)*newScale/curScale + focusY - height*0.5f);

        //2.checkXOffset依赖于scaleChanged
        curScale = newScale;
        scaleChanged();

        //3.
        xOffset = checkXOffset(xOffset);
        yOffset = checkYOffset(yOffset);

        postInvalidate();
    }

    private int checkXOffset(int offset){
        if(xOffset < minXOffset){
            //超出右边界
            return minXOffset;
        }
        if(xOffset > maxXOffset){
            //超出左边界
            return maxXOffset;
        }
        return offset;
    }

    private int checkYOffset(int offset){
        if(yOffset < minYOffset){
            //超出右边界
            return minYOffset;
        }
        if(yOffset > maxYOffset){
            //超出左边界
            return maxYOffset;
        }
        return offset;
    }


    // --------------------- Getter & Setter -----------------

    public void setSubway(Subway subway) {
        this.subway = subway;
        initViewProperty();
        postInvalidate();
    }


    public void setNearestStationName(String nearestStationName) {
        this.nearestStationName = nearestStationName;

        postInvalidate();
    }

    public void setStationSelectListener(StationSelectListener stationSelectListener) {
        this.stationSelectListener = stationSelectListener;
    }


    // --------------- Inner and Anonymous Classes ---------------

    public interface StationSelectListener{
        public void selectChanged(SubwayStation station);
    }

}
