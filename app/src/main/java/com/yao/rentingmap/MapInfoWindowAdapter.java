package com.yao.rentingmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;

import java.lang.ref.WeakReference;

/**
 * Created by Yao on 2016/8/24 0024.
 */
public class MapInfoWindowAdapter implements AMap.InfoWindowAdapter, AMap.OnMarkerClickListener, AMap.OnMapClickListener {

    private WeakReference<Context> mContext = null;
    //    Marker currentMarker;
    ViewHolder viewHolder;
    View.OnClickListener listener;
    AMap aMap;

    public MapInfoWindowAdapter(Context context, AMap aMap) {
        mContext = new WeakReference<>(context);
        this.aMap = aMap;
    }

    /**
     * 监听自定义infowindow窗口的infocontents事件回调
     */

    @Override
    public View getInfoContents(Marker marker) {
//        Log.i("Yao", "------------getInfoContents");
//        if (viewHolder == null)
        Context context = mContext.get();
        if (context != null) {
            viewHolder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_map_info, null));
            viewHolder.tvTitle.setText(marker.getTitle());
            if (listener != null) viewHolder.btNavigation.setOnClickListener(listener);
            return viewHolder.infoWindow;
        }
        return null;
    }

    /**
     * 监听自定义infowindow窗口的infowindow事件回调
     */
    @Override
    public View getInfoWindow(Marker marker) {
//        Log.i("Yao", "------------getInfoWindow");
//        if (viewHolder == null)
        Context context = mContext.get();
        if (context != null) {
            viewHolder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_map_info, null));
            viewHolder.tvTitle.setText(marker.getTitle());
            if (listener != null) viewHolder.btNavigation.setOnClickListener(listener);
            return viewHolder.infoWindow;
        }
        return null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
//        currentMarker = marker;
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
//        if (currentMarker.isInfoWindowShown()) {
//            currentMarker.hideInfoWindow();
//        }
//        for (Marker marker : aMap.getMapScreenMarkers()) {
//            if (marker.isInfoWindowShown())
//                marker.hideInfoWindow();
//        }
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    static class ViewHolder {
        View infoWindow, btNavigation;
        TextView tvTitle;

        public ViewHolder(View infoWindow) {
            this.infoWindow = infoWindow;
            tvTitle = (TextView) infoWindow.findViewById(R.id.tv_title);
        }
    }
}
