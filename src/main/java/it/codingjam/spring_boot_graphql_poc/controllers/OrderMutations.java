package it.codingjam.spring_boot_graphql_poc.controllers;

import graphql.schema.DataFetchingEnvironment;
import it.codingjam.spring_boot_graphql_poc.controllers.dtos.OrderDto;
import it.codingjam.spring_boot_graphql_poc.controllers.dtos.inputs.OrderDetailInput;
import it.codingjam.spring_boot_graphql_poc.models.Order;
import it.codingjam.spring_boot_graphql_poc.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

import static it.codingjam.spring_boot_graphql_poc.controllers.DataFetcherUtils.getSelectedFieldNames;

@Controller
public class OrderMutations {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderMutations.class);

    private final OrderService orderService;

    public OrderMutations(OrderService orderService) {
        this.orderService = orderService;
    }

    @MutationMapping
    public OrderDto createOrder(@Argument List<OrderDetailInput> orderDetails, DataFetchingEnvironment env) {
        Order order = orderService.saveOrder(orderDetails);
        env.getGraphQlContext().put("selectedFieldNames", getSelectedFieldNames(env));

        return new OrderDto(order.getId(), order.getCreationDate());
    }
}
