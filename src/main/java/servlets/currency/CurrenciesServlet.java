package servlets.currency;

import dto.CurrencyDto;
import exception.RespException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.CurrencyEntity;
import servlets.BaseServlet;
import util.Validator;

import java.io.IOException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

public class CurrenciesServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            List<CurrencyDto> CurrenciesDto = currencyDao.findAll().stream()
                    .map(currencyEntity -> currencyService.buildCurrencyDto(currencyEntity))
                    .toList();
            objectMapper.writeValue(response.getWriter(), CurrenciesDto);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        String fullName = req.getParameter("full_name");
        String sign = req.getParameter("sign");
        Validator.checkCurrencyParam(code, fullName, sign);
        CurrencyEntity currencyEntity = currencyDao.save(new CurrencyEntity(code, fullName, sign));
        CurrencyDto currencyDto = currencyService.buildCurrencyDto(currencyEntity);
        objectMapper.writeValue(resp.getWriter(), currencyDto);
        resp.setStatus(SC_CREATED);
    }
}
