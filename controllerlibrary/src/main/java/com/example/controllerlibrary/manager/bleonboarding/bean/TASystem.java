package com.example.controllerlibrary.manager.bleonboarding.bean;

public class TASystem {

    private String deviceName;
    private String deviceAddress;

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }


    @Override
    public boolean equals(Object o) {
        if (TASystem.class.isInstance(o)
                && ((TASystem) o).deviceAddress.equals(this.deviceAddress)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "TASystem{" +
                "deviceName='" + deviceName + '\'' +
                ", deviceAddress='" + deviceAddress + '\'' +
                '}';
    }
}
