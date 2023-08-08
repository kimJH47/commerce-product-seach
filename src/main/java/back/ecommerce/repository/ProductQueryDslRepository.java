package back.ecommerce.repository;

import static back.ecommerce.domain.product.QProduct.*;
import static back.ecommerce.dto.ProductSortCondition.*;
import static com.querydsl.core.types.dsl.Expressions.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import back.ecommerce.domain.product.Category;
import back.ecommerce.dto.ProductDto;
import back.ecommerce.dto.ProductSearchCondition;
import back.ecommerce.dto.ProductSortCondition;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ProductQueryDslRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public List<ProductDto> findByCategoryWithPaginationOrderByBrandNew(Category category, Pageable pageable) {
		List<Long> ids = jpaQueryFactory.select(product.id)
			.from(product)
			.where(eqCategory(category))
			.orderBy(product.createdDate.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
		if (ids.isEmpty()) {
			return new ArrayList<>();
		}
		return jpaQueryFactory.select(Projections.fields(ProductDto.class,
				product.id, product.name, product.brandName, product.price,
				asEnum(category).as(product.category)))
			.from(product)
			.where(product.id.in(ids))
			.orderBy(product.createdDate.desc())
			.fetch();
	}

	public List<ProductDto> findBySearchCondition(ProductSearchCondition productSearchCondition) {
		OrderSpecifier<?> orderBy = defineOrderBy(productSearchCondition.getSortCondition());

		List<Long> ids = jpaQueryFactory.select(product.id)
			.from(product)
			.where(
				eqCategory(productSearchCondition.getCategory()),
				minPrice(productSearchCondition.getMinPrice())
					.and(maxPrice(productSearchCondition.getMaxPrice())),
				likeBrandName(productSearchCondition.getBranName()),
				likeName(productSearchCondition.getName()))
			.limit(productSearchCondition.getPageSize())
			.offset(productSearchCondition.getOffset())
			.orderBy(orderBy)
			.fetch();

		if (ids.isEmpty()) {
			return new ArrayList<>();
		}

		return jpaQueryFactory.select(
				Projections.fields(ProductDto.class,
					product.id,
					product.name, product.brandName, product.price,
					asEnum(productSearchCondition.getCategory()).as(product.category)))
			.from(product)
			.where(product.id.in(ids))
			.orderBy(orderBy)
			.fetch();

	}

	private BooleanExpression eqCategory(Category category) {
		return product.category.eq(category);
	}

	private BooleanExpression minPrice(Long price) {
		if (price == null) {
			return null;
		}
		return product.price.goe(price);
	}

	private BooleanExpression maxPrice(Long price) {
		if (price == null) {
			return null;
		}
		return product.price.loe(price);
	}

	private BooleanExpression likeName(String name) {
		if (!StringUtils.hasText(name)) {
			return null;
		}
		return product.name.like("%" + name + "%");
	}

	private BooleanExpression likeBrandName(String brandName) {
		if (!StringUtils.hasText(brandName)) {
			return null;
		}
		return product.brandName.like("%" + brandName + "%");
	}

	private OrderSpecifier<?> defineOrderBy(ProductSortCondition sortCondition) {
		if (PRICE_HIGH.equals(sortCondition)) {
			return product.price.desc();
		}
		if (PRICE_LOW.equals(sortCondition)) {
			return product.price.asc();
		}
		return product.createdDate.desc();
	}
}
