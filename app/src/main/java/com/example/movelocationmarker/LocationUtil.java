package com.example.movelocationmarker;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.MyTrafficStyle;

/**
 * <br>
 * <p>
 * <p>
 * com.example.movelocationmarker
 * <p>
 * FUXI
 */
public class LocationUtil {

    public static void zoomIn(AMap aMap) {
        changeCamera(aMap, CameraUpdateFactory.zoomIn(), null);
    }


    public static void zoomOut(AMap aMap) {
        changeCamera(aMap, CameraUpdateFactory.zoomOut(), null);
    }

    private static void changeCamera(AMap aMap, CameraUpdate cameraUpdate,
                                     AMap.CancelableCallback cancelableCallback) {
        if (aMap == null) {
            return;
        }
        aMap.moveCamera(cameraUpdate);
    }


    public static void setTrafficStyle(AMap aMap, boolean isTraffic) {
        MyTrafficStyle myTrafficStyle = new MyTrafficStyle();
        myTrafficStyle.setSeriousCongestedColor(-0x6dfff6);
        myTrafficStyle.setCongestedColor(-0x15fcee);
        myTrafficStyle.setSlowColor(-0x8af8);
        myTrafficStyle.setSmoothColor(-0xff5df7);
        aMap.setMyTrafficStyle(myTrafficStyle);
        aMap.setTrafficEnabled(isTraffic);
    }


    public static void setSetting(AMap aMap) {
        if (aMap == null) {
            return;
        }
        // 设置为 true 表示启动显示定位蓝点
        // 设置为 false 表示隐藏定位蓝点并不进行定位
        aMap.setMyLocationEnabled(false);
        /* 设置最小缩放级别
         * 在 SDK 地图 zoom 级别 3 和 4（世界地图级别）显示中国以外地区时，
         * 由于没有详细地图数据，显示效果看起来和地图加载失败时一样的黄白色底图，
         * 因此我们把最小缩放级别设置为 5 。
         */
        aMap.setMinZoomLevel(3f);
        UiSettings uiSettings = aMap.getUiSettings();
        // 显示高德自带缩放按钮
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setScaleControlsEnabled(true);
        // 不显示高德自带定位按钮
        uiSettings.setMyLocationButtonEnabled(false);
        // 把高德 Logo 挪到屏幕显示区域外
        uiSettings.setLogoBottomMargin(-666);
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
}
