package service;

import dao.currency.CurrencyDao;
import dto.CurrencyDto;

import java.util.List;
import java.util.stream.Collectors;

public class CurrencyService {

    CurrencyDao currencyDao = CurrencyDao.getInstance();

    private static final CurrencyService INSTANCE = new CurrencyService();
    private CurrencyService(){
    }
    public static CurrencyService getInstance() {
        return INSTANCE;
    }
    public List<CurrencyDto> findAll() {
        return currencyDao.findAll().stream()
                .map(ce -> new CurrencyDto(
                        ce.getId(),
                        ce.getCode(),
                        ce.getFullName(),
                        ce.getSign()))
                .collect(Collectors.toList());
    }
}
