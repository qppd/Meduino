
<h1 align="center">💊 Meduino</h1>
<p align="center">
  <b>Smart IoT Pill Dispenser App</b><br/>
  Arduino + Firebase + Android for automated medication scheduling.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/platform-Android%20%7C%20Arduino-blue" />
  <img src="https://img.shields.io/badge/firebase-connected-orange" />
  <img src="https://img.shields.io/badge/license-MIT-green" />
</p>

---

## 📱 About Meduino

**Meduino** is an open-source, IoT-powered smart pill dispenser system designed to automate medication schedules for up to **3 separate compartments**. It combines:

- Android mobile app interface  
- Firebase Realtime Database  
- Arduino-controlled dispenser (Arduino R4)

This system helps ensure medication adherence through scheduled dispensing, alerts, and a simple UI for caregivers or patients.

---

## ✨ Features

<ul>
  <li>Add, edit, and delete pill schedules (date + time)</li>
  <li>Control up to <b>3 dispensers</b> (A, B, C)</li>
  <li>Realtime schedule sync with Firebase</li>
  <li>Easy-to-use Android app UI</li>
</ul>

---

## 🚀 Getting Started

### 🔧 Requirements

- Android Studio (Flamingo or later)
- Arduino IDE
- ESP32 or Arduino board + RTC + Servo motor
- Firebase project (Realtime Database)

### 📲 Android Setup

1. Clone the repo:
   ```bash
   git clone https://github.com/qppd/Meduino.git
   ```

2. Open `android-app` in **Android Studio**

3. Add your Firebase `google-services.json` file inside:
   ```
   android-app/app/google-services.json
   ```

4. Build and run the app on your device.


## 🔥 Firebase Structure (Sample)

```json
{
  "dispensers": {
    "A": {
      "2025-07-25T08:00": true
    },
    "B": {
      "2025-07-25T12:00": true
    },
    "C": {}
  }
}
```


## 🛠️ Built With

- 💚 **Android (Java)**
- 🔥 **Firebase Realtime DB**

---

## 🤝 Contributing

We welcome contributions! To contribute:

1. Fork the repo  
2. Create a feature branch: `git checkout -b feature/YourFeature`  
3. Commit your changes  
4. Push to your branch  
5. Create a Pull Request

---

## 📄 License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

---

## 🙋‍♂️ Author

Made with ❤️ by [QPPD](https://github.com/qppd)  
Star ⭐ this repo if you find it useful!
