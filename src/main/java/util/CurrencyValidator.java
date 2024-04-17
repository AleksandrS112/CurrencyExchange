package util;

import exception.RespException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CurrencyValidator {

    public static void checkCurrencyProperties(String code, String fullName, String sign) {
        if (code == null || code.isBlank() || fullName == null || fullName.isBlank() || sign == null || sign.isBlank()) {
            Map<String, String> mapParameters = new HashMap<>();
            mapParameters.put("код валюты", code);
            mapParameters.put("название валюты", fullName);
            mapParameters.put("символ валюты", sign);
            String message = Stream.of(mapParameters).flatMap(e -> e.entrySet().stream())
                    .filter(e -> e.getValue() == null || e.getValue().isBlank())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.joining(", ", "Поля: ", " - не заполнены."));
            throw new RespException(new RuntimeException(), 400, message);
        }
        if (!code.matches("[A-Z]{3}"))
            throw new RespException(new RuntimeException(), 400, "Код валюты состоит не из 3 заглавных букв латинского алфавита.");
        if (fullName.length() > 255)
            throw new RespException(new RuntimeException(), 400, "Полное название валюты не должно превышать 255 символов.");
        if (sign.length() > 3)
            throw new RespException(new RuntimeException(), 400, "Символ валюты не должен превышать 3 символов.");
    }

    public static void checkExchangeRates(String baseCurrenciesCode, String targetCurrenciesCode, String rate) {
        if (baseCurrenciesCode == null || baseCurrenciesCode.isBlank()
                || targetCurrenciesCode == null || targetCurrenciesCode.isBlank()
                || rate == null || rate.isBlank()) {
            Map<String, String> mapParameters = new HashMap<>();
            mapParameters.put("код базовой валюты", baseCurrenciesCode);
            mapParameters.put("код целевой валюты", targetCurrenciesCode);
            mapParameters.put("курс обмена", rate);
            String message = Stream.of(mapParameters).flatMap(e -> e.entrySet().stream())
                    .filter(e -> e.getValue() == null || e.getValue().isBlank())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.joining(", ", "Поля: ", " - не заполнены."));
            throw new RespException(new RuntimeException(), 400, message);
        }
        if (!baseCurrenciesCode.matches("[A-Z]{3}"))
            throw new RespException(new RuntimeException(), 400, "Код базовой валюты состоит не из 3 заглавных букв латинского алфавита.");
        if (!targetCurrenciesCode.matches("[A-Z]{3}"))
            throw new RespException(new RuntimeException(), 400, "Код целевой валюты состоит не из 3 заглавных букв латинского алфавита.");
        try {
            Double.parseDouble(rate);
        } catch (NumberFormatException e) {
            throw new RespException(e, 400, "Неверно указан курс обмена");
        }
    }

}
