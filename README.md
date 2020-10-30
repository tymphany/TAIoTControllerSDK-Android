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

### IoTSysManager

The `IoTSysManager` class is related to the `IoTDevice` and `IoTService` in the `IOTControllerSDK.IoTSysManager` encapsulates many methods from `IoTDevice`. Only the methods in `IoTSysManager` need to be used during the development process. or you can use `IoTService` and `IoTDevice` methods

#### init

```java
IoTService.init(Arrays.asList(OCF_RESOURCE_TYPE_ALLPLAY, OCF_RESOURCE_TYPE_IOTSYS), IoTDiscovery.getInstance());

IoTSysManager.init(this);
```
##### please see on App layer in demo project, The App package below has a `SmartAudioApplication` class to init 

#### Device setting
```java

  rebootDevice(String id, IoTCompletionCallback callback)

  setDeviceName(String host, String name, IoTCompletionCallback callback)

```
##### and so on
 
#### Device information

```java

 getWifiIPAddress(String host)

 getWifiMacAddress(String host)

 getFirmwareVersion(String host)

```
##### and so on

## Author

RockyPeng, Rocky.Peng@tymphany.com

## License

Contact Tymphany for more info

