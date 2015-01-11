package de.svdragster.teleportrequest;

import net.canarymod.Canary;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.plugin.Plugin;

public class TeleportRequest extends Plugin {

	public void disable() {

	}

	public boolean enable() {
		Canary.hooks().registerListener(new RequestListener(), this);
		try {
			Canary.commands().registerCommands(new RequestCommands(), this, false);
		} catch (CommandDependencyException e) {
			getLogman().error(e);
			return false;
		}
		return true;
	}

}
