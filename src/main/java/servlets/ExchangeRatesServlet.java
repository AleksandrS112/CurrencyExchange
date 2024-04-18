package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDao;
import dao.ExchangeRatesDao;
import dto.ExchangeRatesDto;
import exception.RespException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.CurrencyEntity;
import model.ExchangeRatesEntity;
import service.ExchangeRatesService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static util.CurrencyValidator.checkExchangeRates;

public class ExchangeRatesServlet extends HttpServlet {

    ExchangeRatesService exchangeRatesService;
    ExchangeRatesDao exchangeRatesDao;
    CurrencyDao currencyDao;
    ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        currencyDao = CurrencyDao.getInstance();
        exchangeRatesDao = ExchangeRatesDao.getInstance();
        objectMapper = new ObjectMapper();
        exchangeRatesService = ExchangeRatesService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");
        try {
            List<ExchangeRatesDto> allExchangeRatesDto = exchangeRatesDao.findAll().stream()
                    .map(exchangeRatesEntity -> exchangeRatesService.buildExchangeRatesDto(exchangeRatesEntity))
                    .toList();
            resp.setStatus(SC_OK);
            objectMapper.writeValue(resp.getWriter(), allExchangeRatesDto);
        } catch (RespException respException) {
            resp.setStatus(respException.getCode());
            objectMapper.writeValue(resp.getWriter(), respException.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");
        String baseCurrenciesCode = req.getParameter("baseCurrenciesCode");
        String targetCurrenciesCode = req.getParameter("targetCurrenciesCode");
        String rate = req.getParameter("rate");
        try {
            checkExchangeRates(baseCurrenciesCode, targetCurrenciesCode, rate);
            Optional<CurrencyEntity> baseCurrencies = currencyDao.findByCode(baseCurrenciesCode);
            if (baseCurrencies.isEmpty()) {
                throw new RespException(new RuntimeException(), 404, "Базовая валюта с кодом " +baseCurrenciesCode +" отсутствует");
            }
            Optional<CurrencyEntity> targetCurrencies = currencyDao.findByCode(targetCurrenciesCode);
            if (targetCurrencies.isEmpty()) {
                throw new RespException(new RuntimeException(), 404, "Целевая валюта с кодом " +targetCurrenciesCode +" отсутствует");
            }
            ExchangeRatesEntity exchangeRatesEntity = new ExchangeRatesEntity(baseCurrencies.get(),
                    targetCurrencies.get(),
                    BigDecimal.valueOf(Double.parseDouble(rate))
            );
            exchangeRatesEntity = exchangeRatesDao.save(exchangeRatesEntity);
            ExchangeRatesDto exchangeRatesDto = exchangeRatesService.buildExchangeRatesDto(exchangeRatesEntity);
            resp.setStatus(SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), exchangeRatesDto);
        } catch (RespException respException) {
            resp.setStatus(respException.getCode());
            objectMapper.writeValue(resp.getWriter(), respException);
        }
    }

}
