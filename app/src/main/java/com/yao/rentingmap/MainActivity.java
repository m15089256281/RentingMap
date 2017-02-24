package com.yao.rentingmap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

public class MainActivity extends Activity implements AMap.CancelableCallback {

    TextureMapView mMapView = null;
    private AMap aMap;
    CardGalleryView cardGalleryView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        //获取地图控件引用
        mMapView = (TextureMapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        cardGalleryView = (CardGalleryView) findViewById(R.id.card_gallery);

        cardGalleryView.setAdapter(new PicAdapter());

        aMap.getUiSettings().setZoomControlsEnabled(false);
        MapInfoWindowAdapter mapInfoWindowAdapter = new MapInfoWindowAdapter(context, aMap);
//        mapInfoWindowAdapter.setListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (v.getId()) {
////                    case R.id.bt_navigation:
////                        Toast.makeText(context, "bt_navigation", Toast.LENGTH_SHORT).show();
////                        break;
//                }
//            }
//        });
        aMap.setOnMapClickListener(mapInfoWindowAdapter);// 设置点击地图事件监听器
        aMap.setOnMarkerClickListener(mapInfoWindowAdapter);// 设置点击marker事件监听器
        aMap.setInfoWindowAdapter(mapInfoWindowAdapter);// 设置自定义InfoWindow样式

        LatLng latLng = new LatLng(23.124680, 113.361200);
        addMarker(latLng, "250", R.mipmap.ic_posframe);

        moveCamera(latLng);
    }

    private void addMarker(LatLng latLng, String location, int icon) {
        MarkerOptions mo = new MarkerOptions();
        mo.position(latLng).title(location).visible(true).icon(BitmapDescriptorFactory.fromResource(icon));
        if (aMap != null) {
            aMap.addMarker(mo).showInfoWindow();
        }
    }

    private void moveCamera(LatLng latLng) {
        try {
            final CameraUpdate update = CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latLng, 16));
            if (aMap != null)
                aMap.animateCamera(update, 1000, this);
            aMap.moveCamera(update);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onCancel() {

    }
}