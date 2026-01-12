package online.ebatel.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import online.ebatel.forge.commands.PlayerListCommand;
import online.ebatel.forge.commands.ServerStatsCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(WebAdminMod.MOD_ID)
public class WebAdminMod {
    public static final String MOD_ID = "webadminplugin";
    public static final Logger LOGGER = LogManager.getLogger();

    public WebAdminMod() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(TpsTracker.class);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("WebAdminPlugin (Forge) enabled!");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        PlayerListCommand.register(event.getDispatcher());
        ServerStatsCommand.register(event.getDispatcher());
        LOGGER.info("WebAdminPlugin commands registered!");
    }
}
