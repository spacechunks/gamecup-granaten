package space.chunks.gamecup.dgr.map.object.setup;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.impl.marketing.MarketingConfigEntry;
import space.chunks.gamecup.dgr.map.procedure.securitycheck.SecurityCheckConfig;
import space.chunks.gamecup.dgr.map.procedure.ticketcontrol.TicketControlConfig;

import java.util.List;


/**
 * @author Nico_ND1
 */
public record MapObjectDefaultSetupConfig(
    @NotNull List<SecurityCheckConfig> securityChecks,
    @NotNull List<TicketControlConfig> ticketControls,
    @NotNull MarketingConfigEntry marketing
) {
}
