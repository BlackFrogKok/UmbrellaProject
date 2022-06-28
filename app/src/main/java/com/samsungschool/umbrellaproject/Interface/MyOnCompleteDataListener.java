package com.samsungschool.umbrellaproject.Interface;

import androidx.annotation.NonNull;

import java.util.Collections;

import io.reactivex.rxjava3.core.Observable;

public interface MyOnCompleteDataListener<TResult> {
    void onCompleteObservable(@NonNull Observable<TResult> observable);

    void onComplete(@NonNull TResult result);

    void onCanceled();
}
