package space.chunks.gamecup.dgr.map.object.setup;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.impl.flight.FlightRadarConfig;
import space.chunks.gamecup.dgr.map.object.impl.flight.monitor.FlightMonitorConfig;
import space.chunks.gamecup.dgr.map.object.impl.marketing.MarketingConfigEntry;
import space.chunks.gamecup.dgr.map.object.impl.procedure.luggageclaim.LuggageClaimConfig;
import space.chunks.gamecup.dgr.map.object.impl.procedure.seats.SeatConfig;
import space.chunks.gamecup.dgr.map.object.impl.procedure.securitycheck.SecurityCheckConfig;
import space.chunks.gamecup.dgr.map.object.impl.procedure.ticketcontrol.TicketControlConfig;

import java.util.List;


/**
 * @author Nico_ND1
 */
public record MapObjectDefaultSetupConfig(
    @NotNull List<SecurityCheckConfig> securityChecks,
    @NotNull List<TicketControlConfig> ticketControls,
    @NotNull MarketingConfigEntry marketing,
    @NotNull List<FlightRadarConfig> flightRadars,
    @NotNull List<FlightMonitorConfig> flightMonitors,
    @NotNull List<LuggageClaimConfig> luggageClaims,
    @NotNull List<SeatConfig> seats
) {
}
