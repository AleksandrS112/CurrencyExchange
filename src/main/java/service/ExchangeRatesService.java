package service;

import dao.ExchangeRatesDao;
import dto.ExchangeRatesDto;
import model.ExchangeRatesEntity;

public class ExchangeRatesService {
    ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    CurrencyService currencyService = CurrencyService.getInstance();

    private static final ExchangeRatesService INSTANCE = new ExchangeRatesService();
    private ExchangeRatesService() {

    }
    public static ExchangeRatesService getInstance() {
        return INSTANCE;
    }
    public ExchangeRatesDto buildExchangeRatesDto(ExchangeRatesEntity exchangeRatesEntity) {
        return new ExchangeRatesDto(exchangeRatesEntity.getId(),
                currencyService.buildCurrencyDto(exchangeRatesEntity.getBaseCurrencyEntity()),
                currencyService.buildCurrencyDto(exchangeRatesEntity.getTargetCurrencyEntity()),
                exchangeRatesEntity.getRate());
    }
}
