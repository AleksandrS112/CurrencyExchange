package dto;

public record CurrencyFilter(int limit,
                             int offset,
                             String code,
                             String full_name,
                             String sign) {
}
