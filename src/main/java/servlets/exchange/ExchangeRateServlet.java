package servlets.exchange;

import dao.ExchangeRatesDao;
import dto.ExchangeRatesDto;
import exception.RespException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExchangeRatesEntity;
import service.ExchangeRatesService;
import servlets.BaseServlet;

import java.io.IOException;

public class ExchangeRateServlet extends BaseServlet {

    protected ExchangeRatesService exchangeRatesService;
    protected ExchangeRatesDao exchangeRatesDao;
    private static final String METHOD_PATCH = "PATCH";

    @Override
    public void init() throws ServletException {
        exchangeRatesDao = ExchangeRatesDao.getInstance();
        exchangeRatesService = ExchangeRatesService.getInstance();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase(METHOD_PATCH)) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String doubleCode = req.getPathInfo().replaceAll("/", "");
        if (doubleCode.isBlank()) {
            throw new RespException(400, "Коды валют пары отсутствуют в адресе");
        }
        if (!doubleCode.matches("[A-Z]{6}")) {
            throw new RespException(400, "Некорректно указаны коды валют");
        }
        String baseCurrency = doubleCode.substring(0, 3);
        String targetCurrency = doubleCode.substring(3);
        ExchangeRatesEntity exchangeRatesEntity = exchangeRatesDao.findByCodes(baseCurrency, targetCurrency)
                .orElseThrow(() -> new RespException(404, "Обменный курс для пары не найден"));
        ExchangeRatesDto exchangeRatesDto = exchangeRatesService.buildExchangeRatesDto(exchangeRatesEntity);
        objectMapper.writeValue(resp.getWriter(), exchangeRatesDto);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) {

    }
}
