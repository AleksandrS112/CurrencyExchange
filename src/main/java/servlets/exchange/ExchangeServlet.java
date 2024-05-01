package servlets.exchange;

import dto.ExchangeDTO;
import exception.RespException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeService;
import servlets.BaseServlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

public class ExchangeServlet extends BaseServlet {

    ExchangeService exchangeService = ExchangeService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        BigDecimal amount;
        try {
            amount = new BigDecimal(BigDecimal.valueOf(Double.parseDouble(req.getParameter("amount"))).stripTrailingZeros().toPlainString());
        } catch (NullPointerException e) {
            throw new RespException(400, "Не указано количество валюты");
        } catch (NumberFormatException e) {
            throw new RespException(400, "Не корректно указано количество валюты");
        }
        Optional<ExchangeDTO> exchangeDTO = exchangeService.executeExchange(baseCurrencyCode, targetCurrencyCode, amount);
        if (exchangeDTO.isPresent())
            objectMapper.writeValue(resp.getWriter(), exchangeDTO);
        else
            throw new RespException(400, "Нет возможности произвести обмен");
    }
}
