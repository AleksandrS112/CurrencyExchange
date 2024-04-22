
import dao.CurrencyDao;
import dao.ExchangeRatesDao;
import dao.PSQLState;
import exception.RespException;
import model.CurrencyEntity;
import model.ExchangeRatesEntity;

import java.math.BigDecimal;

public class DaoTest {
    public static void main(String[] args)  {

        try {

        CurrencyDao cd = CurrencyDao.getInstance();
            cd.findByCode("USD");
        BigDecimal bd = BigDecimal.valueOf(Double.parseDouble("100"));
        ExchangeRatesEntity exchangeRatesEntity = new ExchangeRatesEntity(41, cd.findByCode("TNG").get(), cd.findByCode("USD").get(), bd);
        ExchangeRatesDao.getInstance().save(exchangeRatesEntity);

        } catch (RespException e) {

            System.out.println(e.getCode());
            System.out.println("_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_");
            System.out.println("getMessage   " + e.getMessage());
            System.out.println("fillInStackTrace   " + e.fillInStackTrace());
            System.out.println("getLocalizedMessage   " + e.getLocalizedMessage());
            System.out.println("getCause   " + e.getCause());
            System.out.println("toString   " );
            System.out.println("getStackTrace   " + e.getMessage());
            System.out.println("getNextException   " + e.getMessage());
            System.out.println("getSQLState   " + e.getLocalizedMessage());
            System.out.println("getErrorCode   " + e.getCause());
            System.out.println("getLocalizedMessage   " + e.getLocalizedMessage());

        }
        /*
        var all = ExchangeRatesDao.getInstance().findAll();

        var exchangeRatesEntity = all.get(0);

        CurrencyEntity c1 = exchangeRatesEntity.getBaseCurrency();
        CurrencyEntity c2 = CurrencyDao.getInstance().findById(7).get();
        BigDecimal bd = new BigDecimal("10");

        ExchangeRatesEntity ere = new ExchangeRatesEntity(0 , c1, c2, bd);

        ExchangeRatesDao.getInstance().save(ere);
        */

/*
        var all = CurrencyDao.getInstance().findAll();
        for(CurrencyEntity currencyEntity : all) {
            System.out.println(currencyEntity.toString());
        }
        */
/*
        CurrencyFilter cf = new CurrencyFilter(1,0, "RUB", "ru", "P");
        CurrencyDao cd = CurrencyDao.getInstance();
        System.out.println(cd.findAll(cf));
 */

    }
}
