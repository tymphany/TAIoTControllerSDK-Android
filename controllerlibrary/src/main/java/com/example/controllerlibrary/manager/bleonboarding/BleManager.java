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
        mBleEngine.write(null,null, Constant.WRITE_SCAN_REQUEST, Constant.ScanRequestCharacteristicUUID);
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
        mBleEngine.write(ssid, passWord,null, Constant.ConnectRequestCharacteristicUUID);
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
    public void didUpdateWifiList(List<WifiBean> wifiList) {
        synchronized (mOnBleListeners) {
            for (onBleListener listener : mOnBleListeners) {
                listener.didUpdateWifiList(wifiList);
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
    public void didUpdateWifiConnectStatus(boolean status) {
        synchronized (mOnBleListeners){
            for(onBleListener listener : mOnBleListeners){
                listener.didUpdateWifiConnectStatus(status);
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
         * @param wifiList  return wifi list from speaker scan
         */
        void didUpdateWifiList(List<WifiBean> wifiList);

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
         * @param status The status value false is disconnect, value true is connected
         */
        void didUpdateWifiConnectStatus(boolean status);
    }
}
