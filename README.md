# SCA

This is a cloud album storage App that does not require users to trust cloud services.

The project realizes cloud encrypted storage based on client encryption, cloud picture preview and cloud encrypted sharing. That is, the App realizes more secure cloud image storage and more secure cloud image sharing. Currently using Tencent Cloud's object storage service, but it can be extended to any object storage service. 

The following is a schematic diagram of the system framework of this project:

![System Architecture](system_architecture.png)

The project is developed using Java.

## Installation

1. Install requirements

   CloudSync installation requires [Java](https://www.java.com/en/download/) and [Android studio](https://developer.android.com/studio).

   Please install them before using this code.

2. Clone the code

   Use git to clone source code of this project to the local.

```
git clone https://github.com/HDSLiang/SCA.git
```

3. Install dependencies

   The Android studio configuration is as follows

  - java -- 18.0.1.1
  - Android Gradle Plugin Version -- 7.1.3
  - Gradle Version -- 7.2

## Usage

Open this project with **Android Studio**, click the `Sync Project Gradle Files` button to automatically download the dependent plugin.

Find the `Config.java` file under the `app/src/main/java/com/example/sca/Config.java` path, and modify the configuration parameters in it. For the specific application method of the parameters, see [Tencent Cloud Object Storage Preparations ](https://cloud.tencent.com/document/product/436/56390)

Run `MainActivity.java` to use this app

## Contributing

Please feel free to hack on SCA!



