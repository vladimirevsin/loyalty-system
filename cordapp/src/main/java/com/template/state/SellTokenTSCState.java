package com.template.state;

import com.google.common.collect.ImmutableList;
import com.template.schema.SellTokenTSCSchemaV1;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class SellTokenTSCState implements QueryableState, LinearState {
    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    public SellTokenTSCState(UniqueIdentifier linearId, Party partyBank, Party partyTSC, double countToken, int codeCoalition) {
        this.linearId = linearId;
        this.partyBank = partyBank;
        this.partyTSC = partyTSC;
        this.countToken = countToken;
        this.codeCoalition = codeCoalition;
    }

    private final UniqueIdentifier linearId;
    private final Party partyBank;
    private final Party partyTSC;
    private final double countToken;
    private final int codeCoalition;

    @NotNull
    @Override
    public PersistentState generateMappedObject(MappedSchema schema) {
        if (schema instanceof SellTokenTSCSchemaV1) {
            return new SellTokenTSCSchemaV1.PersistentBankReg(
              this.linearId.getId(),
              this.partyBank,
              this.partyTSC,
              this.countToken,
              this.codeCoalition);
        }
        return null;
    }

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return ImmutableList.of(new SellTokenTSCSchemaV1());
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(partyBank, partyTSC);
    }
}
