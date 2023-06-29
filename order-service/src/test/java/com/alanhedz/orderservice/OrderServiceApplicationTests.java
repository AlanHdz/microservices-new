package com.alanhedz.orderservice;

import com.alanhedz.orderservice.dto.OrderLineItemsDto;
import com.alanhedz.orderservice.dto.OrderRequest;
import com.alanhedz.orderservice.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.MySQLR2DBCDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class OrderServiceApplicationTests {

	@Container
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:latest")
			.withDatabaseName("testcontainer")
			.withUsername("root")
			.withPassword("test");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private OrderRepository orderRepository;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
		dynamicPropertyRegistry.add("spring.datasource.username", mySQLContainer::getUsername);
		dynamicPropertyRegistry.add("spring.datasource.password", mySQLContainer::getPassword);
	}

	@Test
	void shouldPlaceOrder() throws Exception {
		OrderRequest orderRequest = getOrderRequest();
		String orderRequestString = objectMapper.writeValueAsString(orderRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(orderRequestString))
				.andExpect(status().isCreated());

	}

	private OrderRequest getOrderRequest() {
		List<OrderLineItemsDto> orderLineItemsDto = getOrderLineItemsDto();
		
		return OrderRequest.builder()
				.orderLineItemsDtoList(orderLineItemsDto).build();
		
	}

	private List<OrderLineItemsDto> getOrderLineItemsDto() {
		List<OrderLineItemsDto> orderLineItemsDtoList = new ArrayList<>(Collections.emptyList());
		OrderLineItemsDto orderLineItemsDto = new OrderLineItemsDto();

		orderLineItemsDto.setSkuCode("iphone_13");
		orderLineItemsDto.setPrice(BigDecimal.valueOf(1200));
		orderLineItemsDto.setQuantity(200);

		orderLineItemsDtoList.add(orderLineItemsDto);

		return orderLineItemsDtoList;
		
	}

}
