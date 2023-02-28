# Aimstar Push Notification Sdk
## Requirements
MinSDK - 23  
CompileSDK - 31  
TargetSDK - 31

## SDKで提供する機能について
- aimstarのPush通知を受信するために必要な情報を登録する
- Push通知から起動した場合のログ送信

# SDKのInterfaceについて

## 用語

| 用語 | 説明 |
|---|---|
| API Key | AimstarMessagingを利用するために必要なAPIキーで、Aimstar側で事前にアプリ開発者に発行されます。 |
| Tenant ID | AimstarMessagingを利用するために必要なテナントIDで、Aimstar側で事前にアプリ開発者に発行されます。 |
| Aimstar ID | アプリ開発者がユーザーを識別するIDで、アプリ開発者が独自に発行、生成、または利用します。 |
| 端末の識別ID | アプリを端末ごと(インストールごと)に識別するIDです。アプリ起動後の初回のセットアップ時にUUIDを永続化して使います。 |
| FCMトークン | Firebaseがプッシュ通知を送信するために必要なIDで、Firebase側で発行・更新され、アプリ側で取得できます。 |


## Aimstar class
objectクラスです。
### init(Context, ApiKey, TenantId)
アプリ起動時に呼び出してください。
### registerToken(Context, AimstarId, FcmToken)
アプリ起動時など、ログインが完了したタイミングでFcmTokenを取得して呼び出してください。
ここで配信基盤のバックエンドにAimstarId、FcmTokenが連携され、配信対象になります

ログインが完了してないなどでAimstarIdが未確定の場合は呼び出す必要はありません。

### logout(Context)
ログアウトしたときや、匿名ユーザーによる使用など、アプリにおいてAimstarIdを特定できない状態となった場合に呼び出してください。
この処理を呼び出すことでPush通知の配信対象外になります。

### sendLog(Context, NotificationId, TargetGroupId)
AimstarのPush通知から起動した際にPayloadに含まれるNotificationId,TargetGroupIdを使用して呼び出してください。
ログをaimstarに集積することで、Push通知の効果検証を行うことができます

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
