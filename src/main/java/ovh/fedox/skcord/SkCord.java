package ovh.fedox.skcord;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import com.jeff_media.updatechecker.UserAgentBuilder;
import lombok.Getter;
import ovh.fedox.skcord.command.SkCordCommand;
import ovh.fedox.skcord.listener.JoinListener;
import ovh.fedox.skcord.util.Checker;
import ovh.fedox.skcord.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkCord extends JavaPlugin {

	@Getter
	private static SkCord plugin;
	@Getter
	private final Logger customLogger = new Logger();
	@Getter
	private SkriptAddon addon;

	@Override
	public void onEnable() {
		plugin = this;
		this.addon = Skript.registerAddon(this);
		try {
			addon.loadClasses("ovh.fedox.skcord", "elements");
		} catch (Exception e) {
			customLogger.error("Failed to load classes: " + e.getMessage());
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		new UpdateChecker(this, UpdateCheckSource.SPIGOT, "106832")
				.checkEveryXHours(24)
				.setNotifyOpsOnJoin(true)
				.setDownloadLink("https://github.com/Fedox-die-Ente/skCord-v3/releases")
				.setDonationLink("https://www.paypal.com/paypalme/feeedox")
				.setUserAgent(new UserAgentBuilder().addPluginNameAndVersion())
				.checkNow();

		customLogger.log("SkCord has been enabled!");



		this.registerCommands();
		this.registerListeners();
		this.loadMetrics();

		new Checker(plugin, 106832).getVersion(version -> {
			if (!plugin.getDescription().getVersion().equals(version)) {
				customLogger.warn("New version available: " + version);
			} else {
				customLogger.log("You are using the latest version of SkCord.");
			}
		});
	}

	@Override
	public void onDisable() {
		customLogger.log("SkCord has been disabled!");
	}


	private void registerCommands() {
		getCommand("skcord").setExecutor(new SkCordCommand());
	}

	private void registerListeners() {
		Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
	}

	private void loadMetrics() {
		Metrics metrics = new Metrics(this, 20590);
		metrics.addCustomChart(new Metrics.SimplePie("skript_version", () -> Skript.getVersion().toString()));
	}
}
