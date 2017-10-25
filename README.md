# QuickChannels
a plugin which helps you generate greate amount of channel apks in few seconds.
基于gradle的快速打包插件，hook了app:assembleRelease任务，执行该任务后将自动打渠道包。
##打包方案：
1. app:asssembleRelease 生成一个签名的正式包。
2. 解压apk包，在META-INF目录下新建一个文件，写入渠道信息
3. 重新压缩并用渠道命名。
##用法：
### 1. 引入插件
```gradle
classpath 'com.github.vinci.quick-channels:plugin:1.0.2'
apply plugin: 'com.github.vinci.quick-channels'
```
### 2. 配置参数
```gradle
channelExt{
    desPath = "E:\\androidWorkspace\\QuickChannels\\outputapks"
    apkPath = "E:\\androidWorkspace\\QuickChannels\\app\\build\\outputs\\apk\\app-release.apk"
    nameFormat = "QuickChannel-channel-{channel}.apk"dfdsfsdfsd
    channelFile = "singleChannel.properties"
}
```
以上为示例，请自行对照替换相应的值。
其中闭包channelExt为配置参数，
- desPath 为渠道包输出目录，
- apkPath为签名包生成路径，这个一般是在app\build\outputs\apk\下，
- nameFormat为生成的渠道包的命名规则，其中‘{channel}’将被替换为对应的渠道名，
- channelFile为渠道列表文件，一般放在根目录下。渠道列表文件内填写规则如下
    >channel_baidushoujizhushou = baidushoujizhushou
    
    >channel_xiaomiyingyongshangdian = xiaomiyingyongshangdian
    
    >channel_official = official
    
    >channel_jifengshichang = jifengshichang
    
### 3. 配置签名信息
```gralde
    release{
            storeFile file(keyProperties['store'])
            storePassword keyProperties['storePass']
            keyAlias keyProperties['alias']
            keyPassword keyProperties['pass']
        }
```
签名信息请在signingConfigs中配置好。

### 4. 获取渠道名
最后通过代码获取渠道信息
```java
 public static String getChannel(Context context) {
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith("META-INF/pwchannel")) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String[] split = ret.split("-");
        if (split != null && split.length == 3) {
            String channel = split[2];
            return channel;

        } else {
            return "";
        }
    }
```
