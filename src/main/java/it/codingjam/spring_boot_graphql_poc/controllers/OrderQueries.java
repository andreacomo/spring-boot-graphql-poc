package it.codingjam.spring_boot_graphql_poc.controllers;

import graphql.schema.DataFetchingEnvironment;
import it.codingjam.spring_boot_graphql_poc.controllers.dtos.OrderDto;
import it.codingjam.spring_boot_graphql_poc.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.OffsetScrollPosition;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Slice;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.query.ScrollSubrange;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static it.codingjam.spring_boot_graphql_poc.controllers.DataFetcherUtils.getSelectedFieldNames;

@Controller
public class OrderQueries {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderQueries.class);

    private final OrderService orderService;

    public OrderQueries(OrderService orderService) {
        this.orderService = orderService;
    }

    @QueryMapping
    public OrderDto orderById(@Argument UUID id, DataFetchingEnvironment env) {
        Set<String> selectedFieldNames = getSelectedFieldNames(env);
        LOGGER.info("selectedFieldNames: {}", selectedFieldNames);
        env.getGraphQlContext().put("selectedFieldNames", selectedFieldNames);

        return orderService.findOrderById(id)
                .map(o -> new OrderDto(o.getId(), o.getCreationDate()))
                .orElse(null);
    }

    @QueryMapping
    public List<OrderDto> orders(DataFetchingEnvironment env) {
        Set<String> selectedFieldNames = getSelectedFieldNames(env);
        LOGGER.info("selectedFieldNames: {}", selectedFieldNames);
        env.getGraphQlContext().put("selectedFieldNames", selectedFieldNames);

        return orderService.findAllOrders().stream()
                .map(o -> new OrderDto(o.getId(), o.getCreationDate()))
                .toList();
    }

    /**
     * See https://bijukunjummen.medium.com/pagination-with-spring-graphql-b2eb2e019971
     * @param subrange
     * @return
     */
    @QueryMapping
    public Slice<OrderDto> ordersScrollable(ScrollSubrange subrange) {
        OffsetScrollPosition position = (OffsetScrollPosition) subrange.position().orElse(ScrollPosition.offset());
        long offset = position.isInitial() ? 0 : position.getOffset() + 1;
        int limit = subrange.count().orElse(20);
        return orderService.findAllOrders(limit, offset)
                .map(o -> new OrderDto(o.getId(), o.getCreationDate()));
    }
}
