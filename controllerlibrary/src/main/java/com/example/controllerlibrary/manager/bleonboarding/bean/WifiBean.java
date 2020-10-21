package com.example.controllerlibrary.manager.bleonboarding.bean;

public class WifiBean {
    private String ssid;
    private String signal;
    private boolean wep;
    private String wpa;

    public WifiBean() {
    }

    public WifiBean(String ssid, String signal, boolean wep, String wpa) {
        this.ssid = ssid;
        this.signal = signal;
        this.wep = wep;
        this.wpa = wpa;
    }

    public String getSSid() {
        return ssid;
    }

    public void setSSid(String ssid) {
        this.ssid = ssid;
    }

    public String getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    public boolean isWep() {
        return wep;
    }

    public void setWep(boolean wep) {
        this.wep = wep;
    }

    public String getWpa() {
        return wpa;
    }

    public void setWpa(String wpa) {
        this.wpa = wpa;
    }
}
