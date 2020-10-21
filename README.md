# SmartSDK-Android-Document
This is the Tymphany SmartSDK document for Android, based on the provided IoTControllerSDK source code from Qualcomm. The document for the SDK is based on the IoTSysManager class and the classes below the Onboarding package, and add the bleonboarding package to control wifi BLE onboarding, which are encapsulated in the App layer. 

## How to open document
The Tymphany Android SmartSDK document is generated in html. To open the document, please **download this repo** and find source file index.html under resource folder /docs, **double click file index.html** will open document in web browser.

## SDK Document Description
* The SDK document consists of two parts, wifi BLE onboarding and IoTSysManager. Both of these parts are under the manager package, and this part is in the App layer.

* Wifi BLE Onboarding doesn't involve Iotivity.The IoTSysManager class is related to the IoTDevice and IoTService in the IOTControllerSDK.IoTSysManager encapsulates many methods from IoTDevice. Only the methods in IoTSysManager need to be used during the development process. If there are any shortcomings or methods need to be added later, please check if the IoTDevice class is supported.

* There should be an instance of the IoTService in each application that needs to be initialized when the application is started. See the Reference App for details

* Because FW implement wifi onboarding via BLE. The project on app layer below manager package where create a bleonbarding package. This package has BleManager class to provide use.


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
  * @param wifiList  return wifi list from speaker scan
  */
  void didUpdateWifiList(List<WifiBean> wifiList);    
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
    * @param status The status value false is disconnect, value true is connected
    */
    void didUpdateWifiConnectStatus(boolean status);    
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

#### How to integrate the IoTController SDK
##### Because integration is still project dependent now. You can refer to how it's written in gradle on our demo app. Next I'll put it on gradle's remote dependencies, similar to okhttp 


## Author

RockyPeng, Rocky.Peng@tymphany.com


