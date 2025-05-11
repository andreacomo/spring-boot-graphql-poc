package it.codingjam.spring_boot_graphql_poc.controllers;

import graphql.GraphQLContext;
import it.codingjam.spring_boot_graphql_poc.controllers.dtos.BookDto;
import it.codingjam.spring_boot_graphql_poc.controllers.dtos.OrderDetailDto;
import it.codingjam.spring_boot_graphql_poc.controllers.dtos.OrderDto;
import it.codingjam.spring_boot_graphql_poc.models.Book;
import it.codingjam.spring_boot_graphql_poc.services.OrderFetchStrategy;
import it.codingjam.spring_boot_graphql_poc.services.OrderService;
import org.dataloader.BatchLoaderEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class OrderDataFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderDataFetcher.class);

    private final OrderService orderService;

    public OrderDataFetcher(OrderService orderService) {
        this.orderService = orderService;
    }

    @BatchMapping(typeName = "Order")
    public Map<OrderDto, List<OrderDetailDto>> orderDetails(List<OrderDto> orders, GraphQLContext context) {
        Map<UUID, Book> detailIdToBook = new HashMap<>();
        context.put("detailIdToBook", detailIdToBook);

        List<UUID> orderIds = orders.stream()
                .map(OrderDto::id)
                .toList();
        Map<UUID, OrderDto> ordersById = orders.stream()
                .collect(Collectors.toMap(OrderDto::id, Function.identity()));

        OrderFetchStrategy strategy = OrderFetchStrategy.fromSelectedFields(context.get("selectedFieldNames"));
        LOGGER.info("Using fetch strategy: {}", strategy);
        return orderService.findDetailsByOrderId(orderIds, strategy).entrySet().stream()
                .reduce(
                        new HashMap<>(),
                        (acc, entry) -> {
                            acc.computeIfAbsent(ordersById.get(entry.getKey().getId()), order -> entry.getValue().stream()
                                    .map(d -> {
                                        detailIdToBook.put(d.getId(), d.getBook());
                                        return new OrderDetailDto(d.getId(), d.getQuantity(), d.getPrice());
                                    })
                                    .toList());
                            return acc;
                        },
                        (o1, o2) -> o1);
    }

    @BatchMapping(typeName = "OrderDetail")
    public Map<OrderDetailDto, BookDto> book(List<OrderDetailDto> details, @ContextValue("detailIdToBook") Map<UUID, Book> detailIdToBook , BatchLoaderEnvironment env) {
        return details.stream()
                .reduce(
                        new HashMap<>(),
                        (acc, dto) -> {
                            Book book = detailIdToBook.get(dto.id());
                            if (book != null) {
                                acc.put(dto, new BookDto(book));
                            }
                            return acc;
                        },
                        (o1, o2) -> o1);
    }
}
