# Bluetooth control  
android app project for iot  
you can use it for HC-05 and etc... with arduino or your own board  
with send and receive data  

IDE : android studio

## use:


for send data (in mainActivity) :


```java
send("your data");
```

for receive data (in mainActivity) :


```java
@Override
public void onSerialRead(byte[] data) {
    runOnUiThread(() -> {
        //your reveived data
        String Received = data.toString());

    });
}
```

### Application screenshot
  ![alt tag](https://raw.githubusercontent.com/mahdi2001h/BluetoothControl/master/img/app_bluetooth.png)
