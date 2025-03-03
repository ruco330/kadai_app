package com.example.kadai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot アプリケーションのエントリーポイント（起動クラス）
 * 
 * - main メソッドを実行すると、Spring Boot アプリが起動する
 */
@SpringBootApplication // Spring Boot の自動設定を有効化（ComponentScan, Configuration, EnableAutoConfiguration の機能を含む）
public class KadaiApplication {

    /**
     * アプリケーションのエントリーポイント（ここから Spring Boot が起動）
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        SpringApplication.run(KadaiApplication.class, args);
    }
}
