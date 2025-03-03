# kadai_app
課題用リポジトリ

## 概要
本プロジェクトは、特定の画像ファイルのパスを指定すると、AI APIを通じて画像を分析し、その結果をデータベースに保存するAPIです。モックAPIを提供しており、実際のAI APIが存在しなくてもテストが可能です。

## 動作環境
- Java 21
- Spring Boot 3.2.3
- PostgreSQL

## 実行方法

APIに対して画像分析リクエストを送信するためのコマンドは以下の通りです。コマンドプロンプト（またはターミナル）で実行できます。

### リクエストを送信するコマンド
以下の`curl`コマンドを使用して、画像分析APIにリクエストを送信できます。

```bash
curl -X POST "http://localhost:8080/ai-analysis" ^
     -H "Content-Type: application/json" ^
     -d "{ \"imagePath\": \"/image/d03f1d36ca69348c51aa/c413eac329e1c0d03/test.jpg\" }"

```

### **1. AI画像分析API**
- **レスポンス例（成功時）**
```json
 {
    "success": true,
    "message": "success",
    "estimated_data": {
        "class": 3,
        "confidence": 0.8683
    }
 }
```
- **レスポンス例（失敗時）**
```json
 {
    "success": false,
    "message": "Error:E50012",
    "estimated_data": {}
 }
```

### **2. Mock AI API**

実際に動作するAPIは存在しないため、モックアップを作成しています。

- **エンドポイント:** `POST /mock-ai-api`
- **説明:** 50%の確率で成功 or 失敗のレスポンスを返します。



## 仕様確認事項

### 1. 画像パスのリクエスト形式
- 現状、`image_path`は`application/json`形式（`{ "image_path": "/path/to/image" }`）を想定しています。
- APIの仕様によっては`application/x-www-form-urlencoded`形式など、別の形式に変更される可能性もあります。
- 
### 2. APIレスポンスの仕様
- `message`の最大長が255文字を超過した場合は先頭255文字で切り落としています。エラーコードが設定されず、StackTrace等が設定された場合を想定しています。

### 3. エラーハンドリング
- APIを呼び出せない場合や、DBに登録するとエラーになる場合はエラーとしています。
- その他、APIの仕様に応じてエラーハンドリングの検討が必要です。

### 4. 環境設定
- 設定ファイルをローカル、本番で切り替える想定をしています。`application.properties`の`spring.profiles.active=local`でローカル設定を選択し、`application-local.properties`を使用します。
- 本番環境では`spring.profiles.active=prod`を指定し、`application-prod.properties`を使用します。

## ライセンス
本プロジェクトはMITライセンスのもとで提供されます。

