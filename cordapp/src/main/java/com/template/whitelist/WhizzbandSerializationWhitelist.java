package com.template.whitelist;

import com.template.dto.Client;
import com.template.dto.TransactionResultData;
import com.template.dto.Wallet;
import net.corda.core.serialization.SerializationWhitelist;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WhizzbandSerializationWhitelist implements SerializationWhitelist {

    @NotNull
    @Override
    public List<Class<?>> getWhitelist() {
        return Arrays.asList(
                List.class,
                Wallet.class,
                TransactionResultData.class,
                Date.class,
                UUID.class,
                Client.class
        );
    }
}
