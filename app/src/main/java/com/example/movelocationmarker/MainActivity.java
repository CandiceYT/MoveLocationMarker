package com.example.movelocationmarker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    private ImageView ivCarMode;
    private ImageView ivNorthMode;
    private ImageView iv3dMode;
    private ImageView ivZoomIn;
    private ImageView ivZoomOut;
    private MapView mapView;
    private AMap mAMap;
    private boolean is2DCar = false;
    private boolean is3DCar = false;
    private boolean is2DNorth = true;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private LocationOverlay mLocationOverlay;
    private MyLocationListener mAMapLocationListener;
    private double mLatitude;
    private double mLongitude;
    private float mBearing;
    private static final float BEARING = -45.0f;
    private boolean isFirst = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLocation();
        ivCarMode = findViewById(R.id.iv_car_mode);
        ivNorthMode = findViewById(R.id.iv_north_mode);
        iv3dMode = findViewById(R.id.iv_3d_mode);
        ivZoomIn = findViewById(R.id.iv_zoom_in);
        ivZoomOut = findViewById(R.id.iv_zoom_out);
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        initMap();
        setListener();
    }

    private void setListener() {
        ivCarMode.setOnClickListener(this);
        ivNorthMode.setOnClickListener(this);
        iv3dMode.setOnClickListener(this);
        ivZoomIn.setOnClickListener(this);
        ivZoomOut.setOnClickListener(this);
        mAMap.setOnMapTouchListener(motionEvent -> {
            useMoveToLocationWithMapMode = true;
            setUserTouch(useMoveToLocationWithMapMode);
        });
        mAMap.setOnMapClickListener(latLng -> {
            if (mLocationOverlay != null) {
//                mLocationOverlay.locationChange(latLng, mBearing);
            }
        });
    }

    private void setUserTouch(boolean useMoveToLocationWithMapMode) {
        if (mLocationOverlay != null) {
            mLocationOverlay.setUserMoveToLocationWithMode(useMoveToLocationWithMapMode);
        }
    }


    private void initMap() {
        if (mAMap == null) {
            mAMap = mapView.getMap();
        }
        mAMap.setMyLocationEnabled(true);
        LocationUtil.setTrafficStyle(mAMap, true);
        LocationUtil.setSetting(mAMap);
        mLocationOverlay = new LocationOverlay(mAMap, this);
        mLocationOverlay.set3D2D(is2DNorth, is3DCar, is2DCar);
    }

    private void initLocation() {
        mLocationClient = new AMapLocationClient(this);// 初始化定位
        mAMapLocationListener = new MyLocationListener();
        mLocationClient.setLocationListener(mAMapLocationListener);// 设置定位回调监听
        mLocationOption = new AMapLocationClientOption(); // 初始化定位参数
        // 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        // 设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        // 设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        // 设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        // 设置是否使用设备传感器 默认值：false 不使用设备传感器
        mLocationOption.setSensorEnable(true);
        mLocationOption.setLocationCacheEnable(true);
        // 设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(3000);
        mLocationClient.setLocationOption(mLocationOption);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_car_mode:
                is2DCar = true;
                is3DCar = false;
                is2DNorth = false;
                useMoveToLocationWithMapMode = true;
                CameraUtil.moveMapCamera(mAMap, mLatitude, mLongitude, 17, 0, mBearing);
                useMoveToLocationWithMapMode = false;
                mLocationOverlay.set3D2D(is2DNorth, is3DCar, is2DCar);
                mLocationOverlay.locationChange(new LatLng(mLatitude,mLongitude),mBearing);
                break;
            case R.id.iv_north_mode:
                is2DCar = false;
                is3DCar = false;
                is2DNorth = true;
                useMoveToLocationWithMapMode = true;
                CameraUtil.moveMapCamera(mAMap, mLatitude, mLongitude, 17, 0);
                useMoveToLocationWithMapMode = false;
                mLocationOverlay.set3D2D(is2DNorth, is3DCar, is2DCar);
                mLocationOverlay.locationChange(new LatLng(mLatitude,mLongitude),mBearing);
                break;
            case R.id.iv_3d_mode:
                is2DCar = false;
                is3DCar = true;
                is2DNorth = false;
                useMoveToLocationWithMapMode = true;
                CameraUtil.moveMapCamera(mAMap, mLatitude, mLongitude, 19, 60, mBearing);
                useMoveToLocationWithMapMode = false;
                mLocationOverlay.set3D2D(is2DNorth, is3DCar, is2DCar);
                mLocationOverlay.locationChange(new LatLng(mLatitude,mLongitude),mBearing);
                break;
            case R.id.iv_zoom_in:
                useMoveToLocationWithMapMode = true;
                LocationUtil.zoomIn(mAMap);
                useMoveToLocationWithMapMode = false;
                break;
            case R.id.iv_zoom_out:
                useMoveToLocationWithMapMode = true;
                LocationUtil.zoomOut(mAMap);
                useMoveToLocationWithMapMode = false;
                break;
            default:
                break;
        }
        setUserTouch(useMoveToLocationWithMapMode);
    }


    @Override
    protected void onResume() {
        super.onResume();
        PermissionUtil.checkPermission();
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
        if (mapView != null) {
            mapView.onPause();
        }
        isFirst = false;
        if (mLocationOverlay != null) {
            mLocationOverlay.removeFromMap();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            mLocationClient = null;
            mLocationOption = null;
        }
    }

    //用户操作放大缩小 2d 3d 切换 当前位置按钮 touch 地图 打断地图的操作
    private boolean useMoveToLocationWithMapMode = false;

    private class MyLocationListener implements AMapLocationListener {


        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                mBearing = aMapLocation.getBearing();
                Log.e(TAG, "mBearing is " + mBearing);
                Log.e(TAG, "aMapLocation===" + aMapLocation.toString());
                if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
                    mLatitude = aMapLocation.getLatitude();
                    mLongitude = aMapLocation.getLongitude();
                    if (!isFirst) {
                        isFirst = true;
                        mLocationOverlay.addLocationIcon(mLatitude, mLongitude);
                        CameraUtil.setCenterForMap(mAMap, mLatitude, mLongitude, 17);
                    }
//                    else {
//                        if (useMoveToLocationWithMapMode) {
//                            //二次以后定位，使用sdk中没有的模式，让地图和小蓝点一起移动到中心点（类似导航锁车时的效果）
//                            mLocationOverlay.startMoveLocationAndMap(new LatLng(mLatitude,
//                                    mLongitude));
//                        } else {
//                            mLocationOverlay.startChangeLocation(new LatLng(mLatitude,
//                            mLongitude));
//                        }
//                    }
                    mLocationOverlay.locationChange(new LatLng(mLatitude, mLongitude), mBearing);
                }
            }
        }
    }


}
