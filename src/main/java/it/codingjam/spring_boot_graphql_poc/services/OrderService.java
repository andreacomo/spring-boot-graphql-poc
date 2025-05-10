package it.codingjam.spring_boot_graphql_poc.services;

import it.codingjam.spring_boot_graphql_poc.controllers.dtos.inputs.OrderDetailInput;
import it.codingjam.spring_boot_graphql_poc.models.Book;
import it.codingjam.spring_boot_graphql_poc.models.Order;
import it.codingjam.spring_boot_graphql_poc.models.OrderDetail;
import it.codingjam.spring_boot_graphql_poc.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public List<OrderDetail> findDetailsByOrderId(UUID orderId, boolean withBooks) {
        if (withBooks) {
            return orderRepository.findOrderDetailsWithBooksByOrderId(List.of(orderId));
        } else {
            return orderRepository.findOrderDetailsByOrderId(List.of(orderId));
        }
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
