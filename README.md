# Aimstar Push Notification Sdk
## Requirements
MinSDK - 23  
CompileSDK - 31  
TargetSDK - 31

## SDKで提供する機能について
- aimstarのPush通知を受信するために必要な情報を登録する
- Push通知から起動した場合のログ送信

# SDKのInterfaceについて
## Aimstar class
objectクラスです。
### init(Context, ApiKey)
アプリ起動時に呼び出してください。
### registerToken(Context, AimstarId, FcmToken)
アプリ起動時などでFcmTokenを取得して呼び出してください。
ログインが完了してないなどでAimstarIdが未確定の場合は呼び出す必要はありません。

### logout(Context)
ログアウトしたときなど、AimstarIdが消失した場合に呼び出してください。
### sendLog(Context, NotificationId, TargetGroupId)
AimstarのPush通知から起動した際にPayloadに含まれるNotificationId,TargetGroupIdを使用して呼び出してください。

# アプリ側で実装する必要がある機能
### FirebaseMessagingServiceを継承したService
アプリがフォアグラウンドのときに通知を作成するためのサービスを実装します。  
onNewTokenメソッドが呼ばれたときは新しいFcmTokenでregisterTokenを呼び出してください。
詳細はサンプル実装を参照。
### Aimstar objectを使用した実装
アプリ起動時、ログイン時、ログアウト時など適切なタイミングで適切なメソッドを呼びます。
### sendLog
MainActivityなどで通知から起動された際にintentに含まれるデータを取得して呼び出します。

## 導入ガイド
1. ライブラリの追加
   .aarファイルををapp/libs直下に配置してください。
   .aarファイルは本サンプルプロジェクトのapp/libsに入っております。
   ​
2. build.gradleの設定
   appレベルのbuild.gradleに以下を追加する。
```
dependencies {
    :
  implementation fileTree(dir: "libs", include: ["*.aar"])   // ★追加
    :
}
```
3. Gradleの同期
   Android Studioのメニュー File -> Sync Project with Gradle Files にてGradleを同期する。