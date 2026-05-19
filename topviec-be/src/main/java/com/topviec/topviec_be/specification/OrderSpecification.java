package com.topviec.topviec_be.specification;

import com.topviec.topviec_be.entity.Order;
import com.topviec.topviec_be.enums.services.OrderStatus;
import com.topviec.topviec_be.enums.services.OrderType;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

import java.time.LocalDateTime;

public class OrderSpecification {

    public static Specification<Order> hasCompanyId(Long companyId) {
        return (root, query, cb) -> companyId == null
                ? null
                : cb.equal(root.get("companyId"), companyId);
    }

    public static Specification<Order> searchKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }
            String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
            Join<Object, Object> company = root.join("company", JoinType.LEFT);
            return cb.or(
                    cb.like(cb.lower(root.get("orderCode")), likeKeyword),
                    cb.like(cb.lower(company.get("name")), likeKeyword));
        };
    }

    public static Specification<Order> hasType(OrderType type) {
        return (root, query, cb) -> type == null
                ? null
                : cb.equal(root.get("type"), type);
    }

    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) -> status == null
                ? null
                : cb.equal(root.get("status"), status);
    }

    public static Specification<Order> createdAfter(LocalDateTime startDate) {
        return (root, query, cb) -> startDate == null
                ? null
                : cb.greaterThanOrEqualTo(root.get("createdAt"), startDate);
    }

    public static Specification<Order> createdBefore(LocalDateTime endDate) {
        return (root, query, cb) -> endDate == null
                ? null
                : cb.lessThanOrEqualTo(root.get("createdAt"), endDate);
    }

    public static Specification<Order> withFilter(
            String search, OrderType type, OrderStatus status,
            LocalDateTime startDate, LocalDateTime endDate) {
        return Specification.where(searchKeyword(search))
                .and(hasType(type))
                .and(hasStatus(status))
                .and(createdAfter(startDate))
                .and(createdBefore(endDate));
    }
}
