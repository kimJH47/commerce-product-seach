package back.ecommerce.product.repository

import back.ecommerce.product.entity.ProductImages
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ProductImageRepository : CrudRepository<ProductImages, UUID>