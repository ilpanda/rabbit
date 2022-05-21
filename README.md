### Android adb 命令的封装

通过 rabbit 命令快速查看当前显示的 Activity 名称、Fragment 名称。

rabbit 命令对 adb 命令进行了一层简单封装，因此在使用 rabbit 命令前，首先保证中断已经成功配置 adb。

rabbit 命令支持将录制的 mp4 文件保存到本地，但使用该功能需要首先安装 scrcpy。

---

### 开发背景

1. 在 Android 开发过程当中，为了快速定位代码，我们经常需要找到当前页面显示的 Activity 名称与 Fragment 名称。本项目 adb 进行封装，帮助快速定位当前页面。
2. [adb idea](https://github.com/pbreault/adb-idea) 插件为我们在开发阶段提供了非常多好用的功能，但是该插件有一个缺点，就是无法指定包名 App。本项目对常用的几个 adb idea
   命令进行了封装。

---

### 查看 Activity 、Fragment 名称原理

对 `adb shell dumpsys activity` 和 `grep` 命令做了简单封装，使得能够快速在命令行打印 Activity、Fragment 信息。

对部分 App， 使用 `rabbit adb -f` 命令获取的 Fragment 命令不准确。

---

### 安装及基础使用

Mac 系统使用 brew 安装 rabbit：

```shell
$ brew update
$ brew install ilpanda/repo/rabbit
```

打开一个新的终端，在命令行执行 `rabbit --help` 查看帮助信息：

```shell
$ rabbit --help
Usage: rabbit adb [OPTIONS]

  android adb command

Print Options:
  -c, --current            print current activity name
  -a, --all                print all activities name
  -f, --fragment           print specific package fragments
  -p, --print packageName  print specific package activities

App Options:
  --clear packageName        clear app data , use adb shell pm clear
                             [packageName]
  --kill packageName         force stop app , use adb shell am force-stop
                             [packageName]
  --grant packageName        grant app all permission , use adb shell pm grant
                             [packageName] [permission]
  --revoke packageName       revoke app all permission , use adb shell pm
                             revoke [packageName] [permission]
  --start packageName        start app , use adb shell monkey -p [packageName]
                             -c android.intent.category.LAUNCHER 1
  -r, --restart packageName  restart app , use --kill and --start command
```

手机连接 adb 后，查看当前手机 Activity 名称：

```shell
$ rabbit adb -c 
```

查看当前手机所有栈中 Activity 名称：

```shell
$ rabbit adb -all
```

查看当前手机栈中 Fragment：

```shell
$ rabbit adb -f
```

查看当前手机栈中指定包名的 Activity，相当于 `rabbit adb -c | grep [packageName]`：

```shell
$ rabbit adb -p [packageName]
```

清除 App 数据：

```shell
$ rabbit adb --clear [packageName]
```

授权 App 所有申请的权限：

```shell
$ rabbit adb --grant [packageName]
```

撤销 App 所有申请的权限：

```shell
$ rabbit adb --revoke [packageName]
```

强制杀死 App:

```shell
$ rabbit adb --kill [packageName]
```

启动 App:

```shell
$ rabbit adb --start [packageName]
```

重新启动 App:

```shell
$ rabbit adb --restart [packageName]
```

重新启动 App 的命令等同于：

```shell
$ rabbit adb --kill [packageName]
$ rabbit adb --start [packageName]
```

---

### 查看手机信息

查看手机基础信息：

```shell
$ rabbit adb  -i device
model: Redmi K30 Pro Zoom Edition  // 手机型号
version: Android 10                // 手机 Android 版本
display: init=1080x2400 440dpi cur=1080x2400 app=1080x2270  // 手机分辨率
density: 440dpi                    // 手机屏幕密度
density scale: 2.75                // 手机密度 440.0f/160 计算得到
android_id: 6c44a46e94c4954b       // Android Id
```

查看手机 CPU 信息，等同于 `adb shell cat /proc/cpuinfo`：

```shell
 $ rabbit adb  -i cpu
```

查看手机内存信息，等同于 `adb shell cat /proc/meminfo`：

```shell
 $ rabbit adb  -i memory
```

查看电池信息，等同于 `adb shell dumpsys battery`：

```shell
 $ rabbit adb  -i battery
```

---

### 跳转到系统页面

跳转到语言列表页：

```shell
$ rabbit adb  -ac locale
```

跳转到开发者选项页（需要已经开启开发者选项）：

```shell
$ rabbit adb  -ac developer
```

跳转到应用列表页：

```shell
$ rabbit adb  -ac application
```

跳转到通知管理列表页：

```shell
$ rabbit adb  -ac notification
```

跳转到蓝牙管理页：

```shell
$ rabbit adb  -ac bluetooth
```

跳转到输入法管理页：

```shell
$ rabbit adb  -ac input
```

跳转到屏幕显示页：

```shell
$ rabbit adb  -ac display
```

---
### 截屏与屏幕录制
 
保存手机截图到当前文件夹：
```shell
$ rabbit adb -s png
```

录制手机视频到当前文件夹，内部使用的是 scrcpy ：
```shell
$ rabbit adb  -s  mp4
```


---
### 常见问题

当遇到下列报错：

```text
Exception occurred while executing 'clear':
java.lang.SecurityException: PID 8391 does not have permission android.permission.CLEAR_APP_USER_DATA to clear data of package xxxx
	at com.android.server.am.ActivityManagerService.clearApplicationUserData(ActivityManagerService.java:3837)
```

或者下列报错：

```text
Exception occurred while executing 'grant':
java.lang.SecurityException: grantRuntimePermission: Neither user 2000 nor current process has android.permission.GRANT_RUNTIME_PERMISSIONS.
	at android.app.ContextImpl.enforce(ContextImpl.java:2096)
	......
```

需要打开手机开发者选项中的禁止权限监控按钮（默认是关闭的）。如果遇到下列报错：

```text
Exception occurred while executing 'clear':
java.lang.SecurityException: adb clearing user data is forbidden.
	at com.android.server.pm.OplusClearDataProtectManager.interceptClearUserDataIfNeeded(OplusClearDataProtectManager.java:88)
	at com.android.server.pm.OplusBasePackageManagerService$OplusPackageManagerInternalImpl.interceptClearUserDataIfNeeded(OplusBasePackageManagerService.java:531)
	at com.android.server.am.ActivityManagerService.clearApplicationUserData(ActivityManagerService.java:4708)
	......
```

部分手机预装 App 不支持 adb clear。