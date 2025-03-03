# kadai_app
課題用リポジトリ

## 概要
本プロジェクトは、特定の画像ファイルのパスを指定すると、AI APIを通じて画像を分析し、その結果をデータベースに保存するAPIです。モックAPIを提供しており、実際のAI APIが存在しなくてもテストが可能です。

## 動作環境
- Java 17
- Spring Boot 3.2.3
- PostgreSQL

### 環境設定
#### **application.propertiesの設定**
```properties
# AI APIのエンドポイント
ai.api.url=http://localhost:8080/mock-ai-api

# データベース接続情報
spring.datasource.url=jdbc:mysql://localhost:3306/ai_analysis
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

## API仕様

### **1. AI画像分析API**
- **エンドポイント:** `POST /ai-analysis`
- **リクエストボディ:**
```json
{
  "imagePath": "/image/d03f1d36ca69348c51aa/c413eac329e1c0d03/test.jpg"
}
```
- **レスポンス例（成功時）**
```json
{
  "imagePath": "/image/d03f1d36ca69348c51aa/c413eac329e1c0d03/test.jpg",
  "success": true,
  "message": "success",
  "classId": 3,
  "confidence": 0.8683
}
```
- **レスポンス例（失敗時）**
```json
{
  "imagePath": "/image/d03f1d36ca69348c51aa/c413eac329e1c0d03/test.jpg",
  "success": false,
  "message": "Error:E50012",
  "classId": null,
  "confidence": null
}
```

### **2. Mock AI API**
- **エンドポイント:** `POST /mock-ai-api`
- **説明:** 50%の確率で成功 or 失敗のレスポンスを返します。

```

## ライセンス
本プロジェクトはMITライセンスのもとで提供されます。

