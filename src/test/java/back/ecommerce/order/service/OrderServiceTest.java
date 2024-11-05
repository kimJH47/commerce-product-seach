package back.ecommerce.order.service;

import static back.ecommerce.order.entity.OrderStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import back.ecommerce.api.payment.OrderProductDto;
import back.ecommerce.common.generator.ULIDGenerator;
import back.ecommerce.exception.CustomException;
import back.ecommerce.order.OrderGroupRepository;
import back.ecommerce.order.application.OrderService;
import back.ecommerce.order.entity.OrderGroup;
import back.ecommerce.order.entity.OrderItem;
import back.ecommerce.product.entity.Category;
import back.ecommerce.product.entity.Product;
import back.ecommerce.product.repository.ProductRepository;
import back.ecommerce.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@Mock
	OrderGroupRepository orderGroupRepository;
	@Mock
	ProductRepository productRepository;
	@Mock
	UserRepository userRepository;
	@Mock
	ULIDGenerator ulidGenerator;

	OrderService orderService;

	@BeforeEach
	void setUp() {
		orderService = new OrderService(orderGroupRepository, productRepository, userRepository, ulidGenerator);
	}

	@Test
	void createOrder() {
		//given
		String orderCode = "d20bfgdsc231-bf3d-4d63-a616-831610627a05";
		ArrayList<Product> products = new ArrayList<>();
		products.add(new Product(1L, "상품1", "브랜드1", 250000L, Category.ACCESSORY));
		products.add(new Product(10L, "상품2", "브랜드2", 250000L, Category.ACCESSORY));

		ArrayList<OrderItem> orderItems = new ArrayList<>();
		orderItems.add(new OrderItem(100L, null, 1L, "상품1", 250000L, 1));
		orderItems.add(new OrderItem(101L, null, 10L, "상품2", 250000L, 1));

		given(userRepository.existsByEmail(anyString())).willReturn(true);
		given(productRepository.findByIds(anyList())).willReturn(products);
		given(orderGroupRepository.save(any(OrderGroup.class)))
			.willReturn(new OrderGroup(
				101L, orderCode, "상품1 등 2개", "user@email.com", 500000L, 2, PAYMENT_READY,
				orderItems));

		//when
		OrderGroupDto order = orderService.createOrder("user@email.com", 500000L, getOrderProducts());

		//then
		assertThat(order.getOrderCode()).isEqualTo(orderCode);
		assertThat(order.getName()).isEqualTo("상품1 등 2개");
		assertThat(order.getTotalPrice()).isEqualTo(500000L);
		assertThat(order.getQuantity()).isEqualTo(2);

		then(userRepository).should(times(1)).existsByEmail(anyString());
		then(productRepository).should(times(1)).findByIds(anyList());
		then(orderGroupRepository).should(times(1)).save(any(OrderGroup.class));
	}

	@Test
	void createOrder_user_not_found() {
		//given
		given(userRepository.existsByEmail(anyString())).willReturn(false);

		//when
		assertThatThrownBy(() -> orderService.createOrder("user@email.com", 500000L, getOrderProducts()))
			.isInstanceOf(CustomException.class)
			.hasMessage("해당하는 유저가 존재하지 않습니다.");

		//then
		then(userRepository).should(times(1)).existsByEmail(anyString());
		then(productRepository).should(times(0)).findByIds(anyList());
		then(orderGroupRepository).should(times(0)).save(any(OrderGroup.class));
	}

	@Test
	void createOrder_invalid_order_argument() {
		//given
		ArrayList<Product> products = new ArrayList<>();
		products.add(new Product(1L, "상품1", "브랜드1", 500000L, Category.ACCESSORY));

		given(userRepository.existsByEmail(anyString())).willReturn(true);
		given(productRepository.findByIds(anyList())).willReturn(products);

		//when
		assertThatThrownBy(() -> orderService.createOrder("user@email.com", 500000L, getOrderProducts()))
			.isInstanceOf(CustomException.class)
			.hasMessage("주문정보가 옳바르지 않습니다.");

		//then
		then(userRepository).should(times(1)).existsByEmail(anyString());
		then(productRepository).should(times(1)).findByIds(anyList());
		then(orderGroupRepository).should(times(0)).save(any(OrderGroup.class));
	}

	@Test
	void createOrder_invalid_totalPrice() {
		ArrayList<Product> products = new ArrayList<>();
		products.add(new Product(1L, "상품1", "브랜드1", 250000L, Category.ACCESSORY));
		products.add(new Product(10L, "상품2", "브랜드2", 300000L, Category.ACCESSORY));

		given(userRepository.existsByEmail(anyString())).willReturn(true);
		given(productRepository.findByIds(anyList())).willReturn(products);

		//when
		assertThatThrownBy(() -> orderService.createOrder("user@email.com", 500000L, getOrderProducts()))
			.isInstanceOf(CustomException.class)
			.hasMessage("요청 주문상품가격과 실제 가격정보가 일치하지 않습니다.");

		//then
		then(userRepository).should(times(1)).existsByEmail(anyString());
		then(productRepository).should(times(1)).findByIds(anyList());
		then(orderGroupRepository).should(times(0)).save(any(OrderGroup.class));

	}

	private List<OrderProductDto> getOrderProducts() {
		List<OrderProductDto> orderProducts = new ArrayList<>();
		orderProducts.add(createOrderDto(1L, "name1", 1, 250000L));
		orderProducts.add(createOrderDto(10L, "name2", 1, 250000L));
		return orderProducts;
	}

	private OrderProductDto createOrderDto(long productId, String name, int quantity, long price) {
		return new OrderProductDto(productId, name, quantity, price);
	}
}

