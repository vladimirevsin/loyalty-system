package com.whizzband.webapplication.dao.bankdao;

import com.template.flow.SaleTokenToTSC;
import com.whizzband.webapplication.model.Abonent;
import com.whizzband.webapplication.model.ResultData;
import com.whizzband.webapplication.model.TSC;
import enums.EnumError;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.FlowProgressHandle;
import net.corda.core.transactions.SignedTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BankDaoImpl implements BankDaoInterface {

    @Autowired
    public void setProxy(CordaRPCOps proxy) {
        this.proxy = proxy;
    }

    private CordaRPCOps proxy;


    @Override
    public ResultData RegisterUser(Abonent abonent) {
        return null;
    }

    @Override
    public ResultData SeilTokenToTSC(CordaX500Name tsc, Double countToken, int identCoalition) {
        try {
            final Party tscParty = proxy.wellKnownPartyFromX500Name(tsc);
//            final FlowProgressHandle<SignedTransaction> flowProgressHandle = proxy.startTrackedFlowDynamic(SaleTokenToTSC.Initiator.class,
//                    tscParty, countToken, identCoalition);
//            final SignedTransaction signedTransaction = flowProgressHandle.getReturnValue().get();
            return new ResultData(EnumError.OK);
        } catch (Exception ex) {
            return new ResultData(EnumError.INNER_ERROR.getCode(), ex.getMessage());
        }
    }

}
