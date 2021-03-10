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
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.ParcelUuid;
import android.text.TextUtils;


import com.example.controllerlibrary.manager.bleonboarding.bean.TASystem;
import com.example.controllerlibrary.manager.bleonboarding.bean.WifiBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class BleEngine {

    private BluetoothAdapter mBluetoothAdapter;
    private Context mContext;
    private BluetoothGatt mBluetoothGatt = null;
    private static UpdatesDelegate mUpdatesDelegate;
    private Handler workHandler = null;
    private Map<String, String> notifyRegisterMap = null;
    private boolean notifyDataRegisterFlag = false;
    private BluetoothLeScanner bluetoothLeScanner = null;
    private ScanSettings mScanSettings = null;
    private ScanFilter mScanFilter = null;
    private List<ScanFilter> scanFilterList = new ArrayList<>();
    private byte[] manufacturerData = null;
    private TASystem taSystem = null;


    private final ScanCallback leCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if(result.getScanRecord().getDeviceName() != null){
                taSystem = new TASystem();
                manufacturerData = result.getScanRecord().getManufacturerSpecificData().valueAt(0);
                if(manufacturerData.length != 0 && manufacturerData.length > 2){
                    taSystem.setSourceType((int)manufacturerData[0]);
                    taSystem.setSerialNumber(new String(extractBytes(manufacturerData,1,manufacturerData.length-1)));
                    taSystem.setDeviceName(result.getDevice().getName());
                    taSystem.setDeviceAddress(result.getDevice().getAddress());
                }else{
                    taSystem.setDeviceName(result.getDevice().getName());
                    taSystem.setDeviceAddress(result.getDevice().getAddress());
                }
                mUpdatesDelegate.didUpdateLeDevices(taSystem, result.getRssi());
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
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
                mBluetoothGatt.requestMtu(200);
                stopLeScan();
                workHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBluetoothGatt.discoverServices();
                    }
                },2000L);
            }else if(newState == BluetoothProfile.STATE_DISCONNECTING){
                mUpdatesDelegate.didUpdateBleConnectStatus(newState);
            }else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                mUpdatesDelegate.didUpdateBleConnectStatus(newState);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            registerListener(true, Constant.CustomAudioControlServiceUUID, Constant.ScanCharacteristicUUID);
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
                }else if(characteristic.getUuid().equals(UUID.fromString(Constant.SetNameCharacteristicUUID))){
                    String deviceName = new String(characteristic.getValue());
                    if(deviceName != null){
                        mUpdatesDelegate.didUpdateDeviceName(deviceName);
                    }
                }else if(characteristic.getUuid().equals(UUID.fromString(Constant.DeviceInfoFirmwareVersionCharacteristicUUID))){
                    String firmwareVersion = new String(characteristic.getValue());
                    if(firmwareVersion != null){
                        mUpdatesDelegate.didUpdateFirmwareVersion(firmwareVersion);
                    }
                }else if(characteristic.getUuid().equals(UUID.fromString(Constant.DeviceInfoSerialNumberCharacteristicUUID))){
                    String serialNumber = new String(characteristic.getValue());
                    if(serialNumber != null){
                        mUpdatesDelegate.didUpdateSerialNumber(serialNumber);
                    }
                }else if(characteristic.getUuid().equals(UUID.fromString(Constant.BatteryLevelCharacteristicUUID))){
                    byte[] data = characteristic.getValue();
                    int batteryLevel = (int)data[0];
                    if(batteryLevel > -1){
                        mUpdatesDelegate.didUpdateBatteryLevel(batteryLevel);
                    }
                }else if(characteristic.getUuid().equals(UUID.fromString(Constant.LedControlCharacteristicUUID))){
                    byte[] data = characteristic.getValue();
                    int ledPattern = (int)data[0];
                    int ledAnimation = (int)data[1];
                    if(ledPattern > -1 || ledAnimation > -1){
                        mUpdatesDelegate.didUpdateLedPattern(ledPattern);
                        mUpdatesDelegate.didUpdateLedAnimation(ledAnimation);
                    }
                }else if(characteristic.getUuid().equals(UUID.fromString(Constant.ActionCharacteristicUUID))){
                    byte[] data = characteristic.getValue();
                    String macAddress =  null;
                    int command = (int)data[0];
                    int btStatus = (int)data[18];
                    byte[] btMacAddress = new byte[17];
                    if(data.length >= 19){
                        for (int i=0; i<btMacAddress.length; i++){
                            btMacAddress[i] = data[i+1];
                        }
                        macAddress = new String(btMacAddress);
                    }

                    if(command == 1){
                        mUpdatesDelegate.didUpdateBTConnectStatus(btStatus);
                        if (macAddress != null){
                            mUpdatesDelegate.didUpdateBTMacAddress(macAddress.toUpperCase());
                        }
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
               }else{
                   mUpdatesDelegate.didUpdateBleConnectStatus(status);
               }
            }else if(characteristic.getUuid().equals(UUID.fromString(Constant.SourceSwitchCharacteristicUUID))){
                byte[] data = characteristic.getValue();
                int type = (int)data[0];
                if(type > 0){
                    mUpdatesDelegate.didUpdateSourceType(type);
                }
            }else if(characteristic.getUuid().equals(UUID.fromString(Constant.SetNameCharacteristicUUID))){
                String deviceName = new String(characteristic.getValue());
                if(deviceName != null){
                    mUpdatesDelegate.didUpdateDeviceName(deviceName);
                }
            }else if(characteristic.getUuid().equals(UUID.fromString(Constant.BatteryLevelCharacteristicUUID))){
                byte[] data = characteristic.getValue();
                int batteryLevel = (int)data[0];
                if(batteryLevel > -1){
                    mUpdatesDelegate.didUpdateBatteryLevel(batteryLevel);
                }
            }else if(characteristic.getUuid().equals(UUID.fromString(Constant.LedControlCharacteristicUUID))){
                byte[] data = characteristic.getValue();
                int ledPattern = (int)data[0];
                int ledAnimation = (int)data[1];
                if(ledPattern > -1 || ledAnimation > -1){
                    mUpdatesDelegate.didUpdateLedPattern(ledPattern);
                    mUpdatesDelegate.didUpdateLedAnimation(ledAnimation);
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
                if(notifyDataRegisterFlag){
                    return;
                }
                if( notifyRegisterMap.size() == 0){
                    notifyDataRegisterFlag = true;
                    mUpdatesDelegate.didUpdateBleConnectStatus(2);
                }
                for(Map.Entry<String,String> entry: notifyRegisterMap.entrySet()){
                     registerListener(true, entry.getValue(), entry.getKey());
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
        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        initNotifyData();
        if(!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();
        }
    }

    public static void setDelegate(UpdatesDelegate delegate){
        mUpdatesDelegate = delegate;
    }

    public void startLeScan(String uuid){
       workHandler.postDelayed(new Runnable() {
           @Override
           public void run() {
               ScanFilter.Builder filterBuilder = new ScanFilter.Builder();
               mScanFilter = filterBuilder.setServiceUuid(ParcelUuid.fromString(uuid)).build();
               scanFilterList.add(mScanFilter);
               ScanSettings.Builder settingBuilder = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED);
               mScanSettings = settingBuilder.build();
               bluetoothLeScanner.startScan(scanFilterList, mScanSettings, leCallback);
           }
       },1000L);
    }


    public void stopLeScan(){
        workHandler.post(new Runnable() {
            @Override
            public void run() {
                bluetoothLeScanner.stopScan(leCallback);
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
        initNotifyData();
        workHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothGatt = device.connectGatt(mContext, false ,gattCallback);
            }
        },1500L);

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
                notifyDataRegisterFlag = false;
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
                notifyDataRegisterFlag = false;
                mScanFilter = null;
                mScanSettings = null;
            }
        });
    }

    public void read(String serviceUUID, String characteristicUUID){
        if(mBluetoothGatt == null){
          return;
        }
        workHandler.post(new Runnable() {
            @Override
            public void run() {
                BluetoothGattService gattService = mBluetoothGatt.getService(UUID.fromString(serviceUUID));
                if(gattService != null){
                    BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(UUID.fromString(characteristicUUID));
                    if(gattCharacteristic != null){
                        mBluetoothGatt.readCharacteristic(gattCharacteristic);
                    }
                }
            }
        });
    }

    public void write(String ssid, String password, String strValue, byte[] value, String CharacteristicUUID){
        if(mBluetoothGatt == null){
            return;
        }
        workHandler.post(new Runnable() {
            @Override
            public void run() {
                BluetoothGattService gattService = mBluetoothGatt.getService(UUID.fromString(Constant.CustomAudioControlServiceUUID));
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
                        }else if(strValue != null && gattCharacteristic != null){
                            gattCharacteristic.setValue(strValue.trim().getBytes());
                            mBluetoothGatt.writeCharacteristic(gattCharacteristic);
                        }
                    }
                }
            }
        });
    }

    public void registerListener(boolean isRegister, String serviceUUID, String characteristicUUID){
        workHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
              BluetoothGattService gattService = mBluetoothGatt.getService(UUID.fromString(serviceUUID));
             if( gattService != null) {
                BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(UUID.fromString(characteristicUUID));
                if (gattCharacteristic != null) {
                    mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, isRegister);
                }else{
                    return;
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

    public void initNotifyData(){
        notifyRegisterMap = new HashMap<>();
        notifyRegisterMap.put(Constant.ScanCharacteristicUUID, Constant.CustomAudioControlServiceUUID);
        notifyRegisterMap.put(Constant.ConnectCharacteristicUUID, Constant.CustomAudioControlServiceUUID);
        notifyRegisterMap.put(Constant.SourceSwitchCharacteristicUUID, Constant.CustomAudioControlServiceUUID);
        notifyRegisterMap.put(Constant.SetNameCharacteristicUUID, Constant.CustomAudioControlServiceUUID);
        notifyRegisterMap.put(Constant.BatteryLevelCharacteristicUUID, Constant.BatteryInfoServiceUUID);
        notifyRegisterMap.put(Constant.LedControlCharacteristicUUID, Constant.CustomAudioControlServiceUUID);
        notifyRegisterMap.put(Constant.ActionCharacteristicUUID, Constant.CustomAudioControlServiceUUID);
    }

    private static byte[] extractBytes(byte[] scanRecord, int start, int length) {
        byte[] bytes = new byte[length];
        System.arraycopy(scanRecord, start, bytes, 0, length);
        return bytes;
    }

    public interface UpdatesDelegate{
        void didUpdateWifi(WifiBean wifiBean);
        void didUpdateBleConnectStatus(int status);
        void didUpdateLeDevices(TASystem taSystem, int rssi);
        void didUpdateWifiConnectStatus(int status);
        void didUpdateSourceType(int sourceType);
        void didUpdateDeviceName(String deviceName);
        void didUpdateFirmwareVersion(String firmwareVersion);
        void didUpdateBatteryLevel(int batteryLevel);
        void didUpdateLedAnimation(int ledAnimation);
        void didUpdateLedPattern(int ledPattern);
        void didUpdateBTConnectStatus(int btConnectStatus);
        void didUpdateBTMacAddress(String btMacAddress);
        void didUpdateSerialNumber(String serialNumber);
    }
}

