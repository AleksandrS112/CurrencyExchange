package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDao;
import dto.CurrencyDto;
import exception.RespException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.CurrencyEntity;
import service.CurrencyService;

import java.io.IOException;
import java.util.Optional;

public class CurrencyServlet extends HttpServlet {

    CurrencyDao currencyDao;
    ObjectMapper objectMapper;
    CurrencyService currencyService;

    @Override
    public void init() throws ServletException {
        currencyDao = CurrencyDao.getInstance();
        currencyService = CurrencyService.getInstance();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");
        String code = req.getPathInfo().replaceAll("/", "");
        try {
            if (code.isBlank()) {
                throw new RespException(400, "Код валюты отсутствует в адресе");
            }
            if (!code.matches("[A-Z]{3}"))
                throw new RespException(400, "Не верно указан код валюты");
            Optional<CurrencyEntity> currencyEntity = currencyDao.findByCode(code);
            if (currencyEntity.isEmpty())
                throw new RespException(404, "Валюта не найдена");
            CurrencyDto currencyDto = currencyService.buildCurrencyDto(currencyEntity.get());
            objectMapper.writeValue(resp.getWriter(), currencyDto);
        } catch (RespException respException) {
            resp.setStatus(respException.getCode());
            objectMapper.writeValue(resp.getWriter(), respException);
        }
    }
}
