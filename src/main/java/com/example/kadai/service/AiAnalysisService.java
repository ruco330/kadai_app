package com.example.kadai.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.kadai.dto.AnalyzeRequest;
import com.example.kadai.dto.AnalyzeResponse;
import com.example.kadai.entity.AiAnalysisLog;
import com.example.kadai.repository.AiAnalysisLogRepository;

/**
 * APIを呼び出してDBに登録をおこなう
 */
@Service
public class AiAnalysisService {

	private static final Logger logger = LoggerFactory.getLogger(AiAnalysisService.class);

	private final AiAnalysisLogRepository repository;
	private final RestTemplate restTemplate;

	// プロパティからAPIのURLを取得
	@Value("${ai.api.url}")
	private String apiUrl;

	public AiAnalysisService(AiAnalysisLogRepository repository) {
		this.repository = repository;
		this.restTemplate = new RestTemplate();
	}

	/**
	 * AnalyzeRequestをAPIに送信し、レスポンスを解析してDBに保存し、レスポンスDTOに変換する
	 * @param request リクエスト情報
	 * @return APIレスポンスを格納した AnalyzeResponse
	 */
	public AnalyzeResponse analyzeAndSave(AnalyzeRequest request) {

		// リクエストのimagePathが未設定の場合
		if (request.getImagePath() == null || request.getImagePath().trim().isEmpty()) {
			logger.error("エラー: imagePath が null または空");
			throw new IllegalArgumentException("imagePath が null または空");
		}

		AiAnalysisLog aiAnalysisLog = new AiAnalysisLog();
		// API 呼び出し
		Map<String, Object> response = callAiApi(request.getImagePath());

		// レスポンスのバリデーション（不正なレスポンスを事前に排除）
		this.validateResponse(response, request.getImagePath());

		// レスポンス解析
		aiAnalysisLog.setSuccess(parseSuccess(response, request.getImagePath())); // success（入力チェックあり）
		aiAnalysisLog.setMessage(parseMessage(response)); // メッセージ（入力チェックあり）

		aiAnalysisLog.setImagePath(request.getImagePath()); // 解析対象の画像パス
		aiAnalysisLog.setRequestTimestamp(LocalDateTime.now()); // リクエスト送信時間
		aiAnalysisLog.setResponseTimestamp(LocalDateTime.now()); // レスポンス送信時間

		// 成功時のみ estimated_data を解析
		if (aiAnalysisLog.getSuccess()) {
			parseEstimatedData(response, aiAnalysisLog, request.getImagePath());
		}

		AiAnalysisLog savedLog;

		try {
			// DB に保存
			savedLog = repository.save(aiAnalysisLog);
			logger.debug("=== データ保存成功 === ID: {}", savedLog.getId());

		} catch (DataAccessException e) {
			logger.error("=== データ保存に失敗 ===", e);
			throw new RuntimeException("データ保存中にエラーが発生しました", e);
		}

		// AnalyzeResponse に変換して返す
		return convertToResponse(savedLog);
	}

	/**
	 * AI API に画像パスを送信し、レスポンスを取得する
	 * @param imagePath 画像のパス
	 * @return API のレスポンス（Map 形式）
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, Object> callAiApi(String imagePath) {
		try {
			logger.info("=== callAiApi() - 送信するリクエスト (JSON) === imagePath: {}", imagePath);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			Map<String, String> requestBody = Map.of("image_path", imagePath);
			HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
			ResponseEntity<Map> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity,
					Map.class);

			logger.info("=== callAiApi() - 受信したレスポンス === {}", responseEntity.getBody());
			return responseEntity.getBody();
		} catch (RestClientException e) {
			logger.error("[APIエラー] Mock API 呼び出しに失敗しました。URL=" + apiUrl, e);
			throw new RuntimeException("Mock API 呼び出しに失敗しました", e);
		}
	}

	/**
	 * API レスポンスのバリデーションを実施
	 * @param response API のレスポンス
	 * @param imagePath 画像のパス
	 */
	private void validateResponse(Map<String, Object> response, String imagePath) {
		if (response == null) {
			throw new IllegalStateException("[APIエラー] APIレスポンスは返却されていません。imagePath=" + imagePath);
		}
		if (!response.containsKey("success")) {
			throw new IllegalStateException("[APIエラー] APIレスポンスに 'success' フィールドがありません。imagePath=" + imagePath);
		}
		if (!response.containsKey("message")) {
			throw new IllegalStateException("[APIエラー] APIレスポンスに 'message' フィールドがありません。imagePath=" + imagePath);
		}
		if (!response.containsKey("estimated_data")) {
			throw new IllegalStateException("[APIエラー] APIレスポンスに 'estimated_data' フィールドがありません。imagePath=" + imagePath);
		}
	}

	/**
	 * API レスポンスから success を取得
	 * @param response API のレスポンス
	 * @param imagePath 画像のパス
	 * @return success（リクエスト成功なら true, 失敗なら false）
	 */
	private boolean parseSuccess(Map<String, Object> response, String imagePath) {
		Object successObj = response.get("success");
		if (successObj instanceof Boolean) {
			return (Boolean) successObj;
		}
		throw new IllegalStateException(
				"[APIエラー] APIレスポンスの 'success' の値が不正です。 imagePath=" + imagePath + ",success=" + successObj);
	}

	/**
	 * API レスポンスから message を取得し、255 文字以内に制限する
	 * @param response API のレスポンス
	 * @return メッセージ（255 文字以内）
	 */
	private String parseMessage(Map<String, Object> response) {
		Object messageObj = response.get("message");
		if (messageObj == null) {
			return "";
		}
		String message = messageObj.toString();

		if (message.length() > 255) {
			return message.substring(0, 255);
		}

		return message;
	}

	/**
	 * API レスポンスから estimated_data を解析し、class と confidence の値を設定する
	 * @param response API のレスポンス
	 * @param imagePath 画像のパス
	 * @param aiAnalysisLog 保存するエンティティ
	 * 
	 */
	private void parseEstimatedData(Map<String, Object> response, AiAnalysisLog aiAnalysisLog, String imagePath) {
		Object estimatedDataObj = response.get("estimated_data");

		if (!(estimatedDataObj instanceof Map)) {
			throw new IllegalStateException("[APIエラー] APIレスポンスの 'estimated_data' が不正です。imagePath=" + imagePath
					+ ",estimated_data=" + estimatedDataObj);
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> estimatedData = (Map<String, Object>) estimatedDataObj;

		Object classObj = estimatedData.get("class");
		if (classObj instanceof Integer) {
			aiAnalysisLog.setClassId((Integer) classObj);
		} else {
			throw new IllegalStateException(
					"[APIエラー] APIレスポンスの 'class' が数値ではありません。imagePath=" + imagePath + ",class=" + classObj);
		}

		Object confidenceObj = estimatedData.get("confidence");
		try {
			aiAnalysisLog.setConfidence(new BigDecimal(confidenceObj.toString()));
		} catch (NumberFormatException e) {
			throw new IllegalStateException(
					"[APIエラー] APIレスポンスの 'confidence' が数値ではありません。imagePath=" + imagePath + ",confidence="
							+ confidenceObj);
		}
	}

	/**
	 * AiAnalysisLog を AnalyzeResponse に変換する
	 * @param aiAnalysisLog 保存されたログ
	 * @return APIレスポンスDTO
	 */
	private AnalyzeResponse convertToResponse(AiAnalysisLog aiAnalysisLog) {
		return new AnalyzeResponse(
				aiAnalysisLog.getImagePath(),
				aiAnalysisLog.getSuccess(),
				aiAnalysisLog.getMessage(),
				aiAnalysisLog.getClassId(),
				aiAnalysisLog.getConfidence());
	}
}
