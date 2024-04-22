package servlets.exchange;

import dto.ExchangeRatesDto;
import exception.RespException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExchangeRatesEntity;
import servlets.BaseServlet;

import java.io.IOException;

public class ExchangeRateServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String doubleCode = req.getPathInfo().replaceAll("/", "");
        try {
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
        } catch (RespException respException) {
            resp.setStatus(respException.getCode());
            objectMapper.writeValue(resp.getWriter(), respException);
        }
    }



}
