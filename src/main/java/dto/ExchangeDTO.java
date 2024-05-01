package dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder({"baseCurrency", "targetCurrency", "rate", "amount", "convertedAmount"})
public class ExchangeDTO {
    @JsonProperty("baseCurrency")
    private CurrencyDto baseCurrencyDto;
    @JsonProperty("targetCurrency")
    private CurrencyDto targetCurrencyDto;

    private BigDecimal rate;

    private BigDecimal amount;

    private BigDecimal convertedAmount;
    public ExchangeDTO(CurrencyDto baseCurrencyDto, CurrencyDto targetCurrencyDto, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
        this.baseCurrencyDto = baseCurrencyDto;
        this.targetCurrencyDto = targetCurrencyDto;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }

    public ExchangeDTO() {

    }
}
