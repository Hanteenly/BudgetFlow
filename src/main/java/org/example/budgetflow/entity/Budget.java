package org.example.budgetflow.entity;

import jakarta.persistence.*;
import org.example.budgetflow.Converters.YearMonthConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private BigDecimal limit;
    @Convert(converter = YearMonthConverter.class)
    private YearMonth period;

    public Budget(){

    }
    public Budget(Category category, BigDecimal limit, YearMonth period){
        this.category = category;
        this.limit = limit;
        this.period = period;
    }

    public Category getCategory(){
        return this.category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }

    public BigDecimal getLimit(){
        return this.limit;
    }
    public void setLimit(BigDecimal limit){
        this.limit = limit;
    }

    public YearMonth getPeriod() {
        return period;
    }
    public void setPeriod(YearMonth period){
        this.period = period;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }
}
