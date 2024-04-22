package servlets.currency;

import dto.CurrencyDto;
import exception.RespException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.CurrencyEntity;
import servlets.BaseServlet;

import java.io.IOException;

public class CurrencyServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getPathInfo().replaceAll("/", "");
        try {
            if (code.isBlank()) {
                throw new RespException(400, "Код валюты отсутствует в адресе");
            }
            if (!code.matches("[A-Z]{3}"))
                throw new RespException(400, "Не верно указан код валюты");
            CurrencyEntity currencyEntity = currencyDao.findByCode(code)
                    .orElseThrow(() -> new RespException(404, "Валюта не найдена"));
            CurrencyDto currencyDto = currencyService.buildCurrencyDto(currencyEntity);
            objectMapper.writeValue(resp.getWriter(), currencyDto);
        } catch (RespException respException) {
            resp.setStatus(respException.getCode());
            objectMapper.writeValue(resp.getWriter(), respException);
        }
    }
}
