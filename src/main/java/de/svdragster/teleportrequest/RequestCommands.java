package de.svdragster.teleportrequest;

import java.util.ArrayList;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.chat.ChatFormat;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandListener;

public class RequestCommands implements CommandListener {
	
	private TeleportRequest tpr;
	
	public RequestCommands(TeleportRequest tpr) {
		this.tpr = tpr;
	}

	@Command(
			aliases = { "tpa", "call" },
			description = "Request to teleport to a player",
			permissions = { "request.request" },
			toolTip = "/tpa <playername> or /call <playername>"
	)
	public void TpaCommand(MessageReceiver caller, String[] parameters) {
		if (parameters.length >= 2) {
			Player sender = Canary.getServer().getPlayer(caller.getName());
			Player receiver = Canary.getServer().getPlayer(parameters[1]);
			if (receiver != null) {
				tpr.newRequest(sender, receiver);
			} else {
				sender.notice(parameters[1] + " is not online.");
			}
		} else {
			caller.notice("Usage: /tpa <playername> or /call <playername>");
		}
	}
	
	@Command(
			aliases = { "tpdeny" },
			description = "Deny a pending teleport request to you",
			permissions = { "request.deny" },
			toolTip = "/tpdeny [playername]"
	)
	public void TpdenyCommand(MessageReceiver caller, String[] parameters) {
		Player player = Canary.getServer().getPlayer(caller.getName());
		ArrayList<Player> allRequests = tpr.getAllRequests(player);
		if (allRequests.size() <= 0) {
			player.notice("No pending requests.");
		} else if (allRequests.size() == 1) {
			tpr.deny(player, null);
		} else {
			if (parameters.length >= 2) {
				Player sender = Canary.getServer().getPlayer(parameters[1]);
				if (sender != null) {
					tpr.deny(player, sender);
				} else {
					player.notice(parameters[1] + " is not online.");
				}
			} else {
				StringBuilder senders = new StringBuilder();
				for (int i=0; i<allRequests.size(); i++) {
					Player sender = allRequests.get(i);
					if (i == allRequests.size() - 2) {
						senders.append("and ");
					} else {
						 senders.append(", ");
					}
					senders.append(sender.getName());
				}
				player.message(ChatFormat.YELLOW + "You have multiple pending requests: " + ChatFormat.GOLD + senders.toString());
				player.notice("Please use /tpdeny playername");
			}
		}
	}
	
	@Command(
			aliases = { "tpaccept" },
			description = "Accept a pending teleport request to you",
			permissions = { "request.accept" },
			toolTip = "/tpaccept [playername]"
	)
	public void TpacceptCommand(MessageReceiver caller, String[] parameters) {
		Player player = Canary.getServer().getPlayer(caller.getName());
		ArrayList<Player> allRequests = tpr.getAllRequests(player);
		if (allRequests.size() <= 0) {
			player.notice("No pending requests.");
		} else if (allRequests.size() == 1) {
			tpr.accept(player, null);
		} else {
			if (parameters.length >= 3) {
				Player sender = Canary.getServer().getPlayer(parameters[2]);
				if (sender != null) {
					tpr.accept(player, sender);
				} else {
					player.notice(parameters[2] + " is not online.");
				}
			} else {
				StringBuilder senders = new StringBuilder();
				for (int i=0; i<allRequests.size(); i++) {
					Player sender = allRequests.get(i);
					if (i == allRequests.size() - 2) {
						senders.append("and ");
					} else {
						 senders.append(", ");
					}
					senders.append(sender.getName());
				}
				player.message(ChatFormat.YELLOW + "You have multiple pending requests: " + ChatFormat.GOLD + senders.toString());
				player.notice("Please use /tpaccept playername");
			}
		}
	}
}
