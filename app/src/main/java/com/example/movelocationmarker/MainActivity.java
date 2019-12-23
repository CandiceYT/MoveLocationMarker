package com.example.movelocationmarker;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        AMapLocationListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    private boolean isNeedCheck = true;
    private ImageView ivCarMode;
    private ImageView ivNorthMode;
    private ImageView iv3dMode;
    private ImageView ivZoomIn;
    private ImageView ivZoomOut;
    private MapView mapView;
    private AMap mAMap;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private LocOverlay mLocOverlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivCarMode = findViewById(R.id.iv_car_mode);
        ivNorthMode = findViewById(R.id.iv_north_mode);
        iv3dMode = findViewById(R.id.iv_3d_mode);
        ivZoomIn = findViewById(R.id.iv_zoom_in);
        ivZoomOut = findViewById(R.id.iv_zoom_out);
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        initMap();
        ivCarMode.setOnClickListener(this);
        ivNorthMode.setOnClickListener(this);
        iv3dMode.setOnClickListener(this);
        ivZoomIn.setOnClickListener(this);
        ivZoomOut.setOnClickListener(this);
    }

    private void initMap() {
        if (mAMap == null) {
            mAMap = mapView.getMap();
        }
        mAMap.setMyLocationEnabled(true);
//        setLocationStyle();
        setSetting(mAMap);
        mLocOverlay = new LocOverlay(mAMap);
        mAMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                Log.e(TAG, "activate");

            }

            @Override
            public void deactivate() {
                destroyLocation();
            }
        });


    }

    private void startLocation() {
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = getLocationOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);

            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        } else {
            mLocationOption = getLocationOption();
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }
    }

    private void stopLocation() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
        }
    }


    private void destroyLocation() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
        mLocationOption = null;
    }

    private AMapLocationClientOption getLocationOption() {
        AMapLocationClientOption aMapLocationClientOption = new AMapLocationClientOption();
        //设置为高精度定位模式
        aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //是指定位间隔
        aMapLocationClientOption.setInterval(3000);
        return aMapLocationClientOption;
    }

    private LatLng mLatLng;

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            Log.e(TAG, "aMapLocation===" + aMapLocation);
            if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
                double latitude = aMapLocation.getLatitude();
                double longitude = aMapLocation.getLongitude();
                if (mLatLng == null) {
                    //首次定位,选择移动到地图中心点并修改级别到15级
                    mLatLng = new LatLng(latitude, longitude);
                    mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 16));
                }
                if (mLocOverlay != null) {
                    mLocOverlay.locationChanged(aMapLocation);
                }
            }
        }
    }


    private void setSetting(AMap aMap) {
        if (aMap == null) {
            return;
        }
        // 设置为 true 表示启动显示定位蓝点
        // 设置为 false 表示隐藏定位蓝点并不进行定位
        aMap.setMyLocationEnabled(true);
        /* 设置最小缩放级别
         * 在 SDK 地图 zoom 级别 3 和 4（世界地图级别）显示中国以外地区时，
         * 由于没有详细地图数据，显示效果看起来和地图加载失败时一样的黄白色底图，
         * 因此我们把最小缩放级别设置为 5 。
         */
        aMap.setMinZoomLevel(3f);
        UiSettings uiSettings = aMap.getUiSettings();
        // 显示高德自带缩放按钮
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setScaleControlsEnabled(true);
        // 不显示高德自带定位按钮
        uiSettings.setMyLocationButtonEnabled(true);
        // 把高德 Logo 挪到屏幕显示区域外
//        uiSettings.setLogoBottomMargin(-666);
        // 设置地图可以手势滑动
        uiSettings.setScrollGesturesEnabled(true);
        // 设置地图可以手势缩放
        uiSettings.setZoomGesturesEnabled(true);
        // 设置地图可以倾斜
        uiSettings.setTiltGesturesEnabled(true);

        // 设置地图可以旋转
        uiSettings.setRotateGesturesEnabled(true);
        //设置全部手势
//    uiSettings.setAllGesturesEnabled(true)
// 显示高德自带的室内楼层效果
        uiSettings.setIndoorSwitchEnabled(true);

    }

    private void setLocationStyle() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle
        // .myLocationType
        // (MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);
        // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(3000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        mAMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//        设置默认定位按钮是否显示，非必需设置。
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_car_mode:
                break;
            case R.id.iv_north_mode:
                break;
            case R.id.iv_3d_mode:
                break;
            case R.id.iv_zoom_in:
                break;
            case R.id.iv_zoom_out:
                break;
            default:
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        checkPermission();
        startLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        stopLocation();
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
        destroyLocation();
    }

    //    ----------------------------权限检查--------------------------------

    private void checkPermission() {
        try {
            super.onResume();
            if (Build.VERSION.SDK_INT >= 23) {
                if (isNeedCheck) {
                    checkPermissions(needPermissions);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * @param
     * @since 2.5.0
     */
    @TargetApi(23)
    private void checkPermissions(String... permissions) {
        try {
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissonList = findDeniedPermissions(permissions);
                if (null != needRequestPermissonList
                        && needRequestPermissonList.size() > 0) {
                    try {
                        String[] array =
                                needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                        Method method = getClass().getMethod("requestPermissions",
                                new Class[]{String[].class, int.class});
                        method.invoke(this, array, 0);
                    } catch (Throwable e) {

                    }
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    @TargetApi(23)
    private List<String> findDeniedPermissions(String[] permissions) {
        try {
            List<String> needRequestPermissonList = new ArrayList<String>();
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                for (String perm : permissions) {
                    if (checkMySelfPermission(perm) != PackageManager.PERMISSION_GRANTED
                            || shouldShowMyRequestPermissionRationale(perm)) {
                        needRequestPermissonList.add(perm);
                    }
                }
            }
            return needRequestPermissonList;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private int checkMySelfPermission(String perm) {
        try {
            Method method = getClass().getMethod("checkSelfPermission", new Class[]{String.class});
            Integer permissionInt = (Integer) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return -1;
    }

    private boolean shouldShowMyRequestPermissionRationale(String perm) {
        try {
            Method method = getClass().getMethod("shouldShowRequestPermissionRationale",
                    new Class[]{String.class});
            Boolean permissionInt = (Boolean) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return false;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        try {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (requestCode == PERMISSON_REQUESTCODE) {
                    if (!verifyPermissions(paramArrayOfInt)) {
                        showMissingPermissionDialog();
                        isNeedCheck = false;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("当前应用缺少必要权限。\\n\\n请点击\\\"设置\\\"-\\\"权限\\\"-打开所需权限");

            // 拒绝, 退出应用
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                finish();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });

            builder.setPositiveButton("设置",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                startAppSettings();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });

            builder.setCancelable(false);

            builder.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        try {
            Intent intent = new Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


}
