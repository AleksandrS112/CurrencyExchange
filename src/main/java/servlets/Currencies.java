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
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_CONFLICT;
import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;

public class Currencies extends HttpServlet {

    CurrencyService currencyService;
    CurrencyDao currencyDao;
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        currencyService = CurrencyService.getInstance();
        currencyDao = CurrencyDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
       //List<CurrencyDto> CurrenciesDto = currencyService.findAll();
        //objectMapper.writeValue(response.getWriter(), CurrenciesDto);

    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("utf-8");
        String code = req.getParameter("code");
        String fullName = req.getParameter("full_name");
        String sign = req.getParameter("sign");
        try {
            CurrencyValidator.checkCurrencyProperties(code, fullName, sign);
            CurrencyEntity currencyEntity = currencyDao.save(new CurrencyEntity(code, fullName, sign));
            CurrencyDto currencyDto = currencyService.buildCurrencyDto(currencyEntity);
            resp.setStatus(SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), currencyDto);
        } catch (RespException respException) {
            resp.setStatus(respException.getCode());
            objectMapper.writeValue(resp.getWriter(), respException.getMessage());
        }
    }
}
