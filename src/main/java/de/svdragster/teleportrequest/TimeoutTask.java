package de.svdragster.teleportrequest;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.tasks.ServerTask;

class TimeoutTask extends ServerTask {

	private TeleportRequest p;
	private Player sender;
	
	public TimeoutTask(TeleportRequest p, Player sender) {
		super(p, p.config.getTimeout() * 20); // convert seconds to ticks
		this.p = p;
		this.sender = sender;
	}

	public void run() {
		p.removeRequest(sender);
		sender.notice("Teleport request has timed out.");
	}
}
