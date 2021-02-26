package com.example.controllerlibrary.manager.bleonboarding;

public class Constant {

    public static final byte[] WRITE_SCAN_REQUEST = { (byte)1};
    public static final byte[] SET_SOURCE_BT = { (byte)1};
    public static final byte[] SET_SOURCE_WIFI ={ (byte)2};

    public static final int WIFI_DISCONNECT = 0;
    public static final int WIFI_CONNECTING = 1;
    public static final int WIFI_CONNECTED = 2;

    public static final String CustomAudioControlServiceUUID = "0000fef1-0000-1000-8000-00805f9b34fb";
    public static final String ScanCharacteristicUUID = "0000aa01-0000-1000-8000-008012340001";
    public static final String ConnectCharacteristicUUID = "0000aa01-0000-1000-8000-008012340000";
    public static final String SourceSwitchCharacteristicUUID = "0000aa01-0000-1000-8000-008012340002";
    public static final String SetNameCharacteristicUUID = "0000aa01-0000-1000-8000-008012340003";
    public static final String LedControlCharacteristicUUID = "0000aa01-0000-1000-8000-008012340004";
    public static final String ActionCharacteristicUUID = "0000aa01-0000-1000-8000-008012340005";
    public static final String DescriptorUUID = "00002902-0000-1000-8000-00805f9b34fb";

    public static final String DeviceInfoServiceUUID = "0000180a-0000-1000-8000-00805f9b34fb";
    public static final String DeviceInfoFirmwareVersionCharacteristicUUID = "00002a26-0000-1000-8000-00805f9b34fb";
    public static final String DeviceInfoSerialNumberCharacteristicUUID = "00002a25-0000-1000-8000-00805f9b34fb";

    public static final String BatteryInfoServiceUUID = "0000180f-0000-1000-8000-00805f9b34fb";
    public static final String BatteryLevelCharacteristicUUID = "00002a19-0000-1000-8000-00805f9b34fb";
}
