package servlets;

import exception.RespException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CurrencyValidator {

    public static void checkCurrencyProperties(String code, String fullName, String sign) {
        if (code == null || code.isBlank() || fullName == null || fullName.isBlank() || sign == null || sign.isBlank()) {
            Map<String, String> mapProperties = new HashMap<>();
            mapProperties.put("код валюты", code);
            mapProperties.put("название валюты", fullName);
            mapProperties.put("символ валюты", sign);
            String message = Stream.of(mapProperties).flatMap(e -> e.entrySet().stream())
                    .filter(e -> e.getValue() == null || e.getValue().isBlank())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.joining(", ", "Поля: ", " - не заполнены."));
            throw new RespException(new RuntimeException(), 400, message);
        }
        if (!code.matches("[A-Z]{3}"))
            throw new RespException(new RuntimeException(), 409, "Код валюты состоит не из 3 заглавных букв латинского алфавита.");
        if (fullName.length() > 255)
            throw new RespException(new RuntimeException(), 409, "Полное название валюты не должно превышать 255 символов.");
        if (sign.length() != 3)
            throw new RespException(new RuntimeException(), 409, "Символ валюты не должен превышать 3 символов.");
    }
}
