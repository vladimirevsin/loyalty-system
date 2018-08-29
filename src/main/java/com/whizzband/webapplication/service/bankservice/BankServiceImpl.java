package com.whizzband.webapplication.service.bankservice;

import com.whizzband.webapplication.dao.bankdao.BankDaoInterface;
import com.whizzband.webapplication.model.Abonent;
import com.whizzband.webapplication.model.ResultData;
import com.whizzband.webapplication.model.TSC;
import net.corda.core.identity.CordaX500Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankServiceImpl implements BankServiceInt {

    BankDaoInterface bankDaoInterface;

    @Override
    public ResultData RegisterUser(Abonent abonent) {
        return bankDaoInterface.RegisterUser(abonent);
    }

    @Override
    public ResultData SeilTokenToTSC(CordaX500Name tsc, Double countToken, int identCoalition) {
        return bankDaoInterface.SeilTokenToTSC(tsc, countToken, identCoalition);
    }
}
