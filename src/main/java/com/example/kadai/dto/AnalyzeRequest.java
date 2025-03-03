package com.example.kadai.dto;

/**
 * AI分析リクエスト用のDTOクラス
 */
public class AnalyzeRequest {

    private String imagePath;

    // Getters and Setters
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
