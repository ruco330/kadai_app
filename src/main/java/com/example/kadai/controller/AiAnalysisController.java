package com.example.kadai.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kadai.dto.AnalyzeRequest;
import com.example.kadai.dto.AnalyzeResponse;
import com.example.kadai.service.AiAnalysisService;

/**
 * AI分析リクエストを処理するコントローラー
 */
@RestController
@RequestMapping("/ai-analysis")
@Validated
public class AiAnalysisController {

	private final AiAnalysisService aiAnalysisService;

	/**
	 * コンストラクタ
	 * @param aiAnalysisService AI解析サービス
	 */
	public AiAnalysisController(AiAnalysisService aiAnalysisService) {
		this.aiAnalysisService = aiAnalysisService;
	}

	/**
	 * AI画像解析を実行し、結果を返す
	 * 
	 * @param request JSON リクエストボディ（AnalyzeRequestDTO）
	 * @return AI解析結果のレスポンスDTO
	 */
	@PostMapping
	public ResponseEntity<AnalyzeResponse> analyzeImage(@Valid @RequestBody AnalyzeRequest request) {
		AnalyzeResponse response = aiAnalysisService.analyzeAndSave(request);
		return ResponseEntity.ok(response);
	}

	/** 400 Bad Request - クライアントの入力エラー */
	@ExceptionHandler({ IllegalArgumentException.class })
	public ResponseEntity<String> handleBadRequest(Exception ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	/** 500 Internal Server Error - サーバー内部エラー */
	@ExceptionHandler({ RuntimeException.class, IllegalStateException.class })
	public ResponseEntity<String> handleServerError(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
	}
}
