package com.example.movelocationmarker;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

/**
 * <br>
 * <p>
 * <p>
 * com.example.movelocationmarker
 * <p>
 * FUXI
 */
public class LocationOverlay {
    public static final String TAG = LocationOverlay.class.getSimpleName();
    private Context mContext;
    private AMap mAMap;
    private float radius = 30.0f;
    private Marker locMarker;
    private Circle locCircle;
    private LatLng point;
    private float mBearing;
    private float mSpeed;
    private boolean mIs2DNorth;
    private boolean mIs3DCar;
    private boolean mIs2DCar;
    private float mRotate;


    public LocationOverlay(AMap aMap, Context context) {
        this.mAMap = aMap;
        this.mContext = context;
    }

    private void addLocationIcon() {
        if (locMarker == null) {
            addMarker();
        }
        if (locCircle == null) {
            addCircle();
        }
    }

    /**
     * 添加定位marker
     */
    private void addMarker() {
        BitmapDescriptor des = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_location);
        locMarker = mAMap.addMarker(new MarkerOptions().position(point).icon(des).draggable(false)
                .anchor(0.5f, 0.5f));
    }


    /**
     * 添加定位精度圈
     */
    private void addCircle() {
        locCircle = mAMap.addCircle(new CircleOptions().center(point).radius(radius)
                .fillColor(Color.parseColor("#17536D93"))
                .strokeColor(Color.parseColor("#4D455874"))
                .strokeWidth(2.0f));
    }


    public void locationChange(LatLng latLng, float bearing) {
        mBearing = bearing;
        point = latLng;
        addLocationIcon();
        if (mIs2DNorth) {
            locMarker.setRotateAngle(mBearing);
        }else {
            mAMap.moveCamera(CameraUpdateFactory.changeBearing(mBearing));
        }
        moveMarker();
        locCircle.setRadius(radius);
    }

    private void moveMarker() {
        LatLng startPoint = locMarker.getPosition();
        LatLng endPoint = point;
//        if (startPoint.latitude == endPoint.latitude && startPoint.longitude == endPoint.longitude) {
//            mRotate = 0;
//        } else {
//        float rotate = getRotate(startPoint, endPoint);
//        float angle = rotate + mAMap.getCameraPosition().bearing;
//        float rotate = (float) getAngle(startPoint.latitude, startPoint.longitude,
//                endPoint.latitude,
//                endPoint.longitude);
//            float angle = 360 - rotate + 90;
//            Log.e(TAG, "angle is " + angle + ",rotate is " + rotate);
            mRotate = (float) getAngle1(startPoint.latitude, startPoint.longitude,
                    endPoint.latitude,
                    endPoint.longitude);
            Log.e(TAG, ",rotate is " + mRotate);
//        }

        CameraPosition cameraPosition = mAMap.getCameraPosition();
        if (cameraPosition == null) {
            return;
        }
        float zoom = cameraPosition.zoom;
        float tilt = cameraPosition.tilt;
        float bearing = cameraPosition.bearing;
        Log.e(TAG, "current cameraPosition is " + cameraPosition.toString());
        if (mIs2DCar || mIs3DCar) {
            if (mIs2DCar) {
                CameraUtil.moveMapCamera(mAMap, endPoint.latitude, endPoint.longitude, zoom, 0,
                        -mRotate);
            } else {
                CameraUtil.moveMapCamera(mAMap, endPoint.latitude, endPoint.longitude, zoom, 60,
                        -mRotate);
            }
        } else {
            //获取 Marker覆盖物的图片旋转角度，从正北开始，逆时针计算。
            locMarker.setRotateAngle(mRotate);
            CameraUtil.moveMapCamera(mAMap, endPoint.latitude, endPoint.longitude, zoom, 0, 0);
        }
//        将小蓝点提取到屏幕上
        ValueAnimator anim = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);

        anim.addUpdateListener(valueAnimator -> {
            LatLng target = (LatLng) valueAnimator.getAnimatedValue();
            if (locCircle != null) {
                locCircle.setCenter(target);
            }
            if (locMarker != null) {
                locMarker.setPosition(target);
            }
        });
        anim.setDuration(1800);
        anim.start();
    }


    public void set3D2D(boolean is2DNorth, boolean is3DCar, boolean is2DCar) {
        this.mIs2DNorth = is2DCar;
        this.mIs3DCar = is3DCar;
        this.mIs2DCar = is2DCar;
    }


    public void removeFromMap() {
        if (locMarker != null) {
            locMarker.remove();
        }
        if (locCircle != null) {
            locCircle.remove();
        }
    }

    public class PointEvaluator implements TypeEvaluator {
        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            LatLng startPoint = (LatLng) startValue;
            LatLng endPoint = (LatLng) endValue;
            double x = startPoint.latitude + fraction * (endPoint.latitude - startPoint.latitude);
            double y =
                    startPoint.longitude + fraction * (endPoint.longitude - startPoint.longitude);
            return new LatLng(x, y);
        }
    }


    /**
     * 根据经纬度计算需要偏转的角度
     *
     * @param curPos
     * @param nextPos
     * @return
     */
    private float getRotate(LatLng curPos, LatLng nextPos) {
        if (curPos == null || nextPos == null) {
            return 0;
        }
        double x1 = curPos.latitude;
        double x2 = nextPos.latitude;
        double y1 = curPos.longitude;
        double y2 = nextPos.longitude;

        return (float) (Math.atan2(y2 - y1, x2 - x1) / Math.PI * 180);
    }


    // 计算经纬度旋转角度
    private double getAngle(double lng1, double lat1, double lng2, double lat2) {
        double dRotateAngle = (float) Math.atan2(Math.abs(lng2 - lng1), Math.abs(lat2 - lat1));
        if (lng2 >= lng1) {
            if (lat2 > lat1) {
                dRotateAngle = 2 * Math.PI - dRotateAngle;
            }
        } else {
            if (lat2 >= lat1) {
                dRotateAngle = Math.PI + dRotateAngle;
            } else {
                dRotateAngle = Math.PI - dRotateAngle;
            }
        }
        dRotateAngle = dRotateAngle * 180 / Math.PI;
        return dRotateAngle;
    }

    private double getAngle1(double lat_a, double lng_a, double lat_b, double lng_b) {

        double y = Math.sin(lng_b - lng_a) * Math.cos(lat_b);
        double x =
                Math.cos(lat_a) * Math.sin(lat_b) - Math.sin(lat_a) * Math.cos(lat_b) * Math.cos(lng_b - lng_a);
        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        if (brng < 0)
            brng = brng + 360;
        return brng;

    }
}
