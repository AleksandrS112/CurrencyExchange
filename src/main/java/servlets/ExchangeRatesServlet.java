package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ExchangeRatesDao;
import dto.ExchangeRatesDto;
import exception.RespException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRatesService;

import java.io.IOException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;

public class ExchangeRatesServlet extends HttpServlet {

    ExchangeRatesService exchangeRatesService;
    ExchangeRatesDao exchangeRatesDao;
    ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
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

        /*
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
        */

}
