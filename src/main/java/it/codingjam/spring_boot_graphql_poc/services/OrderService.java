package it.codingjam.spring_boot_graphql_poc.services;

import it.codingjam.spring_boot_graphql_poc.models.Order;
import it.codingjam.spring_boot_graphql_poc.models.OrderDetail;
import it.codingjam.spring_boot_graphql_poc.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
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
}
