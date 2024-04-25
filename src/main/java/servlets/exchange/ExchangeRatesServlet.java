package servlets.exchange;

import dao.ExchangeRatesDao;
import dto.ExchangeRatesDto;
import exception.RespException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.CurrencyEntity;
import model.ExchangeRatesEntity;
import service.ExchangeRatesService;
import servlets.BaseServlet;
import util.Validator;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;

public class ExchangeRatesServlet extends BaseServlet {

    protected ExchangeRatesService exchangeRatesService;
    protected ExchangeRatesDao exchangeRatesDao;

    @Override
    public void init() throws ServletException {
        exchangeRatesDao = ExchangeRatesDao.getInstance();
        exchangeRatesService = ExchangeRatesService.getInstance();
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ExchangeRatesDto> allExchangeRatesDto = exchangeRatesDao.findAll().stream()
                .map(exchangeRatesEntity -> exchangeRatesService.buildExchangeRatesDto(exchangeRatesEntity))
                .toList();
        objectMapper.writeValue(resp.getWriter(), allExchangeRatesDto);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrenciesCode = req.getParameter("baseCurrenciesCode");
        String targetCurrenciesCode = req.getParameter("targetCurrenciesCode");
        String rate = req.getParameter("rate");
        Validator.checkExchangeRatesParam(baseCurrenciesCode, targetCurrenciesCode, rate);
        CurrencyEntity baseCurrencies = currencyDao.findByCode(baseCurrenciesCode)
                .orElseThrow(() -> new RespException(404, "Базовая валюта с кодом " + baseCurrenciesCode + " отсутствует"));
        CurrencyEntity targetCurrencies = currencyDao.findByCode(targetCurrenciesCode)
                .orElseThrow(() -> new RespException(404, "Целевая валюта с кодом " + targetCurrenciesCode + " отсутствует"));
        ExchangeRatesEntity exchangeRatesEntity = new ExchangeRatesEntity(
                baseCurrencies,
                targetCurrencies,
                BigDecimal.valueOf(Double.parseDouble(rate))
        );
        exchangeRatesEntity = exchangeRatesDao.save(exchangeRatesEntity);
        ExchangeRatesDto exchangeRatesDto = exchangeRatesService.buildExchangeRatesDto(exchangeRatesEntity);
        objectMapper.writeValue(resp.getWriter(), exchangeRatesDto);
        resp.setStatus(SC_CREATED);
    }

}
