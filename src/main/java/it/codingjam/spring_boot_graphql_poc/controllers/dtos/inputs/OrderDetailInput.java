package it.codingjam.spring_boot_graphql_poc.controllers.dtos.inputs;

import java.util.UUID;

public record OrderDetailInput(
        Integer quantity,
        Float price,
        UUID bookId
) {
}
