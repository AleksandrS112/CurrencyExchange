package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDao;
import dao.ExchangeRatesDao;
import exception.RespException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyService;
import service.ExchangeRatesService;

import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {

    protected CurrencyDao currencyDao;
    protected CurrencyService currencyService;
    protected ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        currencyDao = CurrencyDao.getInstance();
        currencyService = CurrencyService.getInstance();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=utf-8");
        try {
                super.service(req, resp);
        } catch (RespException respException) {
            resp.setStatus(respException.getCode());
            objectMapper.writeValue(resp.getWriter(), respException);
        }
    }
}
