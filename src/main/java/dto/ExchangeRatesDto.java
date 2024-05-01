package dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder({"id", "baseCurrency", "targetCurrency", "rate"})
public class ExchangeRatesDto {
        private int id;
        @JsonProperty("baseCurrency")
        private CurrencyDto baseCurrencyDto;
        @JsonProperty("targetCurrency")
        private CurrencyDto targetCurrencyDto;
        private BigDecimal rate;

        public ExchangeRatesDto(int id, CurrencyDto baseCurrencyDto, CurrencyDto targetCurrencyDto, BigDecimal rate) {
            this.id = id;
            this.baseCurrencyDto = baseCurrencyDto;
            this.targetCurrencyDto = targetCurrencyDto;
            this.rate = rate;
        }

    public BigDecimal getRate() {
        return rate;
    }

    public CurrencyDto getBaseCurrencyDto() {
        return baseCurrencyDto;
    }

    public CurrencyDto getTargetCurrencyDto() {
        return targetCurrencyDto;
    }
}
