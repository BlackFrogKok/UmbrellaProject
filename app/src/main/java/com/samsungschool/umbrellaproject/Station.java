package com.samsungschool.umbrellaproject;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.samsungschool.umbrellaproject.Interface.MyOnCompleteDataListener;

import java.util.Objects;

public class Station {
    private static final String TAG = "STATION" ;
    private final FirebaseFirestore dataBase = FirebaseFirestore.getInstance();


    public void checkQrData(String result, MyOnCompleteDataListener<String> listener) {
        try {

            String[] subStr;
            Log.d(TAG, result);
            if(Objects.equals(result, "-")){
                listener.onCanceled();
                return;
            }
            if (result.contains("-")) {
                subStr = result.split("-");
            }
            else {
                subStr = result.split("");

            }
            if (subStr.length == 2) {
                dataBase.collection("stations").document(subStr[1])
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful() &
                                        Objects.equals(task.getResult().get("securityWord"), subStr[0])) {
                                    listener.onComplete(subStr[1]);
                                } else {
                                    listener.onCanceled();
                                }
                            }

                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                FirebaseCrashlytics.getInstance().recordException(e);
                                Log.w("document2", e.getMessage());
                                listener.onCanceled();
                            }
                        });
            }
            else {
                listener.onCanceled();
            }
        }
        catch(Exception e){
            listener.onCanceled();
        }
    }

    public void checkCode(String result, MyOnCompleteDataListener<String> listener){
        String[] subStr;
        subStr = result.split("-");
        if (subStr.length == 2){
            dataBase.collection("stations").document(subStr[1])
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful() &
                                    task.getResult().get("securityCode").equals(subStr[0])){
                                listener.onComplete(subStr[1]);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            Log.w("document2", e.getMessage());
                        }
                    });
        }
    }
}
