package back.ecommerce.repository;

import static back.ecommerce.domain.product.QProduct.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.jpa.impl.JPAQueryFactory;

import back.ecommerce.domain.product.Category;
import back.ecommerce.dto.ProductDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ProductQueryDslRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public List<ProductDto> findByCategoryWithPaginationOrderByBrandNew(Category category, Pageable pageable) {
		return jpaQueryFactory.select(projectedProductDto())
			.from(product)
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset())
			.where(product.category.eq(category))
			.orderBy(product.createdDate.desc())
			.fetch();
	}

	private QBean<ProductDto> projectedProductDto() {
		return Projections.fields(ProductDto.class,
			product.id, product.name, product.brandName, product.price, product.category);
	}
}
