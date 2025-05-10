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
import it.codingjam.spring_boot_graphql_poc.services.OrderService;
import org.dataloader.BatchLoaderEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        List<String> selectedFieldNames = getSelectedFieldNames(env);
        LOGGER.info("selectedFieldNames: {}", selectedFieldNames);
        env.getGraphQlContext().put("selectedFieldNames", selectedFieldNames);
        env.getGraphQlContext().put("orderId", id);

        return orderService.findOrderById(id)
                .map(o -> {
                    return new OrderDto(o.getId(), o.getCreationDate());
                })
                .orElse(null);
    }

    @SchemaMapping(typeName = "Order")
    public List<OrderDetailDto> orderDetails(OrderDto order, @ContextValue("selectedFieldNames") List<String> selectedFieldNames, GraphQLContext context) {
        boolean withBooks = selectedFieldNames.contains("Order.orderDetails/OrderDetail.book");
        Map<UUID, Book> detailIdToBook = new HashMap<>();
        context.put("detailIdToBook", detailIdToBook);
        return orderService.findDetailsByOrderId(order.id(), withBooks).stream()
                .map(d -> {
                    detailIdToBook.put(d.getId(), d.getBook());
                    return new OrderDetailDto(d.getId(), d.getQuantity(), d.getPrice());
                })
                .toList();
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

    @MutationMapping
    public OrderDto createOrder(@Argument List<OrderDetailInput> orderDetails, DataFetchingEnvironment env) {
        Order order = orderService.saveOrder(orderDetails);
        env.getGraphQlContext().put("selectedFieldNames", getSelectedFieldNames(env));
        env.getGraphQlContext().put("orderId", order.getId());
        return new OrderDto(order.getId(), order.getCreationDate());
    }

    private static List<String> getSelectedFieldNames(DataFetchingEnvironment env) {
        return env.getSelectionSet().getFields().stream()
                .map(SelectedField::getFullyQualifiedName)
                .toList();
    }

//    @SchemaMapping(typeName = "OrderDetail")
//    public BookDto book(OrderDetailDto detail, GraphQLContext context) {
//        Map<UUID, Book> orderIdToBook = context.get("orderIdToBook");
//        return new BookDto(orderIdToBook.get(detail.id()));
//    }
}
