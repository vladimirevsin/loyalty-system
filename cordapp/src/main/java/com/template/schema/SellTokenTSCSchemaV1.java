package com.template.schema;

import com.google.common.collect.ImmutableList;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

public class SellTokenTSCSchemaV1 extends MappedSchema {
    public SellTokenTSCSchemaV1() {
        super(SellTokenTSCSchema.class, 1, ImmutableList.of(PersistentBankReg.class));
    }

    @Entity
    @Table(name = "sell_token_tsc")
    public static class PersistentBankReg extends PersistentState {
        @Column(name = "linear_id")
        private final UUID linearID;

        @Column(name = "party_bank")
        private final Party partyBank;

        @Column(name = "party_tsc")
        private final Party partyTSC;

        @Column(name = "count_token")
        private final double countToken;

        @Column(name = "code_coalition")
        private final int codeCoalition;

        public PersistentBankReg(UUID linearID, Party partyBank, Party partyTSC, double countToken, int codeCoalition) {
            this.linearID = linearID;
            this.partyBank = partyBank;
            this.partyTSC = partyTSC;
            this.countToken = countToken;
            this.codeCoalition = codeCoalition;
        }
    }
}
