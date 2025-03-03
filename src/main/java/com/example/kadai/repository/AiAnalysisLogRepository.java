package com.example.kadai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.kadai.entity.AiAnalysisLog;

/**
 * AI解析ログ（ai_analysis_log テーブル）を操作するリポジトリ
 * 
 * JpaRepository を継承することで、基本的なデータ操作（CRUD）が可能になる。
 * - save(entity): エンティティの新規保存・更新
 * - findById(id): ID でエンティティを取得
 * - findAll(): 全エンティティを取得
 * - deleteById(id): ID でエンティティを削除
 * 
 * Spring Boot の DI により、Service クラスでこのリポジトリを注入して使用できる。
 */
@Repository // Spring にこのクラスをリポジトリとして認識させる
public interface AiAnalysisLogRepository extends JpaRepository<AiAnalysisLog, Integer> {
	// 必要に応じて追加
}
