package com.whizzband.webapplication.controller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.whizzband.webapplication.model.ResultData;
import com.whizzband.webapplication.service.bankservice.BankServiceInt;
import com.whizzband.webapplication.setting.NodeRPCConnection;
import enums.EnumError;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/")
public class MainController {

//    private final CordaRPCOps proxy;

    @Autowired
    BankServiceInt bankServiceInt;

//    public MainController(NodeRPCConnection nodeRPCConnection) {
//        this.proxy = nodeRPCConnection.getProxy();
//    }

//    @GetMapping("/get_name")
//    public String getMyNames() {
//        return proxy.nodeInfo().getLegalIdentities().get(0).getName().toString();
//    }
//
//    @GetMapping("/get_peers")
//    public Map<String, List<CordaX500Name>> getPeers() {
//        List<NodeInfo> nodeInfoSnapshot = proxy.networkMapSnapshot();
//        List<String> serviceNames = ImmutableList.of("Notary");
//        final CordaX500Name myName = proxy.nodeInfo().getLegalIdentities().get(0).getName();
//
//        return ImmutableMap.of("peers", nodeInfoSnapshot
//                .stream()
//                .map(node -> node.getLegalIdentities().get(0).getName())
//                .filter(name -> !name.equals(myName) && !serviceNames.contains(name.getOrganisation()))
//                .collect(toList()));
//    }
//
//    @GetMapping("/get_balance")
//    public ResultData getBalance(CordaX500Name nameNode, UUID identClient) {
//        try {
//            final Party clientParty = proxy.wellKnownPartyFromX500Name(nameNode);
//
//            return new ResultData(EnumError.OK);
//        } catch (Exception ex) {
//            return new ResultData(EnumError.INNER_ERROR.getCode(), ex.getMessage());
//        }
//    }
}
