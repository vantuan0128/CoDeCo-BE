package com.prj.furni_shop.modules.product.repository.CustomProductDetailRepo;

import com.prj.furni_shop.modules.product.dto.response.ProductDetailInfoResponse;
import com.prj.furni_shop.modules.product.entity.ProductDetail;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomProductDetailRepoImpl implements CustomProductDetailRepo {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public ProductDetailInfoResponse getTotalQuantityAndPrice(Integer productId, Integer sizeId, Integer colorId, Integer materialId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> quantityQuery = cb.createQuery(Integer.class);
        Root<ProductDetail> quantityRoot = quantityQuery.from(ProductDetail.class);

        List<Predicate> quantityPredicates = new ArrayList<>();
        if (productId != null) {
            quantityPredicates.add(cb.equal(quantityRoot.get("productId"), productId));
        }
        if (sizeId != null) {
            quantityPredicates.add(cb.equal(quantityRoot.get("sizeId"), sizeId));
        }
        if (colorId != null) {
            quantityPredicates.add(cb.equal(quantityRoot.get("colorId"), colorId));
        }
        if (materialId != null) {
            quantityPredicates.add(cb.equal(quantityRoot.get("materialId"), materialId));
        }

        quantityQuery.select(cb.coalesce(cb.sum(quantityRoot.get("quantity")), 0));
        quantityQuery.where(quantityPredicates.toArray(new Predicate[0]));

        Integer totalQuantity = entityManager.createQuery(quantityQuery).getSingleResult();

        Integer price = null;
        Integer productDetailId = null;

        if (sizeId != null && colorId != null && materialId != null) {
            CriteriaQuery<Object[]> priceQuery = cb.createQuery(Object[].class);
            Root<ProductDetail> priceRoot = priceQuery.from(ProductDetail.class);

            List<Predicate> pricePredicates = new ArrayList<>();
            pricePredicates.add(cb.equal(priceRoot.get("productId"), productId));
            pricePredicates.add(cb.equal(priceRoot.get("sizeId"), sizeId));
            pricePredicates.add(cb.equal(priceRoot.get("colorId"), colorId));
            pricePredicates.add(cb.equal(priceRoot.get("materialId"), materialId));

            priceQuery.multiselect(priceRoot.get("price"), priceRoot.get("productDetailId"));
            priceQuery.where(pricePredicates.toArray(new Predicate[0]));

            List<Object[]> results = entityManager.createQuery(priceQuery).getResultList();
            if (!results.isEmpty()) {
                Object[] result = results.get(0);
                price = (Integer) result[0];
                productDetailId = (Integer) result[1];
            }
        }

        return new ProductDetailInfoResponse(productDetailId, totalQuantity, price);
    }
}
