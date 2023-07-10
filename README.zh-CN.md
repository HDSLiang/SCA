# SCA

这是一个无需用户信任云服务的云端相册存储App。

该项目实现了基于客户端加密的云端加密存储，云端图片预览以及云端加密共享。即App实现了更安全的云端图片存储和更安全的云端图片共享。目前使用腾讯云的对象存储服务，但它可拓展至任意的对象存储服务。该项目使用 Java 进行开发。

以下是本项目的系统框架示意图：

![System Architecture](system_architecture.png)

## 安装

在安装之前，首先要确保正确安装了 Java 和 Android Studio。具体配置如下：

- java -- 18.0.1.1
- Android Gradle Plugin Version -- 7.1.3
- Gradle Version -- 7.2

接着将项目下载到本地，下载方式为：

```
git clone https://github.com/HDSLiang/SCA.git
```

## 使用

用Android Studio打开此项目，点击 `Sync Project Gradle Files `按钮,自动下载依赖插件。

找到 `app/src/main/java/com/example/sca/Config.java` 路径下的`Config.java` 文件，修改其中的配置参数，其中参数的具体申请方法见[腾讯云对象存储 准备工作 ](https://cloud.tencent.com/document/product/436/56390)

运行`MainActivity.java`即可使用本app



