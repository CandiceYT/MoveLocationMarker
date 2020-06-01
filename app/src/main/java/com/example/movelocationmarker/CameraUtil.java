package com.example.movelocationmarker;

import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;

/**
 * <br>
 * <p>
 * <p>
 * com.example.movelocationmarker
 * <p>
 * FUXI
 */
public class CameraUtil {
    private static final String TAG = CameraUtil.class.getSimpleName();

    public static void moveMapCamera(AMap aMap, double latitude, double longitude, float zoom,
                                     float tilt, float bearing) {
        if (aMap == null || !isValid(latitude, longitude)) {
            return;
        }
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(zoom)
                .tilt(tilt)
                .bearing(bearing)
                .build();
        Log.e(TAG, "cameraPosition is " + cameraPosition.toString());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
//        aMap.clear(isClear);
        aMap.moveCamera(cameraUpdate);
    }

    public static void animateCamera(AMap aMap, double latitude, double longitude, float zoom,
                                     float tilt, float bearing) {
        if (aMap == null || !isValid(latitude, longitude)) {
            return;
        }
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(zoom)
                .tilt(tilt)
                .bearing(bearing)
                .build();
        Log.e(TAG, "cameraPosition is " + cameraPosition.toString());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        aMap.animateCamera(cameraUpdate, 2000, null);
    }

    private static boolean isValid(double latitude, double longitude) {
        if (0.0 == latitude || 0.0 == longitude) {
            return false;
        } else {
            return true;
        }
    }
}
