package com.samsungschool.umbrellaproject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;

import androidx.annotation.NonNull;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

public class userPoint implements UserLocationObjectListener {
    private Context context;
    UserLocationLayer userLocationLayer;
    MapView mapview;

    public userPoint(Context context, UserLocationLayer userLocationLayer, MapView mapview){
        this.context = context;
        this.userLocationLayer = userLocationLayer;
        this.mapview = mapview;
    }

    @Override
    public void onObjectAdded(@NonNull UserLocationView userLocationView) {
        userLocationLayer.setAnchor(
                new PointF((float)(mapview.getWidth() * 0.5), (float)(mapview.getHeight() * 0.5)),
                new PointF((float)(mapview.getWidth() * 0.5), (float)(mapview.getHeight() * 0.83))
        );
        userLocationView.getArrow().setIcon(ImageProvider.fromResource(context, R.drawable.user_marker));
        CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();
        pinIcon.setIcon(
                "icon",
                ImageProvider.fromResource(context, R.drawable.user_marker_compass),
                new IconStyle().setAnchor(new PointF(0f, 0f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(0f)
                        .setScale(1f)
        );

        pinIcon.setIcon(
                "pin",
                ImageProvider.fromResource(context, R.drawable.ic_user_navi_pin),
                new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(1f)
                        .setScale(0.5f)
        );

        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE & 0x99ffffff);

    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {

    }

    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {

    }
}
