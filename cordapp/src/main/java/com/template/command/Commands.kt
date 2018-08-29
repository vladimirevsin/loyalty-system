package com.template.command

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.TypeOnlyCommandData

interface Commands : CommandData {
    class SellToTSC : Commands
    class AddBonusClient : TypeOnlyCommandData(), Commands
    class MoveBetweenClient : TypeOnlyCommandData(), Commands
    class DestroyBonusClient : TypeOnlyCommandData(), Commands
    class UpdateBalance : TypeOnlyCommandData(), Commands
    class PartnerAdd : TypeOnlyCommandData(), Commands
    class PartnerUpdate : TypeOnlyCommandData(), Commands
    class AllianceAdd : TypeOnlyCommandData(), Commands
    class ClientAdd : TypeOnlyCommandData(), Commands
    class WalletAdd : TypeOnlyCommandData(), Commands
}