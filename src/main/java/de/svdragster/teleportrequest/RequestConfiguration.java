package de.svdragster.teleportrequest;

import net.canarymod.config.Configuration;
import net.canarymod.plugin.Plugin;
import net.visualillusionsent.utils.PropertiesFile;

public class RequestConfiguration {
	private PropertiesFile cfg;
	
	public RequestConfiguration(Plugin p) {
		cfg = Configuration.getPluginConfig(p);
		
		// check/set config with default values and relevant comments
		if(!cfg.containsKey("timeout-enabled")) {
			cfg.setBoolean("timeout-enabled", false, "Enable to timeout pending requests after a delay");
		}
		if(!cfg.containsKey("timeout")) {
			cfg.setInt("timeout", 120, "Teleport request timeout in seconds");
		}
		cfg.save(); // save any keys that were generated
	}


	// reload-safe accessors for configuration file
	public boolean isTimeoutEnabled(){ return cfg.getBoolean("timeout-enabled"); }
	public int getTimeout(){ return cfg.getInt("timeout"); }
}
