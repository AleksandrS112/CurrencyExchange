package service;

import dao.CurrencyDao;
import dao.ExchangeRatesDao;
import dto.ExchangeDTO;
import dto.ExchangeRatesDto;
import exception.RespException;
import model.CurrencyEntity;
import model.ExchangeRatesEntity;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeService {

    ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();

    CurrencyDao currencyDao = CurrencyDao.getInstance();
    CurrencyService currencyService = CurrencyService.getInstance();

    MathContext mathContext = new MathContext(12, RoundingMode.HALF_UP);

    private static final ExchangeService INSTANCE = new ExchangeService();

    private ExchangeService() {

    }

    public static ExchangeService getInstance() {
        return INSTANCE;
    }

    public Optional<ExchangeDTO> executeExchange(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        ExchangeRatesDto exchangeRatesDto = null;
        //прямой
        Optional<ExchangeRatesEntity> exchangeRatesEntityOptional = exchangeRatesDao.findByCodes(baseCurrencyCode, targetCurrencyCode);
        if (exchangeRatesEntityOptional.isPresent()) {
            exchangeRatesDto = exchangeRatesService.buildExchangeRatesDto(exchangeRatesEntityOptional.get());
            return Optional.of(new ExchangeDTO(
                    exchangeRatesDto.getBaseCurrencyDto(),
                    exchangeRatesDto.getTargetCurrencyDto(),
                    exchangeRatesDto.getRate(),
                    amount,
                    new BigDecimal(amount.multiply(exchangeRatesDto.getRate()).stripTrailingZeros().toPlainString())
            ));
        }
        //обратный
        exchangeRatesEntityOptional = exchangeRatesDao.findByCodes(targetCurrencyCode, baseCurrencyCode);
        if (exchangeRatesEntityOptional.isPresent()) {
            exchangeRatesDto = exchangeRatesService.buildExchangeRatesDto(exchangeRatesEntityOptional.get());
            BigDecimal reverseRate = new BigDecimal(new BigDecimal(1).divide(exchangeRatesDto.getRate(), mathContext).stripTrailingZeros().toPlainString());
            return Optional.of(new ExchangeDTO(
                    exchangeRatesDto.getTargetCurrencyDto(),
                    exchangeRatesDto.getBaseCurrencyDto(),
                    reverseRate,
                    amount,
                    new BigDecimal(amount.multiply(reverseRate).stripTrailingZeros().toPlainString())
            ));
        }
        // кросс
        Optional<ExchangeRatesEntity> exchangeRatesBaseToUsdOptional;
        Optional<ExchangeRatesEntity> exchangeRatesUsdToTargetOptional;
        BigDecimal rateBaseToUsd;
        BigDecimal rateUsdToTarget;

        exchangeRatesBaseToUsdOptional = exchangeRatesDao.findByCodes(baseCurrencyCode, "USD");
        if (exchangeRatesBaseToUsdOptional.isPresent()) {
            rateBaseToUsd = exchangeRatesBaseToUsdOptional.get().getRate();
        } else {
            exchangeRatesBaseToUsdOptional = exchangeRatesDao.findByCodes("USD", baseCurrencyCode);
            if (exchangeRatesBaseToUsdOptional.isPresent()) {
                rateBaseToUsd = new BigDecimal(1).divide(exchangeRatesBaseToUsdOptional.get().getRate(), mathContext);
            } else
                return Optional.empty();
        }

        exchangeRatesUsdToTargetOptional = exchangeRatesDao.findByCodes("USD", targetCurrencyCode);
        if (exchangeRatesUsdToTargetOptional.isPresent()) {
            rateUsdToTarget = exchangeRatesUsdToTargetOptional.get().getRate();
        } else {
            exchangeRatesUsdToTargetOptional = exchangeRatesDao.findByCodes(targetCurrencyCode,"USD");
            if (exchangeRatesUsdToTargetOptional.isPresent()) {
                rateUsdToTarget = new BigDecimal(1).divide(exchangeRatesUsdToTargetOptional.get().getRate(), mathContext);
            } else
                return Optional.empty();
        }
        BigDecimal crossRate = rateBaseToUsd.multiply(rateUsdToTarget);
        return Optional.of(new ExchangeDTO(
                currencyService.buildCurrencyDto(currencyDao.findByCode(baseCurrencyCode).get()),
                currencyService.buildCurrencyDto(currencyDao.findByCode(targetCurrencyCode).get()),
                crossRate,
                amount,
                amount.multiply(crossRate)
        ));
    }

}
