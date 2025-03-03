package com.example.kadai.dto;

import java.math.BigDecimal;

/**
 * AI解析結果のレスポンスDTO
 * クライアントに返すデータを定義する
 */
public class AnalyzeResponse {

	private String imagePath;
	private Boolean success;
	private String message;
	private Integer classId;
	private BigDecimal confidence;

	// コンストラクタ
	public AnalyzeResponse(String imagePath, Boolean success, String message, Integer classId,
			BigDecimal confidence) {
		this.imagePath = imagePath;
		this.success = success;
		this.message = message;
		this.classId = classId;
		this.confidence = confidence;
	}

	// ゲッター
	public String getImagePath() {
		return imagePath;
	}

	public Boolean getSuccess() {
		return success;
	}

	public String getMessage() {
		return message;
	}

	public Integer getClassId() {
		return classId;
	}

	public BigDecimal getConfidence() {
		return confidence;
	}
}
