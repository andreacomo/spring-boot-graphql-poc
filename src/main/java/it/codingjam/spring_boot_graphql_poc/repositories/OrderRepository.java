package it.codingjam.spring_boot_graphql_poc.repositories;

import it.codingjam.spring_boot_graphql_poc.models.Order;
import it.codingjam.spring_boot_graphql_poc.models.OrderDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("select d from OrderDetail d join fetch d.book b join fetch b.author where d.order.id in (:orderIds)")
    List<OrderDetail> findOrderDetailsWithBooksAndAuthorsByOrderId(List<UUID> orderIds);

    @Query("select d from OrderDetail d join fetch d.book b where d.order.id in (:orderIds)")
    List<OrderDetail> findOrderDetailsWithBooksByOrderId(List<UUID> orderIds);

    @Query("select d from OrderDetail d where d.order.id in (:orderIds)")
    List<OrderDetail> findOrderDetailsByOrderId(List<UUID> orderIds);

    @Query("SELECT o FROM Order o")
    Slice<Order> findSlice(Pageable pageable);
}
