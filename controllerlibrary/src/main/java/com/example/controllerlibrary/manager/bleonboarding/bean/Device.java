package com.example.controllerlibrary.manager.bleonboarding.bean;

public class Device {
    public String name;
    public String mac;
    public int rssi;
    public String serialNumber;
    public int sourceType;

    public Device(){}
    public Device(String name, String mac, int rssi, String serialNumber, int sourceType) {
        this.name = name;
        this.mac = mac;
        this.rssi = rssi;
        this.serialNumber = serialNumber;
        this.sourceType = sourceType;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Device) {
            Device dev = (Device)o;

            if (dev.name.equals(this.name)
                    && dev.mac.equals(this.mac) && dev.rssi == this.rssi && dev.serialNumber.equals(this.serialNumber) && dev.sourceType == this.sourceType) {
                return true;
            }
        }

        return false;
    }
}
