package model;

import java.math.BigDecimal;

public class ExchangeRatesEntity {
    private int id;
    private CurrencyEntity baseCurrency;
    private CurrencyEntity targetCurrency;
    private BigDecimal rate;

    public ExchangeRatesEntity(int id, CurrencyEntity baseCurrency, CurrencyEntity targetCurrency, BigDecimal rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public CurrencyEntity getBaseCurrency() {
        return baseCurrency;
    }

    public CurrencyEntity getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBaseCurrency(CurrencyEntity baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public void setTargetCurrency(CurrencyEntity targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "ExchangeRates{" +
               "id=" + id +
               ", baseCurrency=" + baseCurrency.toString() +
               ", targetCurrency=" + targetCurrency.toString() +
               ", rate=" + rate +
               '}';
    }
}
