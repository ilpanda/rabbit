### Android adb 命令的封装

通过 rabbit 命令快速查看当前显示的 Activity 名称、Fragment 名称。

---

### 开发背景

1. 在 Android 开发过程当中，为了快速定位代码，我们经常需要找到当前页面显示的 Activity 名称与 Fragment 名称。本项目 adb 进行封装，帮助快速定位当前页面。
2. [adb idea](https://github.com/pbreault/adb-idea) 插件为我们在开发阶段提供了非常多好用的功能，但是该插件有一个缺点，就是无法指定包名 App。本项目对常用的几个 adb idea 命令进行了封装。

---

### 查看 Activity 、Fragment 名称原理

对 `adb shell dumpsys activity` 和 `grep` 命令做了简单封装，使得能够快速在命令行打印 Activity、Fragment 信息。

对部分 App， 使用 `rabbit adb -f` 命令获取的 Fragment 命令不准确。

---

### 安装及使用

Mac 系统使用 brew 安装 rabbit：

```shell
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
