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

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class BleEngine {

    private BluetoothAdapter mBluetoothAdapter;
    private Context mContext;
    private BluetoothGatt mBluetoothGatt = null;
    private static UpdatesDelegate mUpdatesDelegate;
    private Handler workHandler = null;
    private Map<String, Boolean> notifyRegisterMap = null;

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

            if(newState == BluetoothProfile.STATE_CONNECTED){
                mBluetoothGatt.requestMtu(517);
                stopLeScan();
                workHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBluetoothGatt.discoverServices();
                        registerListener(true, Constant.ScanCharacteristicUUID);
                        mUpdatesDelegate.didUpdateBleConnectStatus(newState);
                    }
                },1000L);
            }else if(newState == BluetoothProfile.STATE_DISCONNECTING){
                mUpdatesDelegate.didUpdateBleConnectStatus(newState);
            }else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                mUpdatesDelegate.didUpdateBleConnectStatus(newState);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
                if(characteristic.getUuid().equals(UUID.fromString(Constant.ConnectCharacteristicUUID))){
                    byte[] data = characteristic.getValue();
                    int value = (int)data[0];
                    if(value == Constant.WIFI_DISCONNECT){
                        mUpdatesDelegate.didUpdateWifiConnectStatus(Constant.WIFI_DISCONNECT);
                    }else if(value == Constant.WIFI_CONNECTING){
                        mUpdatesDelegate.didUpdateWifiConnectStatus(Constant.WIFI_CONNECTING);
                    }else if(value == Constant.WIFI_CONNECTED){
                        mUpdatesDelegate.didUpdateWifiConnectStatus(Constant.WIFI_CONNECTED);
                    }
                }else if(characteristic.getUuid().equals(UUID.fromString(Constant.SourceSwitchCharacteristicUUID))){
                    byte[] data = characteristic.getValue();
                    int type = (int)data[0];
                    if(type > 0){
                        mUpdatesDelegate.didUpdateSourceType(type);
                    }
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
            if(characteristic.getUuid().equals(UUID.fromString(Constant.ScanCharacteristicUUID))){
               String response = new String(characteristic.getValue());
               if(response != null){
                   try {
                       JSONObject object1 = new JSONObject(response);
                       WifiBean wifiBean = new WifiBean();
                       wifiBean.setSSid(object1.getString("ssid"));
                       wifiBean.setSignal(object1.getString("signal"));
                       JSONObject object2 = object1.getJSONObject("encryption");
                       wifiBean.setWep((object2.getString("wep").equals("false") ) ? false : true);
                       wifiBean.setWpa(object2.getString("wpa"));
                       if(wifiBean != null){
                           mUpdatesDelegate.didUpdateWifi(wifiBean);
                       }
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
            }else if(characteristic.getUuid().equals(UUID.fromString(Constant.ConnectCharacteristicUUID))){
               byte[] data = characteristic.getValue();
               int status = (int)data[0];
               if(status == Constant.WIFI_DISCONNECT){
                   mUpdatesDelegate.didUpdateWifiConnectStatus(Constant.WIFI_DISCONNECT);
               }else if(status == Constant.WIFI_CONNECTING){
                   mUpdatesDelegate.didUpdateWifiConnectStatus(Constant.WIFI_CONNECTING);
               }else if(status == Constant.WIFI_CONNECTED){
                   mUpdatesDelegate.didUpdateWifiConnectStatus(Constant.WIFI_CONNECTED);
               }
            }else if(characteristic.getUuid().equals(UUID.fromString(Constant.SourceSwitchCharacteristicUUID))){
                byte[] data = characteristic.getValue();
                int type = (int)data[0];
                if(type > 0){
                    mUpdatesDelegate.didUpdateSourceType(type);
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
            if(status == BluetoothGatt.GATT_SUCCESS){
                notifyRegisterMap.remove(descriptor.getCharacteristic().getUuid().toString().trim());
                for(Map.Entry<String,Boolean> entry: notifyRegisterMap.entrySet()){
                     registerListener(true, entry.getKey());
                }
            }
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

        }
    };

    public void init(Context context){
        mContext = context;
        HandlerThread handlerThread = new HandlerThread("Ble work");
        handlerThread.start();
        workHandler = new Handler(handlerThread.getLooper());
        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        notifyRegisterMap = new HashMap<>();
        notifyRegisterMap.put(Constant.ScanCharacteristicUUID, false);
        notifyRegisterMap.put(Constant.ConnectCharacteristicUUID, false);
        notifyRegisterMap.put(Constant.SourceSwitchCharacteristicUUID, false);
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

    public void write(String ssid, String password, byte[] value, String CharacteristicUUID){
        if(mBluetoothGatt == null){
            return;
        }
        workHandler.post(new Runnable() {
            @Override
            public void run() {
                BluetoothGattService gattService = mBluetoothGatt.getService(UUID.fromString(Constant.ServiceUUID));
                if( gattService != null){

                    if(CharacteristicUUID != null && TextUtils.equals(Constant.ConnectCharacteristicUUID, CharacteristicUUID)){
                        BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(UUID.fromString(CharacteristicUUID));
                        if(gattCharacteristic != null){
                              if(ssid != null && password != null){
                                  String JsonBean = "{'ssid':'"+ ssid + "','pwd':'"+ password +"'}";
                                  try{
                                      JSONObject jsonObject = new JSONObject(JsonBean);
                                      String data = jsonObject.toString();
                                      gattCharacteristic.setValue(data.getBytes());
                                      mBluetoothGatt.writeCharacteristic(gattCharacteristic);
                                  }catch (JSONException e) {
                                      e.printStackTrace();
                                  }
                              }
                        }
                    }else{
                        BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(UUID.fromString(CharacteristicUUID));
                        if(value != null && gattCharacteristic != null){
                            gattCharacteristic.setValue(value);
                            mBluetoothGatt.writeCharacteristic(gattCharacteristic);
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
                }else{
                    return;
                }

                UUID descriptorUUID = UUID.fromString(Constant.DescriptorUUID);
                BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(descriptorUUID);
                if(descriptor != null){
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    boolean flag = mBluetoothGatt.writeDescriptor(descriptor);
                    if(!flag){
                        registerListener(true, CharacteristicUUID);
                    }
                }
              }
            }
        },500L);
    }

    public interface UpdatesDelegate{
        void didUpdateWifi(WifiBean wifiBean);
        void didUpdateBleConnectStatus(int status);
        void didUpdateLeDevices(TASystem taSystem, int rssi);
        void didUpdateWifiConnectStatus(int status);
        void didUpdateSourceType(int sourceType);
    }
}

