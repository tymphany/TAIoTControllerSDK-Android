# TAIoTControllerSDK
This is the Tymphany smart audio SDK for Android, based on the provided IoTControllerSDK by Qualcomm, to communicate with Tymphany SmartSDK on QCS40X platform.


## Requirements

| TAIoTControllerSDK Version | minSdkVersion  |                            Notes                                   |
|:--------------------:|:----------------------------:|:-------------------------------------------------------------------------:|
| 0.x | 25 | java version(1.8) , gradle version(5.1.1) |


## How to integrate the TAIoTController SDK
### Step 1.Add the JitPack repository to your build file
#### Add it in your root build.gradle at the end of repositories:
      allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
### Step 2.Add the dependency in your app build.gradle
	dependencies {
	        implementation 'com.github.tymphany:TAIoTControllerSDK-Android:0.0.1'
	}
  
#### Dependency information
- Group: com.github.Username
- Artifact: Repository Name
- Version: Release tag
  
**[Note]  Every time you update the SDK, you just need to update released tag**


## Communication

- If you'd like to **ask a general question**, contact Rocky.Peng@tymphany.com
- If you **found a bug**, _and can provide steps to reliably reproduce it_, open an issue.
- If you **have a feature request**, open an issue.
- If you **want to contribute**, submit a pull request.


## Architecture

### Wifi BLE onboard

- `BleManager`

### Smart audio control

* `IoTSysManager`
  
## Usage

### BleManager

`BleManager` represents the ble manager that provides a convenient interface to perform wifi ble onboard to device, which conforms to `didUpdateBleConnectStatus(int status);`, `didUpdateWifiList(List<WifiBean> wifiList)`, `didUpdateWifiConnectStatus(int status)` and `didUpdateLeDevices(TASystem taSystem, int rssi)`.

#### init

```java
BleManager.getInstance().init(this);
```

#### add listener or remove listener

```java
BleManager.getInstance().addBleListener(this);

BleManager.getInstance().removeBleListener(this);
```

#### scan BLE device 
```java
  /**
     *  This method to start scan around BLE devices, and the method didUpdateLeDevices will call back
     */
    public void startScan()

  /**
      * The method will call back when start scan around BLE devices , and return BLE device
      *
      * @param taSystem BLE device
      *
      * @param rssi BLE signal strength
     */
    void didUpdateLeDevices(TASystem taSystem, int rssi);
```

#### connect device
```java

   /**
     * Using this method will connect to the specified device via BLE, and the method didUpdateBleConnectStatus will call back
     *
     *
     * @param mac mac address of will connect device
     */
    public void connect(String mac)



    /**
      * The method will call back the BLE connect status when device connected or disconnected
      *
      * @param status  The status value 0 is disconnected , value 2 is connected
      */
       void didUpdateBleConnectStatus(int status);


```


You should connect device first before start the wifi ble onboard.

####  Scan wifi list of device and Configure wifi to device

##### Scan wifi list of device

```java
  /**
   * Using this method will get wifi list from speaker,and the method didUpdateWifiList will call back
   *
   */
  public void readWifiList()


 /**
  * The method will call back when start read wifi list , and return a wifi list
  *
  * @param wifiBean  return wifi object from speaker scan
  */
  void didUpdateWifi(WifiBean wifiBean);    
```

##### Configure wifi to device

```java
    /**
     *  This method send the wifi ssid and password to the speaker via BLE , when the speaker connect this wifi or not connect, the
     *
     *  method didUpdateWifiConnectStatus will call back
     *
     * @param ssid  will connect wifi ssid
     *
     * @param passWord will connect wifi password
     */
    public void connectWifi(String ssid, String passWord)

  /**
    * The method will call back wifi connect status when choice wifi to connect
    *
    * @param status The status value 0 is disconnect, value 1 is connecting, value 2 is connected
    */
    void didUpdateWifiConnectStatus(int status);    
```

##### Read wifi connect status

```java
    /**
     *  Using this method the method didUpdateWifiConnectStatus will call back
     *
     * @see onBleListener
     *
     */
    public void readWifiConnectStatus()

```

**[!!!] Due to current FW limitation, if you input password is right,onboard result will return about 20 seconds after you send ssid and password to device**

##### Source switch via ble
```java

   /**
     * Using this method set source to BT
     *
     */
   public void setSourceToBt()
   
    /**
     * Using this method set source to Wifi
     *
     */
   public void setSourceToWifi()
   
```
##### Read source type
```java
   /**
     *  Using this method to get current source type , and the method didUpdateSourceType will call back
     *
     * @see onBleListener
     *
     */
    public void readSourceType()
    
    
    /**
      *  The method will call back current source type when device source type change , or use readSourceType method to read current
      *
      *  source type
      *
      *  (Note: This method will not call back when use method SetSourceToBt or SetSourceToWifi to set source, due to FW not return. FW will do it in next.
      *
      *   If manually trigger a button on the speaker to switch the source, this method will call back)
      *
      *  @param sourceType The sourceType value 1 is BT, value 2 is Wifi
      */
    void didUpdateSourceType(int sourceType); 
```
##### Set device name
```java
   /**
     *  Using this method will change name of current device
     *
     * @param name  Will set name that want to change of current device
     */
    public void setDeviceName(String name)
```
##### Read device name
```java
  /**
    *  Using this method will get name of current device, and the method didUpdateDeviceName will call back
    *
    */
  public void readDeviceName()
  
  
  /**
    *  This method will call back when use readDeviceName method to get device name
    *
    *
    * @param deviceName  The device name of current device
    */
  void didUpdateDeviceName(String deviceName); 

```
##### Read Firmware version
```java
   /**
     *  Using this method to get firmware version , and the method didUpdateFirmwareVersion will call back
     *
     * @see onBleListener
     *
     */
    public void readFirmwareVersion()
    
    /**
      *  This method will call back when use readFirmwareVersion method to get firmware version
      *
      *
      * @param firmwareVersion  The firmware version of current device
      */
    void didUpdateFirmwareVersion(String firmwareVersion);

```
##### Read Battery level
```java
  /**
    *  Using this method to get current battery level , and the method didUpdateBatteryLevel will call back
    *
    * @see onBleListener
    *
    */
  public void readBatteryLevel()
  
  /**
    *  This method will call back when use readBatteryLevel method to get battery level
    *
    *
    * @param batteryLevel  The battery level of current device
    */
  void didUpdateBatteryLevel(int batteryLevel); 
```
##### Read led pattern and animation
```java
 /**
   * Using this method to get led pattern and animation, both method didUpdateLedAnimation and method didUpdateLedPattern will call back
   *
   * @see onBleListener
   *
   */
  public void readLedPatternAndAnimation()
  
  
 /**
   *  This method will call back when use readBatteryLevel method to get battery level
   *
   *
   * @param batteryLevel  The battery level of current device
   */
  void didUpdateBatteryLevel(int batteryLevel);

 /**
   *  This method will call back when led animation changed or using readLedPatternAndAnimation method get led animation and pattern
   *
   * @param ledAnimation The led animation value of current device
   */
   void didUpdateLedAnimation(int ledAnimation);
```
##### Set led pattern and animation
```java
   /**
     * Use this method will set led pattern that you want
     *
     * @param ledPattern The led pattern want to set, the value is 0 to 5 and 10
     */
 public void setLedPattern(int ledPattern)
 
   /**
     * Use this method will set led animation that you want
     *
     * @param ledAnimation The led animation want to set, the value is 0 to 3
     */
 public void setLedAnimation(int ledAnimation)

```
##### Read device BT info 
```java
    /**
     * Use this method get BT info that is current speaker BT connect status and BT mac address. If the BT connect status value is 1 or 2
     *
     * both method didUpdateBTConnectStatus and method didUpdateBTMacAddress will call back. If BT connect status is 0 then only didUpdateBTConnectStatus method will call back.
     *
     * @see onBleListener
     *
     */
  public void readBTInfo()
 
   /**
     *  This method will call back when use method readBTInfo to read current speaker information
     *
     * @param btConnectStatus
     *        value 0 is the speaker no connected BT device
     *        value 1 is the speaker connected one BT device
     *        value 2 is the speaker connected two BT devices
     */
  void didUpdateBTConnectStatus(int btConnectStatus)
  
   /**
     *  This method will call back when use method readBTInfo to read current speaker information.
     *
     * @param btMacAddress  Current connected speaker's BT mac address.
     *
     *  Due to BLE mac address and BT mac address is different on same one speaker, with the BLE MAC address
     *
     *  can not judge whether the current speaker and mobile phone BT has been connected. Therefore, we can use the BT MAC address
     *
     *  of the currently connected speaker to judge whether the speaker connected by BT of the current mobile phone is the same one connected by BLE
     *
     */
   void didUpdateBTMacAddress(String btMacAddress)
 
```
##### Factory reset
```java
    /**
     *  Use this method will send command to speaker to set factory reset
     *
     */
   public void factoryReset()
```
##### Trigger speaker sound and led effect
```java
   /**
     *  Use this method will send command to speaker trigger sound and led effect
     *
     */
   public void triggerSoundAndLedEffect()
```

### IoTSysManager

The `IoTSysManager` class is related to the `IoTDevice` and `IoTService` in the `IOTControllerSDK.IoTSysManager` encapsulates many methods from `IoTDevice`. Only the methods in `IoTSysManager` need to be used during the development process. or you can use `IoTService` and `IoTDevice` methods

#### init

```java
IoTService.init(Arrays.asList(OCF_RESOURCE_TYPE_IOTSYS), IoTDiscovery.getInstance());

IoTSysManager.init(this);

/**
  *  Using this method start discover device on your connected wifi 
  *
  * 
  */
IoTSysManager.getInstance().start()
```
##### please see on App layer in demo project, The App package below has a `SmartAudioApplication` class to init 

#### add listener or remove listener

```java

IoTSysManager.getInstance().addIoTDeviceListener(this);

IoTSysManager.getInstance().removeIoTDeviceListener(this);

IoTSysManager.getInstance().addSystemListener(this);

IoTSysManager.getInstance().removeSystemListener(this);

IoTSysManager.getInstance().addStereoListener(this);

IoTSysManager.getInstance().removeStereoListener(this);

IoTSysManager.getInstance().addOtaListener(this);

IoTSysManager.getInstance().removeOtaListener(this);

IoTSysManager.getInstance().addSourceSwitchListener(this);

IoTSysManager.getInstance().removeSourceSwitchListener(this);


```

#### Add device or remove device
```java

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

```
##### When the method onRemoveIoTDevice() callback, can use below method get current device
```java
   
   IoTService.getInstance().getAllDevices();
   
```

#### Device setting

```java  

    /**
     *  Use this method will set device name that you want
     *
     * @param device  Current device (Speaker) , you want to change name's device
     * @param name    The name want to set
     * @param callback Call back if you change success
     */
  public void setDeviceName(IoTDevice device, String name, IoTCompletionCallback callback)
  
    /**
      * If change device name success, this method will call back
      *
      *  @param device
      *              The IoT device for which the state has changed.
      * @param name
      *             Changed name
      */
  void didChangeName(IoTDevice device, String name);
  
  
    /**
     *  Use this method will set led pattern that you want
     *
     * @param device   Current device (Speaker) , you want to change led pattern's device
     * @param ledPattern  The led pattern want to set, the value is 0 to 5 and 10
     * @param callback  Call back if you change success
     */
  public void setLedPattern(IoTDevice device, int ledPattern, IoTCompletionCallback callback)
  
  
     /**
       * If led pattern is changed or you change success via setLedPattern method, this method will call back
       *
       * @param device
       *              The IoT device for which the state has changed.
       * @param ledPattern
       *              The led pattern will be define on FW side, the pattern value is 0 to 5 and 10
       */
   void deviceDidChangeLedPattern(IoTDevice device, int ledPattern);
   
   
     /**
     *  Use this method will set led animation that you want
     *
     * @param device   Current device (Speaker) , you want to change led animation's device
     * @param ledAnimation  The led animation want to set, the value is 0 to 3
     * @param callback  Call back if you change success
     */
  public void setLedAnimation(IoTDevice device, int ledAnimation, IoTCompletionCallback callback)
   
  /**
    * If led animation is changed or you change success via setLedAnimation method, this method will call back
    *
    * @param device
    *              The IoT device for which the state has changed.
    * @param ledAnimation
    *              The led animation will be define on FW side, the animation value is 0 to 3
    */
  void deviceDidChangeLedAnimation(IoTDevice device, int ledAnimation);
  
  
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
 
 public void setStereo(IoTDevice device, int groupId, int stereoType, IoTCompletionCallback callback)
 
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
  
  
  
   /**
     *  use this method will switch source type, but this method only can switch wifi to bt via IoTivity
     *
     * @param device   current device (Speaker), you want to switch source
     * @see IoTDevice.SourceType  sourceType contains wifi and bt
     * @param callback  completion block to be called asynchronously upon completion (successful or otherwise)
     */
  public void switchSource(IoTDevice device, IoTDevice.SourceType sourceType, IoTCompletionCallback callback)
  
  
   /**
     *  If source type is changed or you switch source via switchSource method, this method will call back
     *
     * @param ioTDevice device the device that has been affected.
     *
     * @param sourceType the current source type that has changed
     */
  void deviceDidChangeSourceType(IoTDevice ioTDevice, IoTDevice.SourceType sourceType);
  
```
##### and so on

##### Reboot device

```java

     /**
     *  Use this method will reboot current device (Speaker)
     *
     * @param device   current device (Speaker), you want to reboot
     *
     * @param callback completion block to be called asynchronously upon completion (successful or otherwise)
     */
  public void rebootDevice(IoTDevice device, IoTCompletionCallback callback)

```

##### Factory reset

```java
   
     /**
     *  Use this method will factory reset for current device (Speaker)
     *
     * @param device   current device (Speaker), that you want factory reset
     *
     * @param callback completion block to be called asynchronously upon completion (successful or otherwise)
     */
     
    public void factoryReset(IoTDevice device, IoTCompletionCallback callback)

```


##### OTA

```java
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
public void startDownloadFirmware(IoTDevice device,String firmwareUrl, String checksum, String name, String pwd, String version, String packageName, IoTCompletionCallback       callback)   

   /**
     *  Asynchronously dispatch request to start ota update of the speaker.
     *
     * @param device   Current device (Speaker), you want to start ota
     * @param packageName  packageName the package name of the firmware ota file.
     * @param checksum  checksum the checksum of the firmware ota file.
     * @param callback  completion block to be called asynchronously upon completion (successful or otherwise).
     */
public void startOtaUpdate(IoTDevice device, String packageName, String checksum, IoTCompletionCallback callback)


   /**
     *  Notification that the ota status for a device has changed.
     *
     * @param ioTDevice  device the device that has been affected.
     * @param ioTOtaStatus status the new ota status.
     * @param progress progress the new ota progress from 0 to 100. Currently this value only valid when ota status is downloading.
     * @param version  version the version for current ota file. This value only valid when ota status is not none.
     */

void deviceDidChangeOtaStatus(IoTDevice ioTDevice, IoTDevice.IoTOtaStatus ioTOtaStatus, int progress, String version);

```
 
#### Device information

```java

 getFirmwareVersion(IoTDevice ioTDevice)
 
 getLedPattern(IoTDevice ioTDevice)
 
 getLedAnimation(IoTDevice ioTDevice)
 
 getStereoType(IoTDevice ioTDevice)
 
 getStereoGroupId(IoTDevice ioTDevice)
 
 getIoTOtaStatus(IoTDevice ioTDevice)
 
 getAccessWifiSSID(IoTDevice ioTDevice)
 
 getAccessWifiRSSI(IoTDevice ioTDevice)
 
 getWifiIPAddress(IoTDevice ioTDevice)
 
 getWifiMacAddress(IoTDevice ioTDevice)
 
 getSourceType(IoTDevice ioTDevice)
 
```
##### and so on

#### This method is called when exiting an application to disconnect and release resources

```java

   IoTService.getInstance().dispose();
   
```

## Author

RockyPeng, Rocky.Peng@tymphany.com

## License

Contact Tymphany for more info

