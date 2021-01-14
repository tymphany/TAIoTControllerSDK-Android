package com.example.controllerlibrary.manager.bleonboarding;

import android.content.Context;
import com.example.controllerlibrary.manager.bleonboarding.bean.TASystem;
import com.example.controllerlibrary.manager.bleonboarding.bean.WifiBean;

import java.util.ArrayList;
import java.util.List;


public class BleManager implements BleEngine.UpdatesDelegate{

    private static volatile BleManager instance;
    private BleEngine mBleEngine;
    private final List<onBleListener> mOnBleListeners = new ArrayList<>();

    private BleManager (){}

    public static BleManager getInstance(){
        if(instance == null){
            synchronized (BleManager.class){
                if(instance == null){
                    instance = new BleManager();
                }
            }
        }
        return instance;
    }

    /**
     * Some of the work in BLE is initialized with this method,
     * which should be used first when you start using other methods of the BleManager class
     *
     * @param context  environment context
     */
    public void init(Context context){
        BleEngine.setDelegate(instance);
        mBleEngine = new BleEngine();
        mBleEngine.init(context);
    }

    /**
     *  This method to start scan around BLE devices, and the method didUpdateLeDevices will call back
     */
    public void startScan(){
        mBleEngine.startLeScan(null);
    }

    /**
     * This method to stop scan around BLE devices
     */
    public void stopScan(){
        mBleEngine.stopLeScan();
    }

    /**
     * Using this method will connect to the specified device via BLE, and the method didUpdateBleConnectStatus will call back
     *
     *
     * @param mac mac address of will connect device
     */
    public void connect(String mac){
        mBleEngine.close();
        mBleEngine.connect(mac);
    }

    /**
     * Using this method will disconnect to device via BLE, and the method didUpdateBleConnectStatus will call back
     *
     */
    public void disConnect(){
        mBleEngine.disconnect();
    }

    /**
     *  Close this Bluetooth GATT client
     */
    public void close(){
        mBleEngine.close();
    }

    /**
     * Using this method will get wifi list from speaker,and the method didUpdateWifiList will call back
     *
     */
    public void readWifiList(){
        mBleEngine.write(null,null, null, Constant.WRITE_SCAN_REQUEST, Constant.ScanCharacteristicUUID);
    }

    /**
     *  This method send the wifi ssid and password to the speaker via BLE , when the speaker connect this wifi or not connect, the
     *
     *  method didUpdateWifiConnectStatus will call back
     *
     * @param ssid  will connect wifi ssid
     *
     * @param passWord will connect wifi password
     */
    public void connectWifi(String ssid, String passWord){
        mBleEngine.write(ssid, passWord,null,null, Constant.ConnectCharacteristicUUID);
    }

    /**
     *  Using this method the method didUpdateWifiConnectStatus will call back
     *
     * @see onBleListener
     *
     */
    public void readWifiConnectStatus(){
        mBleEngine.read(Constant.ConnectCharacteristicUUID);
    }


    /**
     * Using this method set source to BT
     *
     */
    public void setSourceToBt(){
        mBleEngine.write(null,null,null, Constant.SET_SOURCE_BT, Constant.SourceSwitchCharacteristicUUID);
    }

    /**
     * Using this method set source to Wifi
     *
     */
    public void setSourceToWifi(){
        mBleEngine.write(null,null, null, Constant.SET_SOURCE_WIFI, Constant.SourceSwitchCharacteristicUUID);
    }

    /**
     *  Using this method will change name of current device
     *
     * @param name  Will set name that want to change of current device
     */
    public void setDeviceName(String name){
        mBleEngine.write(null,null, name, null, Constant.SetNameCharacteristicUUID);
    }

    /**
     *  Using this method will get name of current device, and the method didUpdateDeviceName will call back
     *
     */
    public void readDeviceName(){
        mBleEngine.read(Constant.SetNameCharacteristicUUID);
    }

    /**
     *  Using this method to get current source type , and the method didUpdateSourceType will call back
     *
     * @see onBleListener
     *
     */
    public void readSourceType(){
        mBleEngine.read(Constant.SourceSwitchCharacteristicUUID);
    }

    /**
     * Add BleListener, if have some update the onBleListener interfaces will receive notify
     *
     * @param listener
     * @see onBleListener
     */
    public void addBleListener(onBleListener listener){
        if(listener != null && !mOnBleListeners.contains(listener)){
            mOnBleListeners.add(listener);
        }
    }

    /**
     * Remove BleListener, if have some update the onBleListener interfaces will not receive notify
     *
     * @param listener
     * @see onBleListener
     */
    public void removeBleListener(onBleListener listener){
        if(listener != null){
            mOnBleListeners.remove(listener);
        }
    }


    @Override
    public void didUpdateWifi(WifiBean wifiBean) {
        synchronized (mOnBleListeners) {
            for (onBleListener listener : mOnBleListeners) {
                listener.didUpdateWifi(wifiBean);
            }
        }
    }

    @Override
    public void didUpdateBleConnectStatus(int status) {
        synchronized (mOnBleListeners){
            for(onBleListener listener : mOnBleListeners){
                listener.didUpdateBleConnectStatus(status);
            }
        }
    }


    @Override
    public void didUpdateLeDevices(TASystem taSystem, int rssi) {
        synchronized (mOnBleListeners){
            for(onBleListener listener : mOnBleListeners){
                listener.didUpdateLeDevices(taSystem, rssi);
            }
        }
    }

    @Override
    public void didUpdateWifiConnectStatus(int status) {
        synchronized (mOnBleListeners){
            for(onBleListener listener : mOnBleListeners){
                listener.didUpdateWifiConnectStatus(status);
            }
        }
    }

    @Override
    public void didUpdateSourceType(int type) {
        synchronized (mOnBleListeners){
            for (onBleListener listener : mOnBleListeners){
                listener.didUpdateSourceType(type);
            }
        }
    }

    @Override
    public void didUpdateDeviceName(String deviceName) {
        synchronized (mOnBleListeners){
            for (onBleListener listener : mOnBleListeners){
                listener.didUpdateDeviceName(deviceName);
            }
        }
    }

    /**
     * <p>This listener gets events related to the BLE feature and data or Wifi connect status of a device. It informs when any of
     * its state change.</p>
     */
    public interface onBleListener{
        /**
         * The method will call back the BLE connect status when device connected or disconnected
         *
         * @param status  The status value 0 is disconnected , value 2 is connected
         */
        void didUpdateBleConnectStatus(int status);

        /**
         * The method will call back when start read wifi list , and return a wifi list
         *
         * @param wifiBean  return wifi object from speaker scan
         */
        void didUpdateWifi(WifiBean wifiBean);

        /**
         * The method will call back when start scan around BLE devices , and return BLE device
         *
         * @param taSystem BLE device
         *
         * @param rssi BLE signal strength
         */
        void didUpdateLeDevices(TASystem taSystem, int rssi);

        /**
         * The method will call back wifi connect status when choice wifi to connect
         *
         * @param status The status value 0 is disconnect, value 1 is connecting, value 2 is connected
         */
        void didUpdateWifiConnectStatus(int status);

        /**
         *  The method will call back current source type when device source type change , or use readSourceType method to read current
         *
         *  source type
         *
         *  (Note: This method will not call back when use method setSourceToBt or setSourceToWifi to set source, due to FW not return. FW will do it in next.
         *
         *   If manually trigger a button on the speaker to switch the source, this method will call back)
         *
         *  @param sourceType The sourceType value 1 is BT, value 2 is Wifi
         */
        void didUpdateSourceType(int sourceType);

        /**
         *  This method will call back when use readDeviceName method to get device name
         *
         *
         * @param deviceName  The device name of current device
         */
        void didUpdateDeviceName(String deviceName);
    }
}
