package it.codingjam.spring_boot_graphql_poc.controllers;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.SelectedField;
import it.codingjam.spring_boot_graphql_poc.controllers.dtos.BookDto;
import it.codingjam.spring_boot_graphql_poc.controllers.dtos.OrderDetailDto;
import it.codingjam.spring_boot_graphql_poc.controllers.dtos.OrderDto;
import it.codingjam.spring_boot_graphql_poc.controllers.dtos.inputs.OrderDetailInput;
import it.codingjam.spring_boot_graphql_poc.models.Book;
import it.codingjam.spring_boot_graphql_poc.models.Order;
import it.codingjam.spring_boot_graphql_poc.services.OrderFetchStrategy;
import it.codingjam.spring_boot_graphql_poc.services.OrderService;
import org.dataloader.BatchLoaderEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

//    @QueryMapping
//    public Order orderById(@Argument UUID id, GraphQLContext context) {
//        return orderService.findOrderById(id)
//                .orElse(null);
//    }

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

    @MutationMapping
    public OrderDto createOrder(@Argument List<OrderDetailInput> orderDetails, DataFetchingEnvironment env) {
        Order order = orderService.saveOrder(orderDetails);
        env.getGraphQlContext().put("selectedFieldNames", getSelectedFieldNames(env));

        return new OrderDto(order.getId(), order.getCreationDate());
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

    private static Set<String> getSelectedFieldNames(DataFetchingEnvironment env) {
        return env.getSelectionSet().getFields().stream()
                .map(SelectedField::getFullyQualifiedName)
                .collect(Collectors.toSet());
    }

//    @SchemaMapping(typeName = "OrderDetail")
//    public BookDto book(OrderDetailDto detail, GraphQLContext context) {
//        Map<UUID, Book> orderIdToBook = context.get("orderIdToBook");
//        return new BookDto(orderIdToBook.get(detail.id()));
//    }
}
