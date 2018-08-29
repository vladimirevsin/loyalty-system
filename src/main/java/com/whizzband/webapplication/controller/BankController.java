package com.whizzband.webapplication.controller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.template.dto.TransactionResultData;
import com.template.dto.Wallet;
import com.template.flow.AddAlliance;
import com.template.flow.AddPartner;
import com.template.flow.SaleTokenToTSC;
import com.template.flow.UpdateBalance;
import com.whizzband.webapplication.model.*;
import com.whizzband.webapplication.service.bankservice.BankServiceInt;
import com.whizzband.webapplication.setting.NodeRPCConnection;
import enums.EnumError;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.FlowProgressHandle;
import net.corda.core.transactions.SignedTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bank")
public class BankController {

    private CordaRPCOps proxy;


    @Bean public CordaRPCOps getProxy() {
        return proxy;
    }

    @Autowired
    BankServiceInt bankServiceInt;

    public BankController(NodeRPCConnection nodeRPCConnection) {
        this.proxy = nodeRPCConnection.getProxy();
    }

    @PostMapping(path = "/reg_abon", consumes = "application/json", produces = "application/json")
    public ResultData RegisterClient(@RequestBody Abonent abonent) {
        try {

            return new ResultData(EnumError.OK);
        } catch (Exception ex) {
            return new ResultData(EnumError.INNER_ERROR.getCode(), ex.getMessage());
        }
    }


    @GetMapping("/sale_token")
    public ResultData saleTokenTSC(/*SeilTokenModel seilTokenModel*/) {
        SaleTokenModel saleTokenModel = new SaleTokenModel(
                new CordaX500Name("Pyaterochka", "Moskow", "GB"),
                12.0,
                1
        );
        try {
            final Party tscParty = proxy.wellKnownPartyFromX500Name(saleTokenModel.getTsc());
            final FlowProgressHandle<TransactionResultData> transactionResultDataFlowProgressHandle = proxy.startTrackedFlowDynamic(SaleTokenToTSC.Initiator.class,
                    tscParty, saleTokenModel.getCountToken(), saleTokenModel.getIdentCoalition());
            final TransactionResultData signedTransaction = transactionResultDataFlowProgressHandle.getReturnValue().get();
            ResultData resultData = upgradeBalance(signedTransaction);
            return resultData;
        } catch (Exception ex) {
            return new ResultData(EnumError.INNER_ERROR.getCode(), ex.getMessage());
        }
    }


    @GetMapping("/reg_partner")
    public ResultData regPartner() {

        TSC tsc = new TSC(
            UUID.randomUUID(),
            new CordaX500Name("Pyaterochka", "Moskow", "GB"),
            "Пятерочка",
            "7854564879",
            "-",
            1,
            UUID.randomUUID(),
            1.6, 1.2, 1000.0
        );
        final Party tscParty = proxy.wellKnownPartyFromX500Name(tsc.getPartnerParty());

        try {
            final FlowProgressHandle<SignedTransaction> signedTransactionFlowProgressHandle = proxy.startTrackedFlowDynamic(AddPartner.Initiator.class,
                    tsc.component1(), tscParty, tsc.component3(), tsc.component4(), tsc.component5(), tsc.component6(), tsc.component7(),
                    tsc.component8(), tsc.component9(), tsc.component10());
            final SignedTransaction signedTransaction = signedTransactionFlowProgressHandle.getReturnValue().get();
            return new ResultData(EnumError.OK);
        } catch (Exception ex) {
            return new ResultData(EnumError.INNER_ERROR.getCode(), ex.getMessage());
        }
    }

    @GetMapping("/reg_alliance")
    public ResultData regAlliance() {

        RegAlliance regAlliance = new RegAlliance(
                UUID.randomUUID(), "Альянс1", 0.5, 1.0, 1000.0
        );

        try {
            final FlowProgressHandle<SignedTransaction> signedTransactionFlowProgressHandle = proxy.startTrackedFlowDynamic(AddAlliance.Initiator.class,
                    regAlliance.getIdAlliance(), regAlliance.getAllianceName(), regAlliance.getExchangeRate(),
                    regAlliance.getPercentageAmount(), regAlliance.getMinAmount());
            final SignedTransaction signedTransaction = signedTransactionFlowProgressHandle.getReturnValue().get();
            return new ResultData(EnumError.OK);
        } catch (Exception ex) {
            return new ResultData(EnumError.INNER_ERROR.getCode(), ex.getMessage());
        }
    }


    public ResultData upgradeBalance(TransactionResultData transactionResultData) {
        try {
            if (transactionResultData.getCode() != 0) return new ResultData(transactionResultData.getCode(), transactionResultData.getMessage());

            for (Wallet wallet: transactionResultData.getWallets()) {
                final FlowProgressHandle<SignedTransaction> transactionResultDataFlowProgressHandle = proxy.startTrackedFlowDynamic(UpdateBalance.Initiator.class,
                        wallet.getWalletId(), wallet.getValue(), wallet.getMarker());
                final SignedTransaction signedTransaction = transactionResultDataFlowProgressHandle.getReturnValue().get();
            }

            return new ResultData(0, "Ok");
        } catch (Exception ex) {
            return new ResultData(-1, ex.getMessage());
        }
    }
}
