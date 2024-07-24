package com.kaua.ecommerce.lib.domain.pagination;

public record PaginationMetadata(
        int currentPage,
        int perPage,
        int totalPages,
        long totalItems
) {
}
