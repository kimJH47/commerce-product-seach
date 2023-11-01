package back.ecommerce.order.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import back.ecommerce.api.payment.OrderProductDto;
import back.ecommerce.common.generator.UuidGenerator;
import back.ecommerce.exception.CustomException;
import back.ecommerce.exception.ErrorCode;
import back.ecommerce.order.OrderGroupRepository;
import back.ecommerce.order.entity.OrderGroup;
import back.ecommerce.product.entity.Product;
import back.ecommerce.product.repository.ProductRepository;
import back.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderGroupRepository orderGroupRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final UuidGenerator uuidGenerator;

	public OrderGroupDto createOrder(String userEmail, Long totalPrice, List<OrderProductDto> orderProducts) {
		validateUserEmail(userEmail);
		List<Product> products = findByIds(orderProducts);
		validateOrder(orderProducts, products, totalPrice);
		OrderGroup orderGroup = orderGroupRepository.save(createOrderGroup(userEmail, totalPrice, orderProducts));
		return OrderGroupDto.create(orderGroup);
	}

	private List<Product> findByIds(List<OrderProductDto> orderProducts) {
		return productRepository.findByIds(orderProducts.stream()
			.map(OrderProductDto::getProductId)
			.collect(Collectors.toList()));
	}

	private void validateUserEmail(String userEmail) {
		if (!userRepository.existsByEmail(userEmail)) {
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
		}
	}

	private void validateOrder(List<OrderProductDto> orderProducts, List<Product> products, Long totalPrice) {
		if (orderProducts.size() != products.size()) {
			throw new CustomException(ErrorCode.INVALID_ORDER_ARGUMENT);
		}
		if (products.stream()
			.mapToLong(Product::getPrice)
			.sum() != totalPrice) {
			throw new CustomException(ErrorCode.INVALID_TOTAL_PRICE);
		}
	}

	private OrderGroup createOrderGroup(String email, Long totalPrice, List<OrderProductDto> products) {
		String orderCode = uuidGenerator.create();
		String name = createShortenName(products);
		return OrderGroup.createWithOrderItems(email, totalPrice, name, orderCode, products);
	}

	private String createShortenName(List<OrderProductDto> products) {
		if (products.size() == 1) {
			return products.get(0).getName();
		}
		return String.format("%s 등 총 %s개의 상품", products.get(0).getName(), products.size());
	}

}
