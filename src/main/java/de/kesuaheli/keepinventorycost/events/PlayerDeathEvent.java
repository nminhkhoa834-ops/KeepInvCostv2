package de.kesuaheli.keepinventorycost.events;

import de.kesuaheli.keepinventorycost.KeepInventoryCost;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerDeathEvent implements Listener {

    private final KeepInventoryCost plugin;

    public PlayerDeathEvent(KeepInventoryCost plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(org.bukkit.event.entity.PlayerDeathEvent event) {
        if (event.getKeepInventory() ||
                !this.plugin.pConf.getConfig().getBoolean(event.getPlayer().getUniqueId() + ".enabled", false)) {
            return;
        }

        int cost = this.plugin.getConfig().getInt("cost", 0);
        String currencyName = cost == 1 ? this.plugin.eco.currencyNameSingular() : this.plugin.eco.currencyNamePlural();
        String costString = cost + " " + currencyName;
        if (cost >= 0 &&
            (this.plugin.eco.getBalance(event.getPlayer()) < cost ||
                !this.plugin.eco.withdrawPlayer(event.getPlayer(), cost).transactionSuccess())) {
            String msg = this.plugin.getConfigString("message.death.no_money");
            String formatted = String.format(msg, this.plugin.eco.currencyNamePlural(), costString);
            this.plugin.sendMessage(event.getPlayer(), formatted);
            return;
        }

        event.setKeepInventory(true);
        event.getDrops().clear();
        event.setKeepLevel(true);
        event.setDroppedExp(0);

        String msg = this.plugin.getConfigString("message.death.paid");
        String formatted = String.format(msg, costString);
        this.plugin.sendMessage(event.getPlayer(), formatted);
    }
}
