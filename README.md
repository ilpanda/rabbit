### Android adb 命令的封装
通过 rabbit 命令快速查看当前显示的 Activity 名称、Fragment 名称。

---
### 基础原理
对 `adb shell dumpsys activity` 和 `grep` 命令做了简单封装，使得能够快速在命令行打印 Activity、Fragment 信息。

因此对部分 App， `rabbit adb -f` 查看 Fragment 命令不准确。

---
### 使用
安装：
```Shell
$ brew install ilpanda/repo/rabbit
```
打开一个新的终端，在命令行执行：
```Shell
$ rabbit --help
Usage: rabbit adb [OPTIONS]

  android adb command

Options:
  -c, --current     print current activity name
  -a, --all         print all activities name
  -f, --fragment    print specific package fragments
  -p, --print TEXT  print specific package activities
  -h, --help        Show this message and exit
```
手机连接 adb 后，查看当前手机 Activity 名称：
```Shell
$ rabbit adb -c 
```
查看当前手机所有栈中 Activity 名称：
```Shell
$ rabbit adb -all
```
查看当前手机栈中 Fragment：
```Shell
$ rabbit adb -f
```
查看当前手机栈中指定包名的 Activity，相当于 `rabbit adb -c | grep [packageName]`：
```Shell
$ rabbit adb -p [packageName]
```