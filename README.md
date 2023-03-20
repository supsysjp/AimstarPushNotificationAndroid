# aimstar android sdk
## Requirements
MinSDK - 23  
CompileSDK - 31  
TargetSDK - 31  

## SDKで提供する機能について
- aimstarのPush通知を受信するために必要な情報を登録する
- Push通知から起動した場合のログ送信

# SDKのInterfaceについて

## 用語

| 用語         | 説明                                                                                                       |
| ------------ | ---------------------------------------------------------------------------------------------------------- |
| API Key      | AimstarMessaging を利用するために必要な API キーで、Aimstar 側で事前にアプリ開発者に発行されます。         |
| Tenant ID    | AimstarMessaging を利用するために必要なテナント ID で、Aimstar 側で事前にアプリ開発者に発行されます。      |
| Customer ID  | アプリ開発者がユーザーを識別する ID で、アプリ開発者が独自に発行、生成、または利用します。                 |
| FCM トークン | Firebase がプッシュ通知を送信するために必要な ID で、Firebase 側で発行・更新され、アプリ側で取得できます。 |


## Aimstar class
objectクラスです。
### init(Context, ApiKey)  
アプリ起動時に呼び出してください。
### registerToken(Context, CustomerId, FcmToken)  
アプリ起動時など、ログインが完了したタイミングでFcmTokenを取得して呼び出してください。
ここで配信基盤のバックエンドにCustomerID、FcmTokenが連携され、配信対象になります。

### logout(Context)  
ログアウトしたときなど、CustomerIDがアプリ側で有効ではなくなった時に呼び出してください。

この処理を呼び出すことでPush通知の配信対象外になります

また、通信などの影響でログアウト処理が完了しなかった場合は、以下のようにしてエラーハンドリングすることが出来ます
```
  try {
      Aimstar.logout(this@MainActivity)
  }catch (e: AimstarException) {
      // retry等, error handle
      Log.e("Error", e.message, e)
  }
```




### sendLog(Context, NotificationId, TargetGroupId)  
aimstarのPush通知から起動した際にPayloadに含まれるNotificationId,TargetGroupIdを使用して呼び出してください。  
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
