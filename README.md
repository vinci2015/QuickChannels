# QuickChannels
a plugin which helps you generate greate amount of channel apks in few seconds.
基于gradle的快速打包插件，hook了app:assembleRelease任务，执行该任务后将自动打渠道包。
用法：
在项目根目录的build.gradle中，
```gradle
classpath 'com.github.vinci.quick-channels:plugin:1.0.2'
```
在app/gradle中，
```gradle
apply plugin: 'com.github.vinci.quick-channels'
channelExt{
    desPath = "E:\\androidWorkspace\\QuickChannels\\outputapks"
    apkPath = "E:\\androidWorkspace\\QuickChannels\\app\\build\\outputs\\apk\\app-release.apk"
    nameFormat = "QuickChannel-channel-{channel}.apk"dfdsfsdfsd
    channelFile = "singleChannel.properties"
}dsgfdsfkjhlsdkljf
```
其中闭包channelExt为配置参数，desPath 为渠道包输出目录，apkPath为签名包生成路径，这个一般是在app\build\outputs\apk\下，nameFormat为生成的渠道包的
命名规则，其中‘{channel}’将被替换为对应的渠道名，channelFile为渠道列表文件，一般放在根目录下。渠道列表文件内填写规则如下

- channel_baidushoujizhushou = baidushoujizhushou
- channel_xiaomiyingyongshangdian = xiaomiyingyongshangdian
- channel_official = official
- channel_jifengshichang = jifengshichang

签名信息请在signingConfigs中配置好。
最后通过代码获取渠道信息
```java
InitUtil.getChannel(context);
```
