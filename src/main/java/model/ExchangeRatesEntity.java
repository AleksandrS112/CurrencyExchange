package model;

import java.math.BigDecimal;

public class ExchangeRatesEntity {
    private int id;
    private CurrencyEntity baseCurrencyEntity;
    private CurrencyEntity targetCurrencyEntity;
    private BigDecimal rate;

    public ExchangeRatesEntity(int id, CurrencyEntity baseCurrency, CurrencyEntity targetCurrency, BigDecimal rate) {
        this.id = id;
        this.baseCurrencyEntity = baseCurrency;
        this.targetCurrencyEntity = targetCurrency;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public CurrencyEntity getBaseCurrencyEntity() {
        return baseCurrencyEntity;
    }

    public CurrencyEntity getTargetCurrencyEntity() {
        return targetCurrencyEntity;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBaseCurrencyEntity(CurrencyEntity baseCurrencyEntity) {
        this.baseCurrencyEntity = baseCurrencyEntity;
    }

    public void setTargetCurrencyEntity(CurrencyEntity targetCurrencyEntity) {
        this.targetCurrencyEntity = targetCurrencyEntity;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "ExchangeRates{" +
               "id=" + id +
               ", baseCurrency=" + baseCurrencyEntity.toString() +
               ", targetCurrency=" + targetCurrencyEntity.toString() +
               ", rate=" + rate +
               '}';
    }
}
