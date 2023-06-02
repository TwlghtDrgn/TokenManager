package me.realized.tokenmanager.hook.hooks;

import com.djrapitops.plan.capability.CapabilityService;
import com.djrapitops.plan.extension.CallEvents;
import com.djrapitops.plan.extension.Caller;
import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.ExtensionService;
import com.djrapitops.plan.extension.annotation.NumberProvider;
import com.djrapitops.plan.extension.annotation.PluginInfo;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import me.realized.tokenmanager.TokenManagerPlugin;
import me.realized.tokenmanager.util.hook.PluginHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;

public class PlanHook extends PluginHook<TokenManagerPlugin> {
    public PlanHook(TokenManagerPlugin plugin) {
        super(plugin, "Plan");
        if (Bukkit.getPluginManager().isPluginEnabled("Plan")) {
            new Plan().hookIntoPlan();
        }
    }

    public class Plan implements Listener {
        private Optional<Caller> caller;

        public void hookIntoPlan() {
            if (!areAllCapabilitiesAvailable()) return;
            registerDataExtension();
            listenForPlanReloads();
            Bukkit.getServer().getPluginManager().registerEvents(this,plugin);
        }

        private boolean areAllCapabilitiesAvailable() {
            CapabilityService capabilities = CapabilityService.getInstance();
            return capabilities.hasCapability("DATA_EXTENSION_VALUES");
        }

        private void registerDataExtension() {
            try {
                caller = ExtensionService.getInstance().register(new Extension());
            } catch (IllegalStateException planIsNotEnabled) {
                // Plan is not enabled, handle exception
                plugin.getLogger().warning("Error: " + planIsNotEnabled);
            } catch (IllegalArgumentException dataExtensionImplementationIsInvalid) {
                // The DataExtension implementation has an implementation error, handle exception
                plugin.getLogger().warning("Error: " + dataExtensionImplementationIsInvalid);
            }
        }

        private void listenForPlanReloads() {
            CapabilityService.getInstance().registerEnableListener(
                    isPlanEnabled -> {
                        // Register DataExtension again
                        if (isPlanEnabled) registerDataExtension();
                    }
            );
        }

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent e) {
            Player p = e.getPlayer();
            Bukkit.getScheduler().runTaskLater(plugin, () ->
                    caller.ifPresent(value -> value.updatePlayerData(p.getUniqueId(), p.getName())), 40L);
        }
    }

    @PluginInfo(
            name = "TokenManager",
            iconName = "money-bill",
            iconFamily = Family.SOLID,
            color = Color.GREEN)
    public class Extension implements DataExtension {
        @Override
        public CallEvents[] callExtensionMethodsOn() {
            return new CallEvents[] {
                    CallEvents.MANUAL
            };
        }

        @NumberProvider(
                text = "Tokens",
                description = "Tokens that player have",
                iconName = "money-bill",
                iconColor = Color.GREEN,
                priority = 100,
                showInPlayerTable = true)
        public long tokens(UUID uuid) {
            Player player = Bukkit.getPlayer(uuid);
            try {
                OptionalLong tokens = plugin.getTokens(player);

                long bal;
                if (!tokens.isPresent()) bal = 0;
                else bal = tokens.getAsLong();

                return bal;
            } catch (Exception e) {
                plugin.getLogger().severe("Error: " + e);
                return 0;
            }
        }
    }
}
