package com.samsungschool.umbrellaproject;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.yandex.mapkit.map.Cluster;
import com.yandex.mapkit.map.ClusterTapListener;

public class ClusterTapLisenerClass implements ClusterTapListener {

    @Override
    public boolean onClusterTap(@NonNull Cluster cluster) {
        Log.w("Cluster", cluster.getAppearance().getGeometry().toString());


        // We return true to notify map that the tap was handled and shouldn't be
        // propagated further.
        return true;    }
}
