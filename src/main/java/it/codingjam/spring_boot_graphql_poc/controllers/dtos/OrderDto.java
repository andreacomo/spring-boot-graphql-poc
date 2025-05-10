package it.codingjam.spring_boot_graphql_poc.controllers.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OrderDto(
        UUID id,
        OffsetDateTime creationDate
) {
}
