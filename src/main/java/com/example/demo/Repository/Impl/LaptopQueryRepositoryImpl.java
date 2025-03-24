package com.example.demo.Repository.Impl;

import com.example.demo.Common.ConvertDate;
import com.example.demo.Common.Enums;
import com.example.demo.DTO.Response.LaptopResponse;
import com.example.demo.Models.Laptop;
import com.example.demo.Repository.LaptopQueryRepository;
import com.example.demo.mapper.LaptopMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class LaptopQueryRepositoryImpl implements LaptopQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private static <T> T getFilterValue(Map<String, Object> filters, String key, Function<String, T> converter) {
        Object value = filters.get(key);
        if (value == null) {
            return null;
        }
        return converter.apply(value.toString());
    }

    @Override
    public List<LaptopResponse> searchLaptopsByLaptopModelAndLaptop(Map<String, Object> filters) {
        StringBuilder queryStr = new StringBuilder("SELECT l FROM Laptop l JOIN l.laptopModel lm WHERE 1=1 ");
        boolean hasCondition = false;

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();

            switch (field) {
                case "MFG":
                    queryStr.append("AND l.MFG = :MFG ");

                    break;
                case "status":
                    queryStr.append("AND l.status = :status ");

                    break;
                case "color":
                    queryStr.append("AND lm.color = :color ");

                    break;
                case "min_price":
                    queryStr.append("AND lm.price >= :min_price ");

                    break;
                case "max_price":
                    queryStr.append("AND lm.price <= :max_price ");

                    break;
                default:
                    queryStr.append("AND lm.").append(field).append(" = :").append(field).append(" ");

                    break;
            }

        }

        TypedQuery<Laptop> query = entityManager.createQuery(queryStr.toString(), Laptop.class);

        for (String field : filters.keySet()) {
            Object value;
            switch (field) {
                case "MFG":
                    Date MFG = getFilterValue(filters, "MFG", ConvertDate::convertToDate);
                    query.setParameter(field, MFG);
                    break;
                case "status":
                    Enums.laptopStatus status = getFilterValue(filters, "status", Enums.laptopStatus::valueOf);
                    query.setParameter(field, status);
                    break;
                case "color":
                    Enums.Color color = getFilterValue(filters, "color", Enums.Color::valueOf);
                    query.setParameter(field, color);
                    break;
                case "min_price":
                case "max_price":
                    BigDecimal minPrice = getFilterValue(filters, "min_price", BigDecimal::new);
                    query.setParameter(field, minPrice);
                    break;
                default:
                    String attribute = getFilterValue(filters, field, Function.identity());
                    query.setParameter(field, attribute);
                    break;
            }

        }
        List<Laptop> laptops = query.getResultList();

        return laptops.stream().map(
                        LaptopMapper::convertToResponse
                )
                .collect(Collectors.toList());
    }
}
