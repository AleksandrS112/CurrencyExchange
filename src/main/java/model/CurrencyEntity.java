package model;

public class CurrencyEntity {
    private int id;
    private String code;
    private String fullName;
    private String sign;

    public CurrencyEntity(String code, String fullName, String sign) {
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }
    public CurrencyEntity(int id, String code, String fullName, String sign) {
        this(code, fullName, sign);
        this.id = id;
    }
    public CurrencyEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "CurrencyEntity{" +
               "id=" + id +
               ", code='" + code + '\'' +
               ", fullName='" + fullName + '\'' +
               ", sign='" + sign + '\'' +
               '}';
    }
}
