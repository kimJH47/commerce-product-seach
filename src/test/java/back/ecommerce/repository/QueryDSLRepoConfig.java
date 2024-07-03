package back.ecommerce.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.querydsl.jpa.impl.JPAQueryFactory;

import back.ecommerce.product.repository.ProductQueryDslRepository;

@TestConfiguration
public class QueryDSLRepoConfig {


	@PersistenceContext
	private EntityManager entityManager;

	@Bean
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(entityManager);
	}
	@Bean
	public ProductQueryDslRepository productQueryDslRepository() {
		return new ProductQueryDslRepository(jpaQueryFactory());
	}
}
