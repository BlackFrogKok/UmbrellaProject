package com.samsungschool.umbrellaproject.interfaces;

public interface AuthListener {

    void onPhoneSubmitted(String phone);

    void onSmsSubmitted(String sms);

    void onInfoSubmitted(String name, String email);
}
