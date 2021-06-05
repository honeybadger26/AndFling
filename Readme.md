# AndFling
Android app to send data over LAN and remote control your PC.

## Instructions
### Client
- Install [Android Studio](https://developer.android.com/studio)
- Open `client` folder in Android Studio
- Connect Android device with USB and run using Android Studio

### Server
- Install `windows-build-tools` for Robotjs using: `npm install --global --production windows-build-tools`
- Change directory: `cd server`
- Install dependencies: `npm install`
- Run: `npm start`

## Notes
- [Key names for RobotJS](https://github.com/octalmage/robotjs/blob/master/src/robotjs.cc) (around line 289)

### How to debug remotely
Requires [SDK Platform-Tools](https://developer.android.com/studio/releases/platform-tools). If Android Studio is installed the Platform Tools can be found at: `%AppData%\Local\Android\Sdk\platform-tools`.
Credits: [Run/install/debug Android applications over Wi-Fi?](https://stackoverflow.com/questions/4893953/run-install-debug-android-applications-over-wi-fi)
1. Connect device using USB
2. `adb tcpip 5555`
3. `adb shell netcfg` or `adb shell ifconfig` to get IP address of device
4. Disconnect device from USB
5. `adb connect <DEVICE_IP_ADDRESS>:5555`

## Todo
### Chat
- [x] Send/recieve text
- [x] Clickable links
- [x] Quick send clipboard contents
- [ ] Send photos/gifs
- [ ] Send files
- [ ] ? Different colored messages
### Remote
- [x] Play/pause
- [x] Left/right, next/previous
- [x] Volume up/down/mute
- [x] Check server status