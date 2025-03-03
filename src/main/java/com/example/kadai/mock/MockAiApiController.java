package com.example.kadai.mock;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * APIモック
 */
@RestController
@RequestMapping("/mock-ai-api")
public class MockAiApiController {

	/**
	 * expectedResultによって成功・失敗を切り替えて返却します。
	 * 
	 * @param requestBody JSONリクエスト (image_path を含む)
	 * @return response
	 */
	@PostMapping
	public Map<String, Object> analyzeImage(@RequestBody Map<String, Object> requestBody) {

		Map<String, Object> response = new HashMap<>();
		Map<String, Object> estimatedData = new HashMap<>();

		// 50%の確率で成功 or 失敗のモックレスポンスを返す
		boolean isSuccess = Math.random() > 0.5;

		// 成功の場合.
		if (isSuccess) {
			response.put("success", true);
			response.put("message", "success");

			estimatedData.put("class", (int) (Math.random() * 10)); // 0-9 のランダムクラス
			estimatedData.put("confidence", Math.round(Math.random() * 10000) / 10000.0); // 0.0000 - 1.0000

			response.put("estimated_data", estimatedData);

			// 失敗の場合
		} else {
			response.put("success", false);
			response.put("message", "Error:E50012");
			response.put("estimated_data", new HashMap<>()); // 空のデータを設定
		}

		return response;
	}

}
