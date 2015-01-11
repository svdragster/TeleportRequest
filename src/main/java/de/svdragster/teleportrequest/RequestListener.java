package de.svdragster.teleportrequest;

import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.DisconnectionHook;
import net.canarymod.plugin.PluginListener;

public class RequestListener implements PluginListener {

	@HookHandler
	public void onLogout(DisconnectionHook hook) {
		RequestCommands commands = new RequestCommands();
		commands.removeRequest(hook.getPlayer());
	}
}
