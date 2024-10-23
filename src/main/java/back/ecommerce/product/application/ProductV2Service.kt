package back.ecommerce.product.application

import back.ecommerce.common.constant.PageConstant
import back.ecommerce.exception.CustomException
import back.ecommerce.exception.ErrorCode.PRODUCT_NOT_FOUND
import back.ecommerce.product.dto.condition.ProductSearchCondition
import back.ecommerce.product.dto.response.v2.ProductDetailDto
import back.ecommerce.product.dto.response.v2.ProductListV2Response
import back.ecommerce.product.entity.Category
import back.ecommerce.product.repository.ProductQueryDslRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ProductV2Service(
    private val productQueryDslRepository: ProductQueryDslRepository,
) {

    fun findWithCategoryAndPagination(category: Category): ProductListV2Response {
        val products = productQueryDslRepository.findByCategoryWithPaginationOrderByBrandNewV2(
            category,
            PageRequest.of(PageConstant.DEFAULT__PAGE, PageConstant.DEFAULT_PAGE_SIZE)
        )
        return ProductListV2Response(products.size, products)
    }

    fun findWithSearchCondition(category: Category, parameters: Map<String, String?>): ProductListV2Response {
        val searchCondition = ProductSearchCondition.createWithCategoryAndAttributes(
            category, parameters
        )
        val products = productQueryDslRepository.findBySearchConditionV2(searchCondition)
        return ProductListV2Response(products.size, products)
    }

    fun findOne(id: Long): ProductDetailDto {
        return productQueryDslRepository.findByIdJoinImages(id) ?: throw CustomException(PRODUCT_NOT_FOUND)
    }
}
