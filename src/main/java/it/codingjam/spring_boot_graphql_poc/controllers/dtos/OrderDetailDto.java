package it.codingjam.spring_boot_graphql_poc.controllers.dtos;

import java.util.UUID;

public record OrderDetailDto(
        UUID id,
        Integer quantity,
        Float price
) {
}
