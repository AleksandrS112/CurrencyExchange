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
    public List<CurrencyDto> findAll() {
        return currencyDao.findAll().stream()
                .map(this::buildCurrencyDto)
                .collect(Collectors.toList());
    }

    public Optional<CurrencyDto> findByCode(String code) {
        Optional<CurrencyEntity> currencyEntityOptional = currencyDao.findByCode(code);
        if (currencyEntityOptional.isEmpty()) {
            return Optional.empty();
        } else {
            CurrencyEntity currencyEntity = currencyEntityOptional.get();
            return Optional.of(buildCurrencyDto(currencyEntity));
        }
    }
    public CurrencyDto buildCurrencyDto(CurrencyEntity currencyEntity) {
        return new CurrencyDto(currencyEntity.getId(),
                currencyEntity.getCode(),
                currencyEntity.getFullName(),
                currencyEntity.getSign());
    }

}
