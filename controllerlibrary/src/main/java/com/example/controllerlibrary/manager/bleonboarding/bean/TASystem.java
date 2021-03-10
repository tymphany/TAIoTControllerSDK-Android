package com.example.controllerlibrary.manager.bleonboarding.bean;

public class TASystem {

    private String deviceName;
    private String deviceAddress;
    private String serialNumber;
    private int sourceType;

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

    public void setSerialNumber(String serialNumber){
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber(){
        return serialNumber;
    }

    public void setSourceType(int sourceType){
        this.sourceType = sourceType;
    }

    public int getSourceType(){
        return sourceType;
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
                ", serialNumber='" + serialNumber + '\'' +
                ", sourceType=" + sourceType +
                '}';
    }
}
