package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "name", "code", "sign" })
public class CurrencyDto {
    @JsonProperty("id")
    private int id;
    @JsonProperty("code")
    private String code;
    @JsonProperty("name")
    private String fullName;
    @JsonProperty("sign")
    private String sign;

    public CurrencyDto(int id, String code, String fullName, String sign) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }
}
