package servlets.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.math.BigDecimal;

public class ExchangeRateServlet extends BaseServlet {

    protected ExchangeRatesService exchangeRatesService;
    protected ExchangeRatesDao exchangeRatesDao;
    private static final String METHOD_PATCH = "PATCH";

    @Override
    public void init() throws ServletException {
        exchangeRatesDao = ExchangeRatesDao.getInstance();
        exchangeRatesService = ExchangeRatesService.getInstance();
        super.init();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase(METHOD_PATCH)) {
            resp.setContentType("application/json; charset=utf-8");
            req.setCharacterEncoding("UTF-8");
            try {
                doPatch(req, resp);
            } catch (RespException respException) {
                resp.setStatus(respException.getCode());
                objectMapper.writeValue(resp.getWriter(), respException);
            }
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String doubleCode = req.getPathInfo().replaceAll("/", "");
        checkDoubleCode(doubleCode);
        String baseCurrency = doubleCode.substring(0, 3);
        String targetCurrency = doubleCode.substring(3);
        ExchangeRatesEntity exchangeRatesEntity = exchangeRatesDao.findByCodes(baseCurrency, targetCurrency)
                .orElseThrow(() -> new RespException(404, "Обменный курс для пары не найден"));
        ExchangeRatesDto exchangeRatesDto = exchangeRatesService.buildExchangeRatesDto(exchangeRatesEntity);
        objectMapper.writeValue(resp.getWriter(), exchangeRatesDto);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String doubleCode = req.getPathInfo().replaceAll("/", "");
        checkDoubleCode(doubleCode);
        String rateParam = req.getParameter("rate");
        BigDecimal rate;
        try {
            rate = BigDecimal.valueOf(Double.parseDouble(rateParam)).stripTrailingZeros();
        } catch (NullPointerException e) {
            throw new RespException(400, "Не указано значение курс обмена");
        } catch (NumberFormatException e) {
            throw new RespException(400, "Некорректно указан курс обмена");
        }
        String baseCurrencyCode = doubleCode.substring(0, 3);
        String targetCurrencyCode = doubleCode.substring(3);
        CurrencyEntity baseCurrencyEntity = new CurrencyEntity();
        baseCurrencyEntity.setCode(baseCurrencyCode);
        CurrencyEntity targetCurrencyEntity = new CurrencyEntity();
        targetCurrencyEntity.setCode(targetCurrencyCode);
        ExchangeRatesEntity exchangeRatesEntity = new ExchangeRatesEntity(
                baseCurrencyEntity,
                targetCurrencyEntity,
                rate);
        exchangeRatesDao.update(exchangeRatesEntity);
        objectMapper.writeValue(resp.getWriter(), exchangeRatesService.buildExchangeRatesDto(exchangeRatesDao.findByCodes(baseCurrencyCode, targetCurrencyCode).get()));
    }

    private static void checkDoubleCode(String doubleCode) {
        if (doubleCode.isBlank()) {
            throw new RespException(400, "Коды валют пары отсутствуют в адресе");
        }
        if (!doubleCode.matches("[A-Z]{6}")) {
            throw new RespException(400, "Некорректно указаны коды валют");
        }
    }
}
