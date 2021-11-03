

package com.example.controllerlibrary.manager;


import android.content.Context;

import com.qualcomm.qti.iotcontrollersdk.controller.IoTService;
import com.qualcomm.qti.iotcontrollersdk.controller.interfaces.IoTCompletionCallback;
import com.qualcomm.qti.iotcontrollersdk.controller.listeners.IoTAppListener;
import com.qualcomm.qti.iotcontrollersdk.iotsys.resource.attributes.AVSOnboardingErrorAttr.Error;
import com.qualcomm.qti.iotcontrollersdk.iotsys.resource.attributes.AirPlayStereoAttr;
import com.qualcomm.qti.iotcontrollersdk.iotsys.resource.attributes.StereoAttr;
import com.qualcomm.qti.iotcontrollersdk.model.iotsys.IoTSysInfo;
import com.qualcomm.qti.iotcontrollersdk.repository.IoTDevice;
import com.qualcomm.qti.iotcontrollersdk.repository.IoTBluetoothDevice;
import com.qualcomm.qti.iotcontrollersdk.repository.IoTDevice.IoTSysUpdatesDelegate;
import com.qualcomm.qti.iotcontrollersdk.repository.IoTDevice.IoTVoiceUIClient;
import com.qualcomm.qti.iotcontrollersdk.iotsys.resource.attributes.BatteryStatusAttr;
import com.qualcomm.qti.iotcontrollersdk.repository.IoTGroup;
import com.qualcomm.qti.iotcontrollersdk.repository.OtaStatusBean;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class IoTSysManager implements IoTAppListener, IoTSysUpdatesDelegate {

  private static IoTSysManager mInstance = new IoTSysManager();

  private IoTService mIoTService;

  private WeakReference<Context> mContextRef;
  private final List<onVoiceUiListener> mVoiceUiListeners = new ArrayList<>();
  private final List<onSystemListener> mSystemListeners = new ArrayList<>();
  private final List<onZigbeeListener> mZigbeeListeners = new ArrayList<>();
  private final List<OnBluetoothListener> mOnBluetoothListeners = new ArrayList<>();
  private final List<onIoTDeviceListener> mIoTDeviceListeners = new ArrayList<>();
  private final List<onStereoListener> mStereoListeners = new ArrayList<>();
  private final List<onAirPlayStereoListener> mAirPlayStereoListeners = new ArrayList<>();
  private final List<onOtaListener> mOtaListeners = new ArrayList<>();
  private final List<onSourceSwitchListener> mSourceSwitchListeners = new ArrayList<>();
  private final List<onAirplayListener> mAirplayListeners = new ArrayList<>();
  private final List<onMusicSourceListener> mMusicSourceListeners = new ArrayList<>();

  public interface onIoTDeviceListener {
        /**
         *  Found device via internet this method will call back
         *
         * @param ioTDevice
         *                The device such as speaker
         */
        void onDeviceAdded(IoTDevice ioTDevice);

        /**
         *  When device is removed this method will call back
         *
         */
        void onRemoveIoTDevice();
    }


    /**
     * Add IoTDeviceListener, if have some update the onIoTDeviceListener interfaces will receive notify
     *
     * @param listener
     * @see onIoTDeviceListener
     */
    public void addIoTDeviceListener(onIoTDeviceListener listener) {
        synchronized (mIoTDeviceListeners) {
            if (listener != null && !mIoTDeviceListeners.contains(listener)) {
                mIoTDeviceListeners.add(listener);
            }
        }
    }

    /**
     * Add IoTDeviceListener, if have some update the onIoTDeviceListener interfaces will not receive notify
     *
     * @param listener
     * @see onIoTDeviceListener
     */
    public void removeIoTDeviceListener(onIoTDeviceListener listener) {
        synchronized (mIoTDeviceListeners) {
            if (listener != null) {
                mIoTDeviceListeners.remove(listener);
            }
        }
    }

    public void addStereoListener(onStereoListener listener) {
        synchronized (mStereoListeners) {
            if (listener != null && !mStereoListeners.contains(listener)) {
                mStereoListeners.add(listener);
            }
        }
    }

    public void removeStereoListener(onStereoListener listener) {
        synchronized (mStereoListeners) {
            if (listener != null) {
                mStereoListeners.remove(listener);
            }
        }
    }
    public void addAirPlayStereoListener(onAirPlayStereoListener listener) {
        synchronized (mAirPlayStereoListeners) {
            if (listener != null && !mAirPlayStereoListeners.contains(listener)) {
                mAirPlayStereoListeners.add(listener);
            }
        }
    }

    public void removeAirPlayStereoListener(onAirPlayStereoListener listener) {
        synchronized (mAirPlayStereoListeners) {
            if (listener != null) {
                mAirPlayStereoListeners.remove(listener);
            }
        }
    }

    public void addOtaListener(onOtaListener listener) {
        synchronized (mOtaListeners) {
            if (listener != null && !mOtaListeners.contains(listener)) {
                mOtaListeners.add(listener);
            }
        }
    }

    public void removeOtaListener(onOtaListener listener) {
        synchronized (mOtaListeners) {
            if (listener != null) {
                mOtaListeners.remove(listener);
            }
        }
    }

    public void addSourceSwitchListener(onSourceSwitchListener listener) {
        synchronized (mSourceSwitchListeners) {
            if (listener != null && !mSourceSwitchListeners.contains(listener)) {
                mSourceSwitchListeners.add(listener);
            }
        }
    }

    public void removeSourceSwitchListener(onSourceSwitchListener listener) {
        synchronized (mSourceSwitchListeners) {
            if (listener != null) {
                mSourceSwitchListeners.remove(listener);
            }
        }
    }

    public void addAirplayListener(onAirplayListener listener){
       synchronized (mAirplayListeners) {
           if(listener != null && !mAirplayListeners.contains(listener)){
               mAirplayListeners.add(listener);
           }
       }
    }

    public void removeAirPlayListener(onAirplayListener listener){
        synchronized (mAirplayListeners) {
            if(listener != null){
                mAirplayListeners.remove(listener);
            }
        }
    }
    public void addMusicSourceListener(onMusicSourceListener listener){
        synchronized (mMusicSourceListeners) {
            if(listener != null && !mMusicSourceListeners.contains(listener)){
                mMusicSourceListeners.add(listener);
            }
        }
    }

    public void removeMusicSourceListener(onMusicSourceListener listener){
        synchronized (mMusicSourceListeners) {
            if(listener != null){
                mMusicSourceListeners.remove(listener);
            }
        }
    }
    @Override
    public void onPlayerGroupAdd(IoTGroup ioTGroup) {

    }

    @Override
    public void onPlayerGroupRemoved(IoTGroup ioTGroup) {

    }

    @Override
    public void onPlayerGroupChanged(IoTGroup ioTGroup) {

    }

    @Override
    public void onPlayerReDiscovered() {

    }

    @Override
    public void onSurroundPlayerDiscovered(String s, String s1) {

    }

    @Override
    public void onDeviceAdded(IoTDevice ioTDevice) {
        synchronized (mIoTDeviceListeners) {
            for (onIoTDeviceListener listener : mIoTDeviceListeners) {
                listener.onDeviceAdded(ioTDevice);
            }
        }
    }

    @Override
    public void onRemoveIoTDevice() {
        synchronized (mIoTDeviceListeners) {
            for (onIoTDeviceListener listener : mIoTDeviceListeners) {
                listener.onRemoveIoTDevice();
            }
        }
    }

    public interface onVoiceUiListener {

    default void voiceUIClientsDidChange(List<IoTVoiceUIClient> voiceUIClients) {
    }

    default void voiceUIEnabledStateDidChange(boolean enabled) {
    }

    default void voiceUIDefaultClientDidChange(IoTVoiceUIClient voiceUIClient) {
    }

    default void voiceUIDidProvideAVSAuthenticationCode(String code, String url) {
    }

    default void voiceUIOnboardingDidErrorWithTimeout(Error didTimeout, int reattempt) {
    }

    }

  public boolean isZbCoordinator(String deviceId) {
      return IoTService.getInstance().isZbCoordinator(deviceId);
  }

  public boolean isZigbeeCoordinatorExisted(String deviceId) {
    return IoTService.getInstance().isZbCoordinatorExisted(deviceId);
  }

	public interface onSystemListener {
        /**
         * If change device name success, this method will call back
         *
         *  @param device
         *              The IoT device for which the state has changed.
          * @param name
         *             Changed name
         */
      void didChangeName(IoTDevice device, String name);
      void deviceDidChangeBatteryState(BatteryStatusAttr attr);

        /**
         * If led pattern is changed or you change success via setLedPattern method, this method will call back
         *
         * @param device
         *              The IoT device for which the state has changed.
         * @param ledPattern
         *              The led pattern will be define on FW side, the pattern value is 0 to 10
         */
      void deviceDidChangeLedPattern(IoTDevice device, int ledPattern);

        /**
         * If led animation is changed or you change success via setLedAnimation method, this method will call back
         *
         * @param device
         *              The IoT device for which the state has changed.
         * @param ledAnimation
         *              The led animation will be define on FW side, the animation value is 0 to 2
         */
      void deviceDidChangeLedAnimation(IoTDevice device, int ledAnimation);
	}

	public interface onStereoListener{
        /**
         * If stereo state is changed or you change success via setStereo method, this method will call back
         *
         * @param device
         *              The IoT device for which the state has changed.
         * @param stereoAttr
         *              The stereoAttr contain groupid and stereo type (Type value 0 to 2 )
         *
         *              when groupId and stereo type both are 0 , that is unPair status
         *
         *              type value 0 is stereo
         *              type value 1 is left
         *              type value 2 is right
         */

        void deviceDidChangeStereoState(IoTDevice device, StereoAttr stereoAttr);
    }

    public interface onAirPlayStereoListener{
        /**
         * If stereo state is changed or you change success via setStereo method, this method will call back
         *
         * @param device
         *              The IoT device for which the state has changed.
         * @param stereoAttr
         *              The stereoAttr contain groupid and stereo type (Type value 0 to 2 )
         *              The masterVerify default value is 0 (masterVerify value 0 to n)
         *              The masterSerialNumber default value is ""
         *
         *              masterVerify value 0 is airplayStereo
         *              masterVerify value 1 is true master
         *              masterVerify value n is true slave
         *
         *              when groupId and stereo type both are 0
         *
         *              type value 0 is stereo
         *              type value 1 is left
         *              type value 2 is right
         */

        void deviceDidChangeAirPlayStereoState(IoTDevice device, AirPlayStereoAttr stereoAttr);
    }
    public interface onMusicSourceListener{
        /**
         *  Notification that the music Source status for a device has changed.
         *
         * @param ioTSourceStatus  The status of music Source.
         */
        void deviceDidNotifyMusicSourceStatus(IoTDevice ioTDevice,int ioTSourceStatus);

    }

    public interface onOtaListener{
        /**
         *  Notification that the ota status for a device has changed.
         *
         * @param ioTDevice  device the device that has been affected.
         * @param ioTOtaStatus status the new ota status.
         * @param progress progress the new ota progress from 0 to 100. Currently this value only valid when ota status is downloading.
         * @param version  version the version for current ota file. This value only valid when ota status is not none.
         */

        void deviceDidChangeOtaStatus(IoTDevice ioTDevice, IoTDevice.IoTOtaStatus ioTOtaStatus, int progress, String version);
    }

    public interface onSourceSwitchListener{
        /**
         *  If source type is changed or you switch source via switchSource method, this method will call back
         *
         * @param ioTDevice device the device that has been affected.
         *
         * @param sourceType the current source type that has changed
         */
        void deviceDidChangeSourceType(IoTDevice ioTDevice, IoTDevice.SourceType sourceType);
    }

    public interface onAirplayListener{
        /**
         *
         *  Notification that the AirPlay home status for a device has changed.
         *  @param ioTDevice the device that has been affected.
         *  @param airplayHomeStatus the new home status for the speaker. the value 0 is not joined, the value 1 is joined
         *
         *  @warning According to Apple's requirement, device name should not be change whenever device join to Apple Home. The App should disable rename feature when home status is join.
         *
         */
        void deviceDidChangeAirplayHomeStatus(IoTDevice ioTDevice, int airplayHomeStatus);
    }
	public interface onZigbeeListener {
      void onZbAdapterStateChanged(IoTDevice device);
      void onZbCoordinatorStateDidChanged(IoTDevice device);
      void onZbJoinedDevicesDidChanged(IoTDevice device);
      void OnZbJoiningStateDidChanged(IoTDevice device, boolean allowed);
	}

  /**
    * <p>This listener gets events related to the Bluetooth feature of a device. It informs when any of
    * its state change.</p>
    */
   public interface OnBluetoothListener {
     /**
      * <p>Notifies the enable state of the Bluetooth Adapter of the provided device.</p>
      *
      * @param device
      *         The device for which the Bluetooth adapter state has been updated.
      * @param state
      *         The new state of the Bluetooth adapter.
      */
     void onBluetoothAdapterStateChanged(IoTDevice device, IoTDevice.IoTBluetoothAdapterState state);

     /**
      * <p>Notifies the new Discoverable state of a device.</p>
      *
      * @param device
      *         The device for which the Discoverable state has been updated.
      * @param state
      *         The new Discoverable state.
      */
     void onBluetoothDiscoverableStateChanged(IoTDevice device, IoTDevice.IoTBluetoothDiscoverableState state);
     /**
      * <p>Notifies the updated list of devices paired with the given device.</p>
      *
      * @param device
      *         The device for which the Discoverable state has been updated.
      * @param devices
      *         The updated list of paired devices.
      */
     void onPairedDevicesUpdated(IoTDevice device, List<IoTBluetoothDevice> devices);
     /**
      * <p>Notifies a Bluetooth error which has occurred on the device.</p>
      *
      * @param device
      *         The device for which the Discoverable state has been updated.
      * @param error
      *         The error which occurs.
      */
     void onBluetoothError(IoTDevice device, IoTDevice.IoTBluetoothError error);
     /**
      * <p>Notifies the Bluetooth connection state of the Bluetooth device with the IoT device.</p>
      *
      * @param device
      *          The IoT device the Bluetooth device is connected with/disconnected from.
      * @param bluetoothDevice
      *          The Bluetooth device which is connected/disconnected.
      */
     void onConnectedBluetoothDeviceChanged(IoTDevice device, IoTBluetoothDevice bluetoothDevice);
     /**
      * <p>Notifies the Bluetooth scan state of the IoT device.</p>
      *
      * @param device
      *          The IoT device for which the state has changed.
      * @param scanning
      *          True if the device is scanning, false otherwise.
      */
     void onBluetoothScanStateChanged(IoTDevice device, boolean scanning);
     /**
      * <p>Notifies the discovery of a Bluetooth device while scanning for devices.</p>
      *
      * @param device The IoT device for which the state has changed.
      * @param scanned The Bluetooth device which has been found.
      */
     void onBluetoothDeviceDiscovered(IoTDevice device, IoTBluetoothDevice scanned);
     /**
      * <p>Notifies when a device has been paired or unpaired with the IoTDevice.</p>
      *
      * @param device The IoT device for which the state has changed.
      * @param pairDevice the device which has been paired and unpaired.
      * @param paired the new pair status between the IoT device and the Bluetooth device.
      */
     void onBluetoothPairStateUpdated(IoTDevice device, IoTBluetoothDevice pairDevice, boolean paired);
   }

  private IoTSysManager() {

  }

  public static void init(Context context) {
    mInstance.mContextRef = new WeakReference<>(context);
    IoTService.getInstance().setIoTSysDelegate(mInstance);
  }

  public static IoTSysManager getInstance() throws Exception {
    if(mInstance.mContextRef == null || mInstance.mContextRef.get() == null)
      throw new Exception("no IoTSys manager instance created!");
      return mInstance;
  }

   /**
    *   The method will start discover device on connected wifi.
    *   When you start app , after you call IoTSysManager init method then you should call this method to
    *   discover device 
    *
    */
  public synchronized void start() {
        mIoTService = IoTService.getInstance();
        mIoTService.setAppListener(this);
        mIoTService.start(mContextRef.get());
   }


  public void addVoiceUiListener(onVoiceUiListener listener) {
    if(listener != null && !mVoiceUiListeners.contains(listener)) {
      mVoiceUiListeners.add(listener);
    }
  }

  public void addSystemListener(onSystemListener listener) {
    if(listener != null && !mSystemListeners.contains(listener)) {
      mSystemListeners.add(listener);
    }
  }

  public void removeVoiceUiListener(onVoiceUiListener listener) {
    if(listener != null) {
      mVoiceUiListeners.remove(listener);
    }
  }

  public void removeSystemListener(onSystemListener listener) {
    if(listener != null ) {
      mSystemListeners.remove(listener);
    }
  }

    /**
     *  Use this method will set device name that you want
     *
     * @param device  Current device (Speaker) , you want to change name's device
     * @param name    The name want to set
     * @param callback Call back if you change success
     */
  public void setDeviceName(IoTDevice device, String name, IoTCompletionCallback callback) {
      device.setDeviceName(name, callback);
  }

    /**
     *  Use this method will set led pattern that you want
     *
     * @param device   Current device (Speaker) , you want to change led pattern's device
     * @param ledPattern  The led pattern want to set, the value is 0 to 5 and 10
     * @param callback  Call back if you change success
     */
  public void setLedPattern(IoTDevice device, int ledPattern, IoTCompletionCallback callback){
       device.setLedPattern(ledPattern,callback);
  }

    /**
     *  Use this method will set led animation that you want
     *
     * @param device   Current device (Speaker) , you want to change led animation's device
     * @param ledAnimation  The led animation want to set, the value is 0 to 3
     * @param callback  Call back if you change success
     */
  public void setLedAnimation(IoTDevice device, int ledAnimation, IoTCompletionCallback callback){
        device.setLedAnimation(ledAnimation,callback);
  }

    /**
     *  Use this method will set stereo pair, when two devices in a group you set groupId and stereo type both are 0 that is unPair
     *
     * @param device   Current device (Speaker) , you want to set stereoPair device
     *
     * @param groupId  The groupId is a unique number randomly generated, When stereo pair mode there are two devices must be same groupID
     *
     * @param stereoType  The value is 0 to 2
     *
     *              type value 0 is stereo
     *              type value 1 is left
     *              type value 2 is right
     *
     * @param callback  Call back if you change success
     */

  public void setStereo(IoTDevice device, int groupId, int stereoType, IoTCompletionCallback callback){
        device.setStereo(groupId, stereoType, callback);
  }
    /**
     *  Use this method will set AirPlaystereo, when two devices in a group you set groupId and stereo and masterVerify and type both are 0 that is unPair
     *
     * @param device   Current device (Speaker) , you want to set stereoPair device
     *
     * @param groupId  The groupId is a unique number randomly generated, When stereo pair mode there are two devices must be same groupID
     *
     * @param stereoType  The value is 0 to 2
     *
     *              type value 0 is stereo
     *              type value 1 is left
     *              type value 2 is right
     * @param masterVerify  The value is 0 to n
     *
     *              type value 0 is stereo
     *              type value 1 is master
     *              type value 2 is slave
     *                     ......
     *              type value n is slave
     *
     * @param masterSerialNumber  The value is the Master speaker's SerialNumber
     *
     *

     * @param callback  Call back if you change success
     */
    public void setAirPlayStereo(IoTDevice device, int groupId, int stereoType,int masterVerify,String masterSerialNumber,IoTCompletionCallback callback){
        device.setAirPlayStereo(groupId,stereoType,masterVerify,masterSerialNumber,callback);
    }

    /**
     *  Asynchronously dispatch request to download firmware of the speaker.
     *
     * @param device Current device (Speaker), you want to start download firmware
     * @param firmwareUrl  firmwareUrl the download url for the firmware ota file.
     * @param checksum   checksum the checksum of firmware ota file.
     * @param name  name the user name for the ota server.
     * @param pwd  pwd the password of target user for the ota server.
     * @param version  version the version of the firmware.
     * @param packageName   packageName the package name of the firmware ota file.
     * @param callback  completion block to be called asynchronously upon completion (successful or otherwise).
     */
  public void startDownloadFirmware(IoTDevice device,String firmwareUrl, String checksum, String name, String pwd, String version, String packageName, IoTCompletionCallback callback){
        device.startDownloadFirmware(firmwareUrl, checksum, name, pwd, version, packageName, callback);
  }

    /**
     *  Asynchronously dispatch request to start ota update of the speaker.
     *
     * @param device   Current device (Speaker), you want to start ota
     * @param packageName  packageName the package name of the firmware ota file.
     * @param checksum  checksum the checksum of the firmware ota file.
     * @param callback  completion block to be called asynchronously upon completion (successful or otherwise).
     */
  public void startOtaUpdate(IoTDevice device, String packageName, String checksum, IoTCompletionCallback callback){
        device.startOtaUpdate(packageName, checksum, callback);
  }

    /**
     *  use this method will switch source type, but this method only can switch wifi to bt via IoTivity
     *
     * @param device   current device (Speaker), you want to switch source
     * @see IoTDevice.SourceType  sourceType contains wifi and bt
     * @param callback  completion block to be called asynchronously upon completion (successful or otherwise)
     */
  public void switchSource(IoTDevice device, IoTDevice.SourceType sourceType, IoTCompletionCallback callback){
      device.switchSource(sourceType, callback);
  }

  public void setZigbeeName(String host, String name, int id, IoTCompletionCallback callback) {
      IoTService.getInstance().setZigbeeName(host, name, id, callback);
  }

    /**
     *  Use this method will reboot current device (Speaker)
     *
     * @param device   current device (Speaker), you want to reboot
     *
     * @param callback completion block to be called asynchronously upon completion (successful or otherwise)
     */
    public void rebootDevice(IoTDevice device, IoTCompletionCallback callback){
        device.rebootDevice(callback);
    }

    /**
     *  Use this method will factory reset for current device (Speaker)
     *
     * @param device   current device (Speaker), that you want factory reset
     *
     * @param callback completion block to be called asynchronously upon completion (successful or otherwise)
     */
    public void factoryReset(IoTDevice device, IoTCompletionCallback callback){
        device.factoryReset(callback);
    }

    public void startAvsOnBoarding(String host, IoTCompletionCallback callback){
        IoTService.getInstance().startAvsOnBoarding(host, callback);
    }

    public String getModel(String host){
        return IoTService.getInstance().getModel(host);
    }

    public String getManufacturer(String host){
        return IoTService.getInstance().getManufacturer(host);
    }

    public String getWifiIPAddress(IoTDevice ioTDevice){
        return ioTDevice.getWifiIPAddress();
    }

    public String getWifiMacAddress(IoTDevice ioTDevice){
        return ioTDevice.getWifiMacAddress();
    }

    public String getAccessWifiSSID(IoTDevice ioTDevice){
        return ioTDevice.getAccessWifiSSID();
    }

    public String getAccessWifiRSSI(IoTDevice ioTDevice){
        return ioTDevice.getAccessWifiRSSI();
    }

    public String getEthernetIPAddress(String host){
        return IoTService.getInstance().getEthernetIPAddress(host);
    }

    public String getEthernetMacAddress(String host){
        return IoTService.getInstance().getEthernetMacAddress(host);
    }

    public IoTSysInfo getIoTSysInfo(String host){
        return IoTService.getInstance().getIoTSysInfo(host);
    }

    public int getLedPattern(IoTDevice ioTDevice){
        return ioTDevice.getLedPattern();
    }

    public int getLedAnimation(IoTDevice ioTDevice){
        return ioTDevice.getLedAnimation();
    }

    public int getStereoType(IoTDevice ioTDevice){
        return ioTDevice.getStereoType();
    }

    public int getStereoGroupId(IoTDevice ioTDevice){
      return  ioTDevice.getStereoGroupId();
    }

    public int getAirPlayStereoType(IoTDevice ioTDevice){
        return ioTDevice.getAirPlayStereoType();
    }

    public int getAirPlayStereoGroupId(IoTDevice ioTDevice){
        return  ioTDevice.getAirPlayStereoGroupId();
    }

    public int getAirPlayStereoMasterVerify(IoTDevice ioTDevice){
        return  ioTDevice.getAirPlayStereoMasterVerify();
    }

    public String getAirPlayStereoMasterSerialNumber(IoTDevice ioTDevice){
        return  ioTDevice.getAirPlayStereoMasterSerialNumber();
    }
    public String getFrimwareVersion(IoTDevice ioTDevice){
      return  ioTDevice.getFirmwareVersion();
    }

    /**
     *
     * @param ioTDevice
     *
     * @return
     *
     *     There are three status only occur in auto-ota, they not have notify , only through this method read.
     *
     *     IoTOtaStatusParseXmlFailed, // Speaker fail to parse xml, only occur in auto-ota
     *
     *     IoTOtaStatusForceUpgradeFalse,  // Speaker fail to force ota, only occur in auto-ota
     *
     *     IoTOtaStatusSmallerVersion  // Speaker detect server exist smaller version, only occur in auto-ota
     *
     */

    public IoTDevice.IoTOtaStatus getIoTOtaStatus(IoTDevice ioTDevice){
      return ioTDevice.getIoTOtaStatus();
    }

    public IoTDevice.SourceType getSourceType(IoTDevice ioTDevice){
      return ioTDevice.getSourceType();
    }

    public int getBatteryLevel(IoTDevice ioTDevice){
      return ioTDevice.getBatteryLevel();
    }

    public String getSerialNumber(IoTDevice ioTDevice){
      return ioTDevice.getSerialNumber();
    }

    /**
     * @param ioTDevice
     *
     * Use this method get home status of current speaker, the value 0 is not joined, the value 1 is joined.
     *
     * The method didUpdateAirplayHomeStatus will call back.
     *
     * According to Apple's requirement, device name should not be change whenever device join to Apple Home.
     */
    public int getAirplayHomeStatus(IoTDevice ioTDevice){
      return ioTDevice.getAirplayHomeStatus();
    }

    public int getMusicSourceStatus(IoTDevice ioTDevice){
        return 0;
    }
  @Override
  public void didChangeName(IoTDevice device, String name) {
    synchronized (mSystemListeners) {
      for (onSystemListener listener : mSystemListeners) {
            listener.didChangeName(device, name);
      }
    }
  }

  @Override
  public void deviceDidChangeBatteryState(BatteryStatusAttr attr) {
    synchronized (mSystemListeners) {
      for (onSystemListener listener : mSystemListeners) {
        listener.deviceDidChangeBatteryState(attr);
      }
    }
  }

    @Override
  public void deviceDidChangeLedPattern(IoTDevice device, int ledPattern) {
       synchronized (mSystemListeners) {
            for (onSystemListener listener : mSystemListeners) {
                listener.deviceDidChangeLedPattern(device, ledPattern);
            }
        }
   }

    @Override
  public void deviceDidChangeLedAnimation(IoTDevice device, int ledAnimation) {
        synchronized (mSystemListeners) {
            for (onSystemListener listener : mSystemListeners) {
                listener.deviceDidChangeLedAnimation(device, ledAnimation);
            }
        }
  }


    @Override
    public void deviceDidChangeStereoState(IoTDevice device, StereoAttr stereoAttr) {
        synchronized (mStereoListeners) {
            for (onStereoListener listener : mStereoListeners) {
                listener.deviceDidChangeStereoState(device,stereoAttr);
            }
        }
    }

    @Override
    public void deviceDidChangeAirPlayStereoState(IoTDevice ioTDevice, AirPlayStereoAttr airPlayStereoAttr) {
        synchronized (mAirPlayStereoListeners) {
            for (onAirPlayStereoListener listener : mAirPlayStereoListeners) {
                listener.deviceDidChangeAirPlayStereoState(ioTDevice,airPlayStereoAttr);
            }
        }
    }

    @Override
    public void deviceDidChangeOtaStatus(IoTDevice ioTDevice, IoTDevice.IoTOtaStatus ioTOtaStatus, OtaStatusBean otaStatusBean) {
        synchronized (mOtaListeners){
            for (onOtaListener listener : mOtaListeners){
                listener.deviceDidChangeOtaStatus(ioTDevice, ioTOtaStatus, otaStatusBean.getProgress(), otaStatusBean.getVersion());
            }
        }
    }

    @Override
    public void deviceDidChangeSourceType(IoTDevice ioTDevice, IoTDevice.SourceType sourceType) {
        synchronized (mSourceSwitchListeners){
            for (onSourceSwitchListener listener : mSourceSwitchListeners){
                listener.deviceDidChangeSourceType(ioTDevice, sourceType);
            }
        }
    }

    @Override
    public void deviceDidChangAirplayHomeStatus(IoTDevice ioTDevice, int airplayHomeStatus) {
        synchronized (mAirplayListeners){
            for (onAirplayListener listener : mAirplayListeners){
                listener.deviceDidChangeAirplayHomeStatus(ioTDevice, airplayHomeStatus);
            }
        }
    }

    @Override
    public void deviceDidChangeEthernetState(IoTDevice ioTDevice, String ipAddress, String macAddress, boolean connectState) {

    }

    @Override
    public void deviceDidChangeWiFiState(IoTDevice ioTDevice, String ipAddress, String macAddress, boolean connectState) {

    }

    @Override
    public void deviceDidChangeAccessPointState(IoTDevice ioTDevice, String rssi, String ssid) {

    }


  /**
    * <p>This method unregisters the given listener from the list of listeners.</p>
    *
    * @param listener
    *         The listener to unregister.
    */
   public void removeOnBluetoothListener(final OnBluetoothListener listener) {
     if (listener != null) {
       synchronized (mOnBluetoothListeners) {
         mOnBluetoothListeners.remove(listener);
       }
     }
   }

 /**
    * <p>This method registers the given listener for Bluetooth State events.</p>
    *
    * @param listener
    *         The listener to register.
    */
   public void addOnBluetoothListener(final OnBluetoothListener listener) {
     if (listener != null) {
       synchronized (mOnBluetoothListeners) {
         if (!mOnBluetoothListeners.contains(listener)) {
           mOnBluetoothListeners.add(listener);
         }
       }
     }
   }

  @Override // IoTDevice.IoTSysUpdatesDelegate
   public void btScanDidDiscoverBtDevice(IoTDevice device, IoTBluetoothDevice scanned) {
     synchronized (mOnBluetoothListeners) {
       for (OnBluetoothListener listener : mOnBluetoothListeners) {
         listener.onBluetoothDeviceDiscovered(device, scanned);
       }
     }
   }

  @Override // IoTDevice.IoTSysUpdatesDelegate
   public void btScanStateDidChange(IoTDevice device, boolean scanning) {
     synchronized (mOnBluetoothListeners) {
       for (OnBluetoothListener listener : mOnBluetoothListeners) {
         listener.onBluetoothScanStateChanged(device, scanning);
       }
     }
   }

  @Override // IoTDevice.IoTSysUpdatesDelegate
   public void btAdapterStateDidChange(IoTDevice device, IoTDevice.IoTBluetoothAdapterState state) {
     synchronized (mOnBluetoothListeners) {
       for (OnBluetoothListener listener : mOnBluetoothListeners) {
         listener.onBluetoothAdapterStateChanged(device, state);
       }
     }
   }

  public void btDiscoverableStateDidChange(IoTDevice device, IoTDevice.IoTBluetoothDiscoverableState state) {
     synchronized (mOnBluetoothListeners) {
       for (OnBluetoothListener listener : mOnBluetoothListeners) {
         listener.onBluetoothDiscoverableStateChanged(device, state);
       }
     }
   }

  @Override // IoTDevice.IoTSysUpdatesDelegate
   public void btConnectedDeviceHasChanged(IoTDevice device, IoTBluetoothDevice bluetoothDevice) {
     synchronized (mOnBluetoothListeners) {
       for (OnBluetoothListener listener : mOnBluetoothListeners) {
         listener.onConnectedBluetoothDeviceChanged(device, bluetoothDevice);
       }
     }
   }

   @Override // IoTDevice.IoTSysUpdatesDelegate
   public void btPairedDevicesDidChange(IoTDevice device, List<IoTBluetoothDevice> devices) {
     synchronized (mOnBluetoothListeners) {
       for (OnBluetoothListener listener : mOnBluetoothListeners) {
         listener.onPairedDevicesUpdated(device, devices);
       }
     }
   }

   @Override // IoTDevice.IoTSysUpdatesDelegate
   public void btPairStateDidChange(IoTDevice device, IoTBluetoothDevice pairDevice, boolean paired) {
     synchronized (mOnBluetoothListeners) {
       for (OnBluetoothListener listener : mOnBluetoothListeners) {
         listener.onBluetoothPairStateUpdated(device, pairDevice, paired);
       }
     }
   }

  @Override // IoTDevice.IoTSysUpdatesDelegate
   public void btError(IoTDevice device, IoTDevice.IoTBluetoothError error) {
     synchronized (mOnBluetoothListeners) {
       for (OnBluetoothListener listener : mOnBluetoothListeners) {
         listener.onBluetoothError(device, error);
       }
     }
  }

  @Override
  public void voiceUIClientsDidChange(List<IoTVoiceUIClient> voiceUIClients) {
    synchronized (mVoiceUiListeners) {
      for (onVoiceUiListener listener : mVoiceUiListeners) {
        listener.voiceUIClientsDidChange(voiceUIClients);
      }
    }
  }

  @Override
  public void voiceUIEnabledStateDidChange(boolean enabled) {
    synchronized (mVoiceUiListeners) {
      for (onVoiceUiListener listener : mVoiceUiListeners) {
        listener.voiceUIEnabledStateDidChange(enabled);
      }
    }
  }

  @Override
  public void voiceUIDefaultClientDidChange(IoTVoiceUIClient voiceUIClient) {
    synchronized (mVoiceUiListeners) {
      for (onVoiceUiListener listener : mVoiceUiListeners) {
        listener.voiceUIDefaultClientDidChange(voiceUIClient);
      }
    }
  }

  @Override
  public void voiceUIDidProvideAVSAuthenticationCode(String code, String url) {
    synchronized (mVoiceUiListeners) {
      for (onVoiceUiListener listener : mVoiceUiListeners) {
        listener.voiceUIDidProvideAVSAuthenticationCode(code, url);
      }
    }
  }

  @Override
  public void voiceUIOnboardingDidErrorWithTimeout(Error error, int reattempt) {
    synchronized (mVoiceUiListeners) {
      for (onVoiceUiListener listener : mVoiceUiListeners) {
        listener.voiceUIOnboardingDidErrorWithTimeout(error, reattempt);
      }
    }
  }

  public void addZigbeeiListener(onZigbeeListener listener) {
    synchronized (mZigbeeListeners) {
      if (listener != null && !mZigbeeListeners.contains(listener)) {
        mZigbeeListeners.add(listener);
      }
    }
  }

  public void removeZigbeeListener(onZigbeeListener listener) {
    synchronized (mZigbeeListeners) {
      if (listener != null) {
        mZigbeeListeners.remove(listener);
      }
    }
  }

  @Override
  public void onZbAdapterStateChanged(IoTDevice device) {
    synchronized (mZigbeeListeners) {
    for (onZigbeeListener listener : mZigbeeListeners) {
      listener.onZbAdapterStateChanged(device);
     }
    }
  }

  @Override
  public void onZbCoordinatorStateDidChanged(IoTDevice device) {
    synchronized (mZigbeeListeners) {
    for (onZigbeeListener listener : mZigbeeListeners) {
      listener.onZbCoordinatorStateDidChanged(device);
     }
    }
  }

  @Override
  public void onZbJoinedDevicesDidChanged(IoTDevice device) {
    synchronized (mZigbeeListeners) {
    for (onZigbeeListener listener : mZigbeeListeners) {
      listener.onZbJoinedDevicesDidChanged(device);
     }
    }
  }

  @Override
  public void OnZbJoiningStateDidChanged(IoTDevice device, boolean allowed) {
    synchronized (mZigbeeListeners) {
    for (onZigbeeListener listener : mZigbeeListeners) {
      listener.OnZbJoiningStateDidChanged(device, allowed);
     }
    }
  }
}
