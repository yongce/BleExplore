# BleDemo

这个demo用于研究BLE的功能特性。

## 调试日志

调试设备：

- Client：Mi 5S，MIUI 10.1，Android 8.0
- Server：Nexus 5X，Android 8.1

### 扫描BLE设备

BLE协议为了保护用户隐私，所有BLE设备暴露出去的device address都是一个随机地址，会定期变化（一般10分钟左右）。
代码是无法获取BLE设备的真实蓝牙地址，也无法获取自己的随机device address。

在通过 android.bluetooth.le.BluetoothLeAdvertiser#startAdvertising() 发送广播时，会立即更新当前设备的device address（即每次调用都不一样）。
这点，可以通过 android.bluetooth.le.BluetoothLeScanner() 的扫描结果观察到。

> 注1：已经与当前设备配对的设备，将不会出现在BLE扫描结果中，需要自己记录下已配对的设备信息（device name & device address）。
> 也可通过 android.bluetooth.BluetoothAdapter#getBondedDevices()来获得已配对设备列表，通过设备名特征来识别特定设备。

> 注2：在某些情况下，即使已经与目标设备配对，也可以在BLE扫描中发现目标设备。

### 配对BLE设备

当通过BLE扫描到设备后，可通过 android.bluetooth.BluetoothDevice#createBond() 来与目标设备配对。
当配对完成后，在发起的设备上会出现两个已配对的目标设备：其中一个是配对前的device address，另一个是新的device address。而在目标设备上，仅一个已配对设备。
这两个目标设备的device address不再变化。不确定是否所有设备上都会有此现象。

配对完成后，都可以通过 android.bluetooth.BluetoothDevice#connectGatt() 与这两个device address建立BLE连接。但有如下差异：

- 通过旧device address进行连接和断开连接（即使只执行 BluetoothGatt#disconnect()，不执行 BluetoothGatt#close()），
会分别在目标设备的 BluetoothGattServerCallback 中收到BLE连接成功和BLE连接断开的事件。
- 通过新device address进行连接和断开连接（即使执行 BluetoothGatt#close()），在目标设备只会收到一次连接成功的事件（即不会发生真正的断连）。
此外，每次的连接速度很快（应该是没有真正断连的原因）。

在某个时刻，无法再通过旧device address进行连接（GATT_ERROR，0x85），原因不明（但始终可以通过新device address进行连接）:

- 关闭/重新开启当前设备的蓝牙后，依然无法连接成功（同样错误）。
- 目标设备关闭/重新开启GATT server/BLE advertiser，依然无法连接成功（同样错误）。
- 取消配对，重新通过BLE扫描去查找设备，并进行BLE连接，会连接成功（此时device address已经改变），但随即会自动触发系统的配对对话框。
取消此对话框，BLE连接会断开（GATT_STATUS_UNKNOWN-[0x16]）。再次连接，会成功。
- 怀疑此问题跟BLE device address的变化有关。

### 连接BLE设备

需要建立BLE连接时，每次都使用 BluetoothDevice#connectGatt() 进行连接，不要使用 BluetoothGatt#connect() 来恢复连接，
否则很可能出现没有 BluetoothGattCallback 回调的情况）。

由于BLE设备的device address会经常变化，可能会导致一段时间后，无法再恢复连接。需要重新进行BLE扫描，用新的device address来连接。

## 一些错误

### onClientRegistered - status=133

如果在日志中发现 “BluetoothGatt/D onClientRegistered() - status=133 clientIf=0”，表明有 BluetoothGatt 连接没有被关闭，
导致达到了可建立GATT连接的上限（我测试的Android 8.0系统为clientIf=32）。
此时，需要检查代码，确保所有 BluetoothGatt 对象在不需要时调用了其 #close() 方法。

### onClientConnectionState - status=133

如果设备未配对，进行BLE连接时总是出现此错误，原因可能是目标设备的device address改变了，需要重新进行BLE扫描，然后再尝试连接。
