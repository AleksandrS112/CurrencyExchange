package dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CurrencyDto {
    private int id;
    private String code;
    private String name;
    private String sign;

    public CurrencyDto(int id, String code, String fullName, String sign) {
        this.id = id;
        this.code = code;
        this.name = fullName;
        this.sign = sign;
    }
}
