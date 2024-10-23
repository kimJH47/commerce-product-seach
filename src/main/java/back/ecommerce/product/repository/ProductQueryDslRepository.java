package back.ecommerce.product.repository;

import static back.ecommerce.product.dto.condition.ProductSortCondition.*;
import static back.ecommerce.product.entity.QProduct.*;
import static back.ecommerce.product.entity.QProductImages.*;
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

import back.ecommerce.product.dto.condition.ProductSearchCondition;
import back.ecommerce.product.dto.condition.ProductSortCondition;
import back.ecommerce.product.dto.response.ProductDto;
import back.ecommerce.product.dto.response.v2.ProductDetailDto;
import back.ecommerce.product.dto.response.v2.ProductV2Dto;
import back.ecommerce.product.dto.response.v2.QProductV2Dto;
import back.ecommerce.product.entity.Category;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ProductQueryDslRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public List<ProductDto> findByCategoryWithPaginationOrderByBrandNew(Category category, Pageable pageable) {
		List<Long> ids = findIdsByCategoryOrderByBrandNew(category, pageable);
		if (ids.isEmpty()) {
			return new ArrayList<>();
		}
		return findProductWithIdsAndOrderSpec(ids, category, product.createdDate.desc());
	}

	public List<ProductV2Dto> findByCategoryWithPaginationOrderByBrandNewV2(Category category, Pageable pageable) {
		List<Long> ids = findIdsByCategoryOrderByBrandNew(category, pageable);
		if (ids.isEmpty()) {
			return new ArrayList<>();
		}
		return findProductWithIdsAndOrderSpecV2(ids, category, product.createdDate.desc());
	}

	private List<Long> findIdsByCategoryOrderByBrandNew(Category category, Pageable pageable) {
		return jpaQueryFactory.select(product.id)
			.from(product)
			.where(eqCategory(category))
			.orderBy(product.createdDate.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	public List<ProductDto> findBySearchCondition(ProductSearchCondition productSearchCondition) {
		List<Long> ids = findIdsBySearchCondition(productSearchCondition);
		if (ids.isEmpty()) {
			return new ArrayList<>();
		}
		return findProductWithIdsAndOrderSpec(ids, productSearchCondition.getCategory(),
			defineOrderBy(productSearchCondition.getSortCondition()));
	}

	public List<ProductV2Dto> findBySearchConditionV2(ProductSearchCondition productSearchCondition) {
		List<Long> ids = findIdsBySearchCondition(productSearchCondition);
		if (ids.isEmpty()) {
			return new ArrayList<>();
		}
		return findProductWithIdsAndOrderSpecV2(ids, productSearchCondition.getCategory(),
			defineOrderBy(productSearchCondition.getSortCondition()));
	}

	private List<Long> findIdsBySearchCondition(ProductSearchCondition productSearchCondition) {
		OrderSpecifier<?> orderSpecifier = defineOrderBy(productSearchCondition.getSortCondition());
		return jpaQueryFactory.select(product.id)
			.from(product)
			.where(
				likeName(productSearchCondition.getName()),
				likeBrandName(productSearchCondition.getBranName()),
				eqCategory(productSearchCondition.getCategory()),
				minPrice(productSearchCondition.getMinPrice()),
				maxPrice(productSearchCondition.getMaxPrice())
			)
			.limit(productSearchCondition.getPageSize())
			.offset(productSearchCondition.getOffset())
			.orderBy(orderSpecifier)
			.fetch();
	}

	public ProductDetailDto findByIdJoinImages(Long id) {
		return jpaQueryFactory.select(
				Projections.fields(ProductDetailDto.class,
					product.id, product.name, product.brandName, product.price, productImages.imageUrls,
					productImages.catalogUrl
				)
			)
			.from(product)
			.join(productImages).on(product.id.eq(productImages.productId))
			.where(product.id.eq(id))
			.fetchOne();
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
		return product.name.like(name + "%");
	}

	private BooleanExpression likeBrandName(String brandName) {
		if (!StringUtils.hasText(brandName)) {
			return null;
		}
		return product.brandName.like(brandName + "%");
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

	private List<ProductDto> findProductWithIdsAndOrderSpec(List<Long> ids, Category category,
		OrderSpecifier<?> orderSpecifier) {
		return jpaQueryFactory.select(
				Projections.fields(ProductDto.class,
					product.id,
					product.name, product.brandName, product.price,
					asEnum(category).as(product.category)))
			.from(product)
			.where(product.id.in(ids))
			.orderBy(orderSpecifier)
			.fetch();
	}

	private List<ProductV2Dto> findProductWithIdsAndOrderSpecV2(List<Long> ids, Category category,
		OrderSpecifier<?> orderSpecifier) {
		return jpaQueryFactory.select(
				new QProductV2Dto(product.id,
					product.name, product.brandName, product.price, asEnum(category).as(product.category),
					asString("").as("thumbnailUrl")))
			.from(product)
			.leftJoin(productImages).on(product.id.eq(productImages.productId))
			.where(product.id.in(ids))
			.orderBy(orderSpecifier)
			.fetch();
	}
}
