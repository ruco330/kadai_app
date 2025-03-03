package com.example.kadai.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * AI解析ログのエンティティクラス（ai_analysis_log テーブルと対応）
 * 
 * このクラスはデータベースの `ai_analysis_log` テーブルとマッピングされる。
 * 画像解析の結果を保存するためのデータを管理する。
 */
@Data
@Entity // JPA のエンティティクラスとして定義
@Table(name = "ai_analysis_log") // 対応するDBのテーブル名を指定
public class AiAnalysisLog {

    /** 主キー（自動採番） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 解析対象の画像パス */
    @Column(nullable = false, length = 255) // NULL 禁止、最大255文字
    private String imagePath;

    /** 解析成功フラグ（true: 成功, false: 失敗） */
    @Column(nullable = false)
    private Boolean success;

    /** メッセージ（成功/エラー情報） */
    private String message; // 255文字制限（トリミングはサービス層で実施）

    /** 解析結果のクラス（分類結果） */
    @Column(name = "class") // DBのclassカラムとマッピング（予約語回避のため変更）
    private Integer classId;

    /** 解析結果の信頼度（小数点第4位まで） */
    @Column(precision = 5, scale = 4) // DECIMAL(5,4) 
    private BigDecimal confidence;

    /** リクエスト送信時間 */
    private LocalDateTime requestTimestamp;

    /** レスポンス受信時間 */
    private LocalDateTime responseTimestamp;
}
