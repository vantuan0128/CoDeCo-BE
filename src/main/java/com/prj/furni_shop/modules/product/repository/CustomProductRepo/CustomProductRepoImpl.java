package com.prj.furni_shop.modules.product.repository.CustomProductRepo;

import com.prj.furni_shop.modules.product.dto.request.ProductFilterDto;
import com.prj.furni_shop.modules.product.entity.Product;
import com.prj.furni_shop.modules.product.entity.ProductDetail;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomProductRepoImpl implements CustomProductRepo {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Product> getFilteredProducts(ProductFilterDto filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        Join<Product, ProductDetail> productDetailJoin = root.join("productDetails", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();

        if (filter.getCategoryIds() != null && !filter.getCategoryIds().isEmpty()) {
            predicates.add(root.get("categoryId").in(filter.getCategoryIds()));
        }

        if (filter.getSizeIds() != null && !filter.getSizeIds().isEmpty()) {
            predicates.add(productDetailJoin.get("sizeId").in(filter.getSizeIds()));
        }

        if (filter.getColorIds() != null && !filter.getColorIds().isEmpty()) {
            predicates.add(productDetailJoin.get("colorId").in(filter.getColorIds()));
        }

        if (filter.getMaterialIds() != null && !filter.getMaterialIds().isEmpty()) {
            predicates.add(productDetailJoin.get("materialId").in(filter.getMaterialIds()));
        }

        if (filter.getFromPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("minPrice"), filter.getFromPrice()));
        }

        if (filter.getToPrice() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("maxPrice"), filter.getToPrice()));
        }

        if (filter.getSearchValue() != null && !filter.getSearchValue().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getSearchValue().toLowerCase() + "%"));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        List<Order> orders = new ArrayList<>();
        if (filter.getNewest() != null && filter.getNewest()) {
            orders.add(cb.desc(root.get("createdAt")));
        } else if (filter.getBestSeller() != null && filter.getBestSeller()) {
            orders.add(cb.desc(root.get("soldCount")));
        }

        if (filter.getPriceSort() != null) {
            if ("asc".equalsIgnoreCase(filter.getPriceSort())) {
                orders.add(cb.asc(root.get("minPrice")));
            } else if ("desc".equalsIgnoreCase(filter.getPriceSort())) {
                orders.add(cb.desc(root.get("minPrice")));
            }
        }

        cq.orderBy(orders);

        List<Product> products = entityManager.createQuery(cq).getResultList();

        return products != null ? products : new ArrayList<>();
    }

}
