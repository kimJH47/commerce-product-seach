package back.ecommerce.api.spec

import back.ecommerce.admin.repository.AdminRepository
import back.ecommerce.auth.service.TokenExtractor
import back.ecommerce.auth.service.TokenProvider
import back.ecommerce.common.logging.GlobalLogger
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.extensions.spring.SpringExtension
import io.mockk.clearAllMocks
import io.mockk.mockkClass
import org.junit.platform.commons.util.ClassFilter
import org.junit.platform.commons.util.ReflectionUtils
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Service

@WebMvcTest
@AutoConfigureRestDocs
@Import(MockServiceBeanFactoryPostProcessor::class, MockConfigurationBeansPostProcessor::class)
abstract class ApiTestSpec(
    body: DescribeSpec.() -> Unit = {}
) : DescribeSpec(body) {

    override fun extensions(): List<Extension> {
        return listOf(SpringExtension)
    }

    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        clearAllMocks()
    }
}


class MockServiceBeanFactoryPostProcessor : BeanFactoryPostProcessor {
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        val classFilter =
            ClassFilter.of { it.isAnnotationPresent(Service::class.java) }
        ReflectionUtils.findAllClassesInPackage("back.ecommerce", classFilter).forEach {
            beanFactory.registerSingleton(it.simpleName, mockkClass(it.kotlin))
        }
        beanFactory.registerSingleton("adminRepository", mockkClass(AdminRepository::class))
        beanFactory.registerSingleton("globalLogger", mockkClass(GlobalLogger::class))
        beanFactory.registerSingleton("TokenProvider", mockkClass(TokenProvider::class))
        beanFactory.registerSingleton("TokenExtractor", mockkClass(TokenExtractor::class))
    }
}

class MockConfigurationBeansPostProcessor : BeanFactoryPostProcessor {
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        val classFilter = ClassFilter.of { it.isAnnotationPresent(Configuration::class.java) }

        ReflectionUtils.findAllClassesInPackage("back.ecommerce.publisher", classFilter).forEach { configClass ->
            val methods = configClass.declaredMethods.filter { it.isAnnotationPresent(Bean::class.java) }
            methods.forEach { method ->
                val beanName = method.name
                val beanType = method.returnType.kotlin
                val mockBean = mockkClass(beanType)
                beanFactory.registerSingleton(beanName, mockBean)
            }
        }

        ReflectionUtils.findAllClassesInPackage("back.ecommerce.client", classFilter).forEach { configClass ->
            val methods = configClass.declaredMethods.filter { it.isAnnotationPresent(Bean::class.java) }
            methods.forEach { method ->
                val beanName = method.name
                val beanType = method.returnType.kotlin
                val mockBean = mockkClass(beanType)
                beanFactory.registerSingleton(beanName, mockBean)
            }
        }

    }
}