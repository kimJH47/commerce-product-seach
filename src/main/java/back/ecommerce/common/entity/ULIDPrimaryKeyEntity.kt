package back.ecommerce.common.entity

import com.github.f4b6a3.ulid.UlidCreator
import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import org.springframework.data.domain.Persistable
import java.io.Serializable
import java.util.*

@MappedSuperclass
class ULIDPrimaryKeyEntity(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private val id: UUID = UlidCreator.getMonotonicUlid().toUuid()
) : Persistable<UUID> {

    @Transient
    private var _isNew = true

    override fun getId(): UUID = id

    override fun isNew() = _isNew

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other !is HibernateProxy && this::class.java != other::class) {
            return false
        }
        return id == getIdentifier(other)
    }
    private fun getIdentifier(obj: Any): Serializable = when (obj) {
        is HibernateProxy -> {
            val identifier = obj.hibernateLazyInitializer.identifier
            if (identifier is Serializable) {
                identifier
            } else {
                throw IllegalArgumentException("Identifier is not Serializable")
            }
        }
        is ULIDPrimaryKeyEntity -> obj.id
        else -> throw IllegalArgumentException("Unsupported object type")
    }

    override fun hashCode() = Objects.hashCode(id)

    @PostPersist
    @PostLoad
    protected fun load() {
        _isNew = false
    }
}