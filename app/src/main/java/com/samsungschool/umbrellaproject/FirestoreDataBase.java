package com.samsungschool.umbrellaproject;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.Service;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yandex.mapkit.geometry.Point;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

public class FirestoreDataBase {

    private FirebaseFirestore dataBase;
    private int count;
    private List<DocumentSnapshot> res;

    public FirestoreDataBase(){
        dataBase = FirebaseFirestore.getInstance();

    }


    public void setOnOnCompleteDataListener(OnLoadDataListener<List<DocumentSnapshot>> listener) {
        dataBase.collection("stations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            listener.onComplete(Observable.fromArray(task.getResult().getDocuments()));
                        }
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        });
    }


    public static Point castHashMapToPoint(DocumentSnapshot document) {
        HashMap hashMap = (HashMap)document.get("location");
        return new Point((Double)hashMap.get("latitude"), (Double)hashMap.get("longitude"));
    }

}
