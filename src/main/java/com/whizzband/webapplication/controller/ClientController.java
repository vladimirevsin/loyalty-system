package com.whizzband.webapplication.controller;

import com.template.dto.TransactionResultData;
import com.template.dto.Wallet;
import com.template.flow.*;
import com.whizzband.webapplication.model.Abonent;
import com.whizzband.webapplication.model.AddBallsModel;
import com.whizzband.webapplication.model.ResultData;
import com.whizzband.webapplication.model.ResultDataSearchClient;
import com.whizzband.webapplication.setting.NodeRPCConnection;
import com.whizzband.webapplication.setting.NodeRPCConnectionClient;
import enums.EnumError;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.FlowProgressHandle;
import net.corda.core.transactions.SignedTransaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/")
public class ClientController {
    private final CordaRPCOps proxy;

    public CordaRPCOps getProxy() {
        return proxy;
    }

    public ClientController(NodeRPCConnectionClient nodeRPCConnection) {
        this.proxy = nodeRPCConnection.getProxy();
    }

    @PostMapping(value = "/reg_client", headers = {
            "content-type=text/plain",
            "content-type=text/html",
            "content-type=application/json"
    })
    public ResultData registrationClient(@RequestBody Abonent abonent) {
        try {
//            Abonent abonent = new Abonent(
//                    "Иванов Иван Сергеевич",
//                    "+7 988 457 88 95",
//                    "123"
//            );

            final FlowProgressHandle<SignedTransaction> flowProgressHandle = proxy.startTrackedFlowDynamic(AddClient.Initiator.class,
                    abonent.component2(), abonent.component1(), abonent.component3());
            final SignedTransaction addClient = flowProgressHandle.getReturnValue().get();

            return new ResultData(0, "Ok");
        } catch (Exception ex) {
            return new ResultData(-1, ex.getMessage());
        }
    }

    @GetMapping("/reg_wallet")
    public ResultData addWallet(String phoneNumber, int marker) {
        try {

            UUID walletId = getIdentClient(phoneNumber);
            final FlowProgressHandle<TransactionResultData> flowProgressHandle = proxy.startTrackedFlowDynamic(AddWallet.Initiator.class,
                    walletId, marker);
            final TransactionResultData addWallet = flowProgressHandle.getReturnValue().get();

            ResultData result = upgradeBalance(addWallet);
            return new ResultData(0, "Ok");
        } catch (Exception ex) {
            return new ResultData(-1, ex.getMessage());
        }
    }

    @GetMapping("/get_balance05")
    public Double getBalance05(String phoneNumber, int marker) {
        try {

            final FlowProgressHandle<Double> flowProgressHandle = proxy.startTrackedFlowDynamic(GetBalance.Initiator.class,
                    phoneNumber, marker);
            final Double addWallet = flowProgressHandle.getReturnValue().get();

            return addWallet;
        } catch (Exception ex) {
            return null;
        }
    }

    @GetMapping("/get_balance_client")
    public List<Wallet> getBalance(String phoneNumber) {
        try {
            if (phoneNumber.charAt(0) == ' ') phoneNumber = "+".concat(phoneNumber.substring(1));
            final FlowProgressHandle<TransactionResultData> transactionResultDataFlowProgressHandle = proxy.startTrackedFlowDynamic(GetBalanceClient.Initiator.class,
                    phoneNumber);
            final TransactionResultData addWallet = transactionResultDataFlowProgressHandle.getReturnValue().get();

            return addWallet.getWallets();
        } catch (Exception ex) {
            return null;
        }
    }


    @GetMapping("/search_client")
    public ResultDataSearchClient searchClient(String phoneNumber, String password) {
        try {
            final FlowProgressHandle<Boolean> flowProgressHandle = proxy.startTrackedFlowDynamic(SearchCLient.Initiator.class,
                    phoneNumber, password);
            final Boolean seachClient = flowProgressHandle.getReturnValue().get();
            return new ResultDataSearchClient(0, "ok", seachClient);
        } catch (Exception ex) {
            return new ResultDataSearchClient(-1, ex.getMessage(), false);
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

    public UUID getIdentClient(String phoneNumber) throws ExecutionException, InterruptedException {

        final FlowProgressHandle<UUID> flowProgressHandle = proxy.startTrackedFlowDynamic(GetWalletByPhone.Initiator.class,
                phoneNumber);
        final UUID signedTransaction = flowProgressHandle.getReturnValue().get();
        return signedTransaction;
    }

}
