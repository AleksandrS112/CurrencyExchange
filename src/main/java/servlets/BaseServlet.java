package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDao;
import dao.ExchangeRatesDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import service.CurrencyService;
import service.ExchangeRatesService;

public class BaseServlet extends HttpServlet {

    protected CurrencyDao currencyDao;
    protected ExchangeRatesDao exchangeRatesDao;
    protected CurrencyService currencyService;
    protected ExchangeRatesService exchangeRatesService;
    protected ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        currencyDao = CurrencyDao.getInstance();
        exchangeRatesDao = ExchangeRatesDao.getInstance();
        currencyService = CurrencyService.getInstance();
        exchangeRatesService = ExchangeRatesService.getInstance();
        objectMapper = new ObjectMapper();
    }
}
