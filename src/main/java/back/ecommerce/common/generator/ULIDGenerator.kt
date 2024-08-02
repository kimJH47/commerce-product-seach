package back.ecommerce.common.generator

import com.github.f4b6a3.ulid.UlidCreator
import org.springframework.stereotype.Component
import java.util.*

@Component
class ULIDGenerator {
    fun generateULIDToUUID(): UUID = UlidCreator.getUlid().toUuid()
}