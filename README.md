# Aimstar Push Notification SDK for Android

## Requirements

MinSDK - 26  
CompileSDK - 36  
TargetSDK - 36  
Java - 17  

## 前提条件

本SDKを使用するには、以下の設定が完了している必要があります：

- Firebase プロジェクトの設定が完了していること
- `google-services.json` がプロジェクトに配置されていること
- Firebase Messaging の依存関係が追加されていること

```groovy
dependencies {
    implementation platform('com.google.firebase:firebase-bom:33.7.0')
    implementation 'com.google.firebase:firebase-messaging'
}
```

## 導入方法

### 1. AARファイルのダウンロード

以下のいずれかの方法でAARファイルを取得してください：
- [Releases](../../releases) ページから最新版をダウンロード
- このリポジトリの `app/libs/AimstarPushSdkLib.aar` をダウンロード

### 2. プロジェクトへの追加

1. ダウンロードしたAARファイルをプロジェクトの `app/libs` フォルダに配置
2. `app/build.gradle` に以下を追加：

```groovy
dependencies {
    implementation fileTree(dir: "libs", includes: ["*.aar"])
}
```

## SDKで提供する機能について

- AimstarのPush通知を受信するために必要な情報を登録する
- Push通知から起動した場合のログ送信

## 用語

| 用語 | 説明 |
| --- | --- |
| API Key      | 本SDK を利用するために必要な API キーで、Aimstar 側で事前にアプリ開発者に発行されます。         |
| Tenant ID    | 本SDK を利用するために必要なテナント ID で、事前にアプリ開発者に提供されます。      |
| Customer ID  | アプリ開発者がユーザーを識別する ID で、アプリ開発者が独自に発行、生成、または利用します。                 |
| FCM トークン | Firebase がプッシュ通知を送信するために必要な ID で、Firebase 側で発行・更新され、アプリ側で取得できます。 |
| Target App ID | アプリへのプッシュ通知の配信接続先が複数ある場合に、ターゲットアプリを明示的に指定するために使用します。 |

## SDK Interface

### Aimstar class

objectクラスです。

#### init(Context, ApiKey, TenantId)

アプリ起動時に呼び出してください。

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Aimstar.init(
            context = this,
            apiKey = API_KEY,
            tenantId = TENANT_ID,
        )
    }
}
```

#### registerToken(Context, CustomerId, FcmToken, TargetAppId?)

アプリ起動時など、ログインが完了したタイミングでFcmTokenを取得して呼び出してください。
ここで配信基盤のバックエンドにCustomerID、FcmTokenが連携され、配信対象になります。

TargetAppIdは、アプリへのプッシュ通知の配信接続先が複数ある場合に、ターゲットアプリを明示的に指定したいときに使用します。

```kotlin
FirebaseMessaging.getInstance()
    .token
    .addOnCompleteListener {
        if (it.isSuccessful) {
            val token = it.result
            Aimstar.registerToken(
                context = context,
                customerId = CUSTOMER_ID,
                fcmToken = token,
                targetAppId = TARGET_APP_ID, // オプション
            )
        }
    }
```

#### logout(Context)

ログアウトしたときなど、CustomerIDがアプリ側で有効ではなくなった時に呼び出してください。

この処理を呼び出すことでPush通知の配信対象外になります

また、通信などの影響でログアウト処理が完了しなかった場合は、以下のようにしてエラーハンドリングすることが出来ます

```kotlin
try {
    Aimstar.logout(this@MainActivity)
} catch (e: AimstarException) {
    // retry等, error handle
    Log.e("Error", e.message, e)
}
```

logoutリクエストが失敗したままですと、想定していない通知が届いてしまう場合があります
そういったケースを防ぐために、もしリクエストが失敗した際にリクエストをリトライしたい場合等に使用することができます

また、アプリ側でもログアウト時にfcm tokenを以下のような処理で失効させておくことができます
これにより、もしlogout時にリクエストが失敗した場合においてもPush通知の配信対象外にすることができます

```kotlin
FirebaseMessaging.getInstance().deleteToken()
```

#### sendLog(Context, NotificationId, TargetGroupId)

aimstarのPush通知から起動した際にPayloadに含まれるNotificationId,TargetGroupIdを使用して呼び出してください。  
ログをaimstarに集積することで、Push通知の効果検証を行うことができます

```kotlin
private fun handleIntent(intent: Intent) {
    val notificationId = intent.extras?.getString("notification_id")
    val targetGroupId = intent.extras?.getString("target_group_id")
    if (notificationId != null && targetGroupId != null) {
        Aimstar.sendLog(
            context = this,
            notificationId = notificationId,
            targetGroupId = targetGroupId,
        )
    }
}
```

## アプリ側で実装する必要がある機能

### FirebaseMessagingServiceを継承したService

アプリがフォアグラウンドのときに通知を作成するためのサービスを実装します。  
onNewTokenメソッドが呼ばれたときは新しいFcmTokenでregisterTokenを呼び出してください。
詳細はサンプル実装を参照。  

### Aimstar objectを使用した実装

アプリ起動時、ログイン時、ログアウト時など適切なタイミングで適切なメソッドを呼びます。  

### sendLog

MainActivityなどで通知から起動された際にintentに含まれるデータを取得して呼び出します。
