package back.ecommerce.product.dto.request

import back.ecommerce.product.entity.Category

class ProductSearchConditionRequest private constructor(val category: Category, val parameters: Map<String, String?>) {
    companion object {
        fun create(category: Category, parameters: MutableMap<String, String?>): ProductSearchConditionRequest {
            validateEmptyCondition(parameters)
            return ProductSearchConditionRequest(category, parameters)
        }

        private fun validateEmptyCondition(parameters: MutableMap<String, String?>) {
            parameters.putIfAbsent("name", null)
            parameters.putIfAbsent("brandName", null)
            parameters.putIfAbsent("minPrice", null)
            parameters.putIfAbsent("maxPrice", null)
            parameters.putIfAbsent("sort", "new")
            parameters.putIfAbsent("page", "")
        }
    }
}
