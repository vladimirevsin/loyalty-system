package com.whizzband.webapplication.service.bankservice;

import com.whizzband.webapplication.dao.bankdao.BankDaoInterface;
import com.whizzband.webapplication.model.Abonent;
import com.whizzband.webapplication.model.ResultData;
import com.whizzband.webapplication.model.TSC;
import net.corda.core.identity.CordaX500Name;

public interface BankServiceInt {

    /**
     * Регистрация клиента
     * @param abonent Абонент
     * @return Результат выполнения операции
     */
    ResultData RegisterUser(Abonent abonent);

    /**
     * Продажа токенов ТСП
     * @param tsc ТСП
     * @param countToken Количество токенов
     * @param identCoalition Коалиция
     * @return Результат выполнения операции
     */
    ResultData SeilTokenToTSC(CordaX500Name tsc, Double countToken, int identCoalition);

}
