package service;

import dao.CurrencyDao;
import dto.CurrencyDto;
import model.CurrencyEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CurrencyService {

    CurrencyDao currencyDao = CurrencyDao.getInstance();

    private static final CurrencyService INSTANCE = new CurrencyService();
    private CurrencyService(){
    }
    public static CurrencyService getInstance() {
        return INSTANCE;
    }
    public CurrencyDto buildCurrencyDto(CurrencyEntity currencyEntity) {
        return new CurrencyDto(currencyEntity.getId(),
                currencyEntity.getCode(),
                currencyEntity.getFullName(),
                currencyEntity.getSign());
    }

}
