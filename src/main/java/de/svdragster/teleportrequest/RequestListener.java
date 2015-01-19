package de.svdragster.teleportrequest;

import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.DisconnectionHook;
import net.canarymod.plugin.PluginListener;

public class RequestListener implements PluginListener {
	
	private TeleportRequest tpr;
	
	public RequestListener(TeleportRequest tpr){
		this.tpr = tpr;
	}

	@HookHandler
	public void onLogout(DisconnectionHook hook) {
		tpr.removeRequest(hook.getPlayer());
	}
}
