package com.example.controllerlibrary.manager.bleonboarding;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;


import com.example.controllerlibrary.manager.bleonboarding.bean.TASystem;
import com.example.controllerlibrary.manager.bleonboarding.bean.WifiBean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class BleEngine {

    private BluetoothAdapter mBluetoothAdapter;
    private Context mContext;
    private BluetoothGatt mBluetoothGatt = null;
    private ArrayList<WifiBean> wifiList = null;
    private static UpdatesDelegate mUpdatesDelegate;
    private Handler workHandler = null;

    private final BluetoothAdapter.LeScanCallback callback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if(device.getName() != null){
                TASystem taSystem =  new TASystem();
                taSystem.setDeviceName(device.getName());
                taSystem.setDeviceAddress(device.getAddress());
                mUpdatesDelegate.didUpdateLeDevices(taSystem, rssi);
            }
        }
    };

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            mUpdatesDelegate.didUpdateBleConnectStatus(newState);
            if(newState == BluetoothProfile.STATE_CONNECTED){
                mBluetoothGatt.requestMtu(517);
                stopLeScan();
            }else if(newState == BluetoothProfile.STATE_DISCONNECTING){

            }else if(newState == BluetoothProfile.STATE_DISCONNECTED){

            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if(status == BluetoothGatt.GATT_SUCCESS){
                registerListener(true, Constant.ConnectedStatusCharacteristicUUID);
                String data = new String(characteristic.getValue());
                String[] strWifiList = data.split("/");
                wifiList = new ArrayList<>();
                for(int i = 0; i < strWifiList.length; i++ ){
                    WifiBean wifiBean = new WifiBean();
                    wifiBean.setSSid(strWifiList[i]);
                    wifiList.add(wifiBean);
                }
                workHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                      mUpdatesDelegate.didUpdateWifiList(wifiList);
                    }
                },200L);
            }

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if(status == BluetoothGatt.GATT_SUCCESS){

            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            if(characteristic.getUuid().equals(UUID.fromString(Constant.ScanRequestCharacteristicUUID))){
               String response = new String(characteristic.getValue());
               if(TextUtils.equals(Constant.SCAN_REQUEST_RESPONSE, response)){
                   read(Constant.ScanRecordCharacteristicUUID);
               }
            }else if(characteristic.getUuid().equals(UUID.fromString(Constant.ConnectedStatusCharacteristicUUID))){
               String response = new String(characteristic.getValue());
               if(TextUtils.equals(Constant.CONNECTED_STATUS_FAILURE, response)){
                   mUpdatesDelegate.didUpdateWifiConnectStatus(false);
               }else if(TextUtils.equals(Constant.CONNECTED_STATUS_SUCCESS, response)){
                   mUpdatesDelegate.didUpdateWifiConnectStatus(true);
               }
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            if(status == BluetoothGatt.GATT_SUCCESS){
                if(mBluetoothGatt != null){
                    mBluetoothGatt.discoverServices();
                    registerListener(true, Constant.ScanRequestCharacteristicUUID);
                }
            }
        }
    };

    public void init(Context context){
        mContext = context;
        HandlerThread handlerThread = new HandlerThread("Ble work");
        handlerThread.start();
        workHandler = new Handler(handlerThread.getLooper());
        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if(!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();
        }
    }

    public static void setDelegate(UpdatesDelegate delegate){
        mUpdatesDelegate = delegate;
    }

    public void startLeScan(UUID[] UUID){
       workHandler.postDelayed(new Runnable() {
           @Override
           public void run() {
               if(UUID == null){
                   boolean isSuccess = mBluetoothAdapter.startLeScan(callback);
               }else{
                   boolean isSuccess = mBluetoothAdapter.startLeScan(UUID,callback);
               }
           }
       },2000L);
    }


    public void stopLeScan(){
        workHandler.post(new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.stopLeScan(callback);
            }
        });
    }

    public boolean connect(String address){

        if(mBluetoothAdapter == null && address == null){
            return false;
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        if(device == null){
            return false;
        }

        workHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothGatt = device.connectGatt(mContext, false ,gattCallback);
            }
        },2000L);

        return true;
    }


    public void disconnect(){
        if(mBluetoothAdapter == null || mBluetoothGatt == null){
            return;
        }
        workHandler.post(new Runnable() {
            @Override
            public void run() {
                mBluetoothGatt.disconnect();
            }
        });

    }

    public void close(){
        if(mBluetoothGatt == null){
            return;
        }
        workHandler.post(new Runnable() {
            @Override
            public void run() {
                mBluetoothGatt.close();
                mBluetoothGatt = null;
            }
        });
    }

    public void read(String CharacteristicUUID){
        if(mBluetoothGatt == null){
          return;
        }
        workHandler.post(new Runnable() {
            @Override
            public void run() {
                BluetoothGattService gattService = mBluetoothGatt.getService(UUID.fromString(Constant.ServiceUUID));
                if(gattService != null){
                    BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(UUID.fromString(CharacteristicUUID));
                    if(gattCharacteristic != null){
                        mBluetoothGatt.readCharacteristic(gattCharacteristic);
                    }
                }
            }
        });
    }

    public void write(String ssid, String password, String strValue, String CharacteristicUUID){
        if(mBluetoothGatt == null){
            return;
        }
        workHandler.post(new Runnable() {
            @Override
            public void run() {
                BluetoothGattService gattService = mBluetoothGatt.getService(UUID.fromString(Constant.ServiceUUID));
                if( gattService != null){
                    if(CharacteristicUUID != null && TextUtils.equals(Constant.ScanRequestCharacteristicUUID, CharacteristicUUID)){
                        BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(UUID.fromString(CharacteristicUUID));
                        if(strValue != null && gattCharacteristic != null){
                            gattCharacteristic.setValue(strValue.getBytes());
                            mBluetoothGatt.writeCharacteristic(gattCharacteristic);
                        }
                    }else if(CharacteristicUUID != null && TextUtils.equals(Constant.ConnectRequestCharacteristicUUID, CharacteristicUUID)){
                        BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(UUID.fromString(CharacteristicUUID));
                        if(gattCharacteristic != null){
                              if(ssid != null && password != null){
                                  String wifiConfig = ssid+","+password;
                                  gattCharacteristic.setValue(wifiConfig.getBytes());
                                  mBluetoothGatt.writeCharacteristic(gattCharacteristic);
                              }
                        }
                    }
                }
            }
        });
    }

    public void registerListener(boolean isRegister, String CharacteristicUUID){
        workHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
              BluetoothGattService gattService = mBluetoothGatt.getService(UUID.fromString(Constant.ServiceUUID));
             if( gattService != null) {
                BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(UUID.fromString(CharacteristicUUID));
                if (gattCharacteristic != null) {
                    mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, isRegister);
                }

                UUID descriptorUUID = UUID.fromString(Constant.DescriptorUUID);
                BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(descriptorUUID);
                if(descriptor != null){
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(descriptor);
                }
              }
            }
        },500L);
    }

    public interface UpdatesDelegate{
        void didUpdateWifiList(List<WifiBean> wifiList);
        void didUpdateBleConnectStatus(int status);
        void didUpdateLeDevices(TASystem taSystem, int rssi);
        void didUpdateWifiConnectStatus(boolean status);
    }
}

