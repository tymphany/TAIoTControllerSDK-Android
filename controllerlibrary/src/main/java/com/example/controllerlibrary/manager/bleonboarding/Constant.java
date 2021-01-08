package com.example.controllerlibrary.manager.bleonboarding;

public class Constant {

    public static final byte[] WRITE_SCAN_REQUEST = { (byte)1};
    public static final byte[] SET_SOURCE_BT = { (byte)1};
    public static final byte[] SET_SOURCE_WIFI ={ (byte)2};

    public static final int WIFI_DISCONNECT = 0;
    public static final int WIFI_CONNECTING = 1;
    public static final int WIFI_CONNECTED = 2;

    public static final String ServiceUUID = "0000fef1-0000-1000-8000-00805f9b34fb";
    public static final String ScanCharacteristicUUID = "0000aa01-0000-1000-8000-008012340001";
    public static final String ConnectCharacteristicUUID = "0000aa01-0000-1000-8000-008012340000";
    public static final String SourceSwitchCharacteristicUUID = "0000aa01-0000-1000-8000-008012340002";
    public static final String DescriptorUUID = "00002902-0000-1000-8000-00805f9b34fb";
}
