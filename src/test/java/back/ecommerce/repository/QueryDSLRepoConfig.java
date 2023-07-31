package back.ecommerce.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.querydsl.jpa.impl.JPAQueryFactory;

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
