package com.samsungschool.umbrellaproject;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.samsungschool.umbrellaproject.Fragments.NavigationItems.HistoryFragment.HistoryItem;
import com.samsungschool.umbrellaproject.Interface.MyOnCompliteDataListener;
import com.samsungschool.umbrellaproject.Interface.MyOnCompliteListener;
import com.yandex.mapkit.geometry.Point;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;

import io.reactivex.rxjava3.core.Observable;

public class FirestoreDataBase {

    private final FirebaseFirestore dataBase = FirebaseFirestore.getInstance();
    private final String userPath = FirebaseAuth.getInstance().getUid();
    private int count;
    private List<DocumentSnapshot> res;

    public FirestoreDataBase() {

    }

    public void getDocument(String documentID, MyOnCompliteDataListener<DocumentSnapshot> listener) {
        dataBase.collection("stations")
                .document(documentID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onComplete(task.getResult());
                    }
                })
                .addOnFailureListener(e -> FirebaseCrashlytics.getInstance().recordException(e));
    }

    public void getDataStations(MyOnCompliteDataListener<List<DocumentSnapshot>> listener) {
        dataBase.collection("stations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onCompleteObservable(Observable.fromArray(task.getResult().getDocuments()));
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onCanceled();
                    FirebaseCrashlytics.getInstance().recordException(e);
                });
    }

    public void getUmbrellaCount(String stationID, MyOnCompliteDataListener<Integer> listener) {
        dataBase.collection("stations")
                .document(stationID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onComplete(((ArrayList<Integer>) (task.getResult().get("freeUmbrella"))).size());
                    }

                })
                .addOnFailureListener(e -> {
                });
    }

    public void closeStation(String stationID) {
        dataBase.collection("stations")
                .document(stationID)
                .collection("auth")
                .document("isIssued")
                .update("issued", true)
                .addOnCompleteListener(task -> {
                })
                .addOnFailureListener(e -> FirebaseCrashlytics.getInstance().recordException(e));
    }

    private void getStationAdress(String stationID, OnCompleteListener<DocumentSnapshot> listener) {
        dataBase.collection("stations")
                .document(stationID)
                .get()
                .addOnCompleteListener(listener)
                .addOnFailureListener(e -> FirebaseCrashlytics.getInstance().recordException(e));

    }

    public void getUser(String uID, MyOnCompliteDataListener<User> listener) {
        dataBase.collection("users")
                .document(uID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            listener.onComplete(task.getResult().toObject(User.class));
                        }
                    }
                })
                .addOnFailureListener(e -> FirebaseCrashlytics.getInstance().recordException(e));

    }

    public void addHistory(String stationID, String name) {
        getStationAdress(stationID, task -> {
            if (task.isSuccessful()) {
                HistoryItem h = new HistoryItem();
                h.setAddress(task.getResult().get("adress", String.class));
                h.setStatus(false);
                h.setDate(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date()));
                h.setTime("Аренда");
                h.setTimeGet(getCurrentTime());
                h.setStationGetID(stationID);
                dataBase.collection("users")
                        .document(FirebaseAuth.getInstance().getUid())
                        .collection("history")
                        .document(name)
                        .set(h)
                        .addOnCompleteListener(task1 -> {
                        })
                        .addOnFailureListener(e -> FirebaseCrashlytics.getInstance().recordException(e));
            }
        });


    }

    public void endHistory(String stationID, String name) {
        DocumentReference b = dataBase.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("history")
                .document(name);
        b.update("timePut", getCurrentTime());
        b.update("time", "Сдан");


    }

    //доделать не забудь
    public void deleteUmbrella(String stationID) {
        dataBase.collection("stations")
                .document(stationID)
                .update("freeUmbrella", Arrays.asList(1, 2, 3, 4, 5, 6))
                .addOnCompleteListener(task -> {
                })
                .addOnFailureListener(e -> FirebaseCrashlytics.getInstance().recordException(e));

    }

    public void getUmbrella(int umbrella, String stationID, MyOnCompliteListener listener) {
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
        HashMap hashMap = (HashMap) document.get("location");
        return new Point((Double) hashMap.get("latitude"), (Double) hashMap.get("longitude"));
    }

    private String getCurrentTime() {
        String time;
        Calendar calendar = Calendar.getInstance();
        Log.d("TAG", calendar.getTime().toString());
        return String.valueOf(calendar.getTime().getHours()) + ":" + String.valueOf(calendar.getTime().getMinutes());
    }
}
