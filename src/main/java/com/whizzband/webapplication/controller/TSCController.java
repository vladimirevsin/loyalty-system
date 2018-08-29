package com.whizzband.webapplication.controller;

import com.template.dto.Client;
import com.template.dto.TransactionResultData;
import com.template.dto.Wallet;
import com.template.flow.*;
import com.template.state.ClientState;
import com.whizzband.webapplication.model.AddBallsModel;
import com.whizzband.webapplication.model.ResultData;
import com.whizzband.webapplication.setting.NodeRPCConnectionTSC;
import enums.EnumError;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.FlowProgressHandle;
import net.corda.core.transactions.SignedTransaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/")
public class TSCController {
    private final CordaRPCOps proxy;

    @Value("${client.organisation}")
    private String clientsOrganisation;
    @Value("${client.location}")
    private String clientsLocation;
    @Value("${client.country}")
    private String clientsCountry;

    public CordaRPCOps getProxy() {
        return proxy;
    }

    public TSCController(NodeRPCConnectionTSC nodeRPCConnection) {
        this.proxy = nodeRPCConnection.getProxy();
    }

    @PostMapping(value = "/add_token_client", headers = {
            "content-type=text/plain",
            "content-type=text/html",
            "content-type=application/json"
    })
    public ResultData addToken(@RequestBody AddBallsModel model) {

        final CordaX500Name tscCordaX500Name = new CordaX500Name(
                clientsOrganisation, clientsLocation, clientsCountry
        );

        try {
            final Party client = proxy.wellKnownPartyFromX500Name(tscCordaX500Name);
            final FlowProgressHandle<TransactionResultData> flowProgressHandle = proxy.startTrackedFlowDynamic(AddTokenToClient.Initiator.class,
                    client,
                    calculateToken(model.component2()), //Количество баллов
                    model.component3(),
                    model.component1()
            );
            final TransactionResultData signedTransaction = flowProgressHandle.getReturnValue().get();
            ResultData resultData = upgradeBalance(signedTransaction);
            return resultData;
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

    @PostMapping("/dest_token")
    public ResultData destroyToken(@RequestBody AddBallsModel model) {
        final CordaX500Name tscCordaX500Name = new CordaX500Name(
                clientsOrganisation, clientsLocation, clientsCountry
        );

        try {
            final Party client = proxy.wellKnownPartyFromX500Name(tscCordaX500Name);
            final FlowProgressHandle<TransactionResultData> flowProgressHandle = proxy.startTrackedFlowDynamic(DecreaseTokenAClient.Initiator.class,
                    client, model.component2(), model.component3(), model.component1());
            final TransactionResultData signedTransaction = flowProgressHandle.getReturnValue().get();
            ResultData resultData = upgradeBalance(signedTransaction);
            return resultData;
        } catch (Exception ex) {
            return new ResultData(EnumError.INNER_ERROR.getCode(), ex.getMessage());
        }    }

    @GetMapping("/calc_token")
    public Double calculateToken(Double sumPay) {
        try {

            final FlowProgressHandle<Double> flowProgressHandle = proxy.startTrackedFlowDynamic(GetTokenBySumPay.Initiator.class,
                    sumPay);
            final Double addWallet = flowProgressHandle.getReturnValue().get();
            return addWallet;
        } catch (Exception ex) {
            return 0.0;
        }

    }

    @GetMapping("/get_client")
    public List<Client> getClient() {
        final CordaX500Name tscCordaX500Name = new CordaX500Name(
                clientsOrganisation, clientsLocation, clientsCountry
        );
        final Party client = proxy.wellKnownPartyFromX500Name(tscCordaX500Name);
        final FlowProgressHandle<List<? extends Client>> listFlowProgressHandle = proxy.startTrackedFlowDynamic(GetClients.Initiator.class, client);
        List<Client> signedTransaction = new ArrayList<>();
        try {
            signedTransaction = (List<Client>) listFlowProgressHandle.getReturnValue().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return signedTransaction;
    }
}
