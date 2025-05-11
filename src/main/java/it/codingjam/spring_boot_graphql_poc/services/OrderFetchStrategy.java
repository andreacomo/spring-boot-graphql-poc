package it.codingjam.spring_boot_graphql_poc.services;

import java.util.Set;

public enum OrderFetchStrategy {
    WITH_BOOKS,
    WITHOUT_BOOKS,
    WITH_BOOKS_AND_AUTHORS;

    public static OrderFetchStrategy fromSelectedFields(Set<String> selectedFields) {
        if (selectedFields.contains("Order.orderDetails/OrderDetail.book") &&
            selectedFields.contains("Order.orderDetails/OrderDetail.book/Book.author")) {
            return WITH_BOOKS_AND_AUTHORS;
        } else if (selectedFields.contains("Order.orderDetails/OrderDetail.book")) {
            return WITH_BOOKS;
        } else {
            return WITHOUT_BOOKS;
        }
    }
}
