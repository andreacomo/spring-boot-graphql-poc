package it.codingjam.spring_boot_graphql_poc.services;

import it.codingjam.spring_boot_graphql_poc.controllers.dtos.inputs.OrderDetailInput;
import it.codingjam.spring_boot_graphql_poc.models.Book;
import it.codingjam.spring_boot_graphql_poc.models.Order;
import it.codingjam.spring_boot_graphql_poc.models.OrderDetail;
import it.codingjam.spring_boot_graphql_poc.repositories.OrderRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final BookService bookService;

    public OrderService(OrderRepository orderRepository, BookService bookService) {
        this.orderRepository = orderRepository;
        this.bookService = bookService;
    }

    @Transactional(readOnly = true)
    public Optional<Order> findOrderById(UUID id) {
        return orderRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Slice<Order> findAllOrders(int limit, long offset) {
        int page = (int) (offset / limit);
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "creationDate"));
        return orderRepository.findSlice(pageRequest);
    }

    @Transactional(readOnly = true)
    public Map<Order, List<OrderDetail>> findDetailsByOrderId(List<UUID> orderIds, boolean withBooks) {
        if (withBooks) {
            return orderRepository.findOrderDetailsWithBooksByOrderId(orderIds).stream()
                    .collect(Collectors.groupingBy(OrderDetail::getOrder));
        } else {
            return orderRepository.findOrderDetailsByOrderId(orderIds).stream()
                    .collect(Collectors.groupingBy(OrderDetail::getOrder));
        }
    }

    @Transactional(readOnly = true)
    public Map<Order, List<OrderDetail>> findDetailsByOrderId(List<UUID> orderIds, OrderFetchStrategy strategy) {
        List<OrderDetail> details = switch (strategy) {
            case WITHOUT_BOOKS -> orderRepository.findOrderDetailsByOrderId(orderIds);
            case WITH_BOOKS -> orderRepository.findOrderDetailsWithBooksByOrderId(orderIds);
            case WITH_BOOKS_AND_AUTHORS -> orderRepository.findOrderDetailsWithBooksAndAuthorsByOrderId(orderIds);
        };
        return details.stream()
                .collect(Collectors.groupingBy(OrderDetail::getOrder));
    }

    @Transactional
    public Order saveOrder(List<OrderDetailInput> details) {
        Order order = new Order();
        order.setCreationDate(OffsetDateTime.now());
        List<OrderDetail> orderDetails = details.stream()
                .map(d -> {
                    Book book = bookService.findBookById(d.bookId())
                            .orElseThrow(() -> new IllegalArgumentException("Book not found: " + d.bookId()));
                    var detail = new OrderDetail();
                    detail.setOrder(order);
                    detail.setBook(book);
                    detail.setQuantity(d.quantity());
                    detail.setPrice(d.price());
                    return detail;
                })
                .toList();
        order.setDetails(orderDetails);

        return orderRepository.save(order);
    }
}
