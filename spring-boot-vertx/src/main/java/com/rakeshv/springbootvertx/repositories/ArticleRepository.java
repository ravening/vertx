package com.rakeshv.springbootvertx.repositories;

import com.rakeshv.springbootvertx.models.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findByArticleEqualsIgnoreCase(String message);
}
