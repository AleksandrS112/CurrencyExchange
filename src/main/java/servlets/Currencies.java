package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDao;
import dto.CurrencyDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.CurrencyEntity;
import service.CurrencyService;

import java.io.IOException;
import java.util.List;

public class Currencies extends HttpServlet {

    CurrencyService currencyService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        currencyService = CurrencyService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        List<CurrencyDto> CurrenciesDto = currencyService.findAll();
        objectMapper.writeValue(response.getWriter(), CurrenciesDto);

    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code =  req.getParameter("code");
        String full_name = req.getParameter("full_name");
        String sign = req.getParameter("sign");
        CurrencyDao cd = CurrencyDao.getInstance();
        CurrencyEntity ce = new CurrencyEntity(code, full_name, sign);
        //cd.save(ce);
    }
}
