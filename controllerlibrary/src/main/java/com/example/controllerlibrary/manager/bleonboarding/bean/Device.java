package com.example.controllerlibrary.manager.bleonboarding.bean;

public class Device {
    public String name;
    public String mac;
    public int rssi;

    public Device(){}
    public Device(String name, String mac, int rssi) {
        this.name = name;
        this.mac = mac;
        this.rssi = rssi;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Device) {
            Device dev = (Device)o;

            if (dev.name.equals(this.name)
                    && dev.mac.equals(this.mac) && dev.rssi == this.rssi) {
                return true;
            }
        }

        return false;
    }
}
