package com.samsungschool.umbrellaproject;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.samsungschool.umbrellaproject.Interface.MyOnCompliteDataListener;
import com.samsungschool.umbrellaproject.Interface.MyOnCompliteListener;
import com.yandex.mapkit.geometry.Point;

import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class FirestoreDataBase {

    private final FirebaseFirestore dataBase = FirebaseFirestore.getInstance();
    private final String userPath = FirebaseAuth.getInstance().getUid();
    private int count;
    private List<DocumentSnapshot> res;

    public FirestoreDataBase(){

    }

    public void getDocument(String documentID, MyOnCompliteDataListener<DocumentSnapshot> listener){
        dataBase.collection("stations")
                .document(documentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        listener.onComplete(task.getResult());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                });
    }

    public void getDataStations(MyOnCompliteDataListener<List<DocumentSnapshot>> listener) {
        dataBase.collection("stations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            listener.onCompleteObservable(Observable.fromArray(task.getResult().getDocuments()));
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

    public void getUmbrella(int umbrella, String stationID, MyOnCompliteListener listener){
        Log.w("document1", stationID + "+");
        dataBase.collection("stations")
                .document(stationID)
                .update("umbrella", umbrella)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dataBase.collection("stations")
                                .document(stationID)
                                .collection("auth")
                                .document("isBusy")
                                .update("busy", true)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        listener.OnComplite();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        FirebaseCrashlytics.getInstance().recordException(e);
                                    }
                                });
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
