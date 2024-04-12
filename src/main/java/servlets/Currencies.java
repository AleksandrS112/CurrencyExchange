package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.currency.CurrencyDao;
import dto.CurrencyDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.CurrencyEntity;
import service.CurrencyService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Currencies extends HttpServlet {

    CurrencyService currencyService;

    @Override
    public void init() throws ServletException {
        currencyService = CurrencyService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        List<CurrencyDto> CurrenciesDto = currencyService.findAll();
        ObjectMapper om = new ObjectMapper();
        om.writeValue(writer, CurrenciesDto);

       /*
        ObjectMapper objectMapper = new ObjectMapper();
        var currencies = CurrencyDao.getInstance().findAll();
        objectMapper.writeValue(response.getWriter(), currencies);
        */
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
