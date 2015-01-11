package de.svdragster.teleportrequest;

import java.util.ArrayList;
import java.util.HashMap;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.chat.ChatFormat;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandListener;

public class RequestCommands implements CommandListener {

	private static HashMap<Player, Player> requests = new HashMap<Player, Player>(); // Sender & Receiver of request
	
	public void newRequest(Player sender, Player receiver)  {
		if (sender.equals(receiver)) {
			sender.notice("How about no");
		} else if (requests.containsKey(sender) && requests.get(sender).equals(receiver)) {
			removeRequest(sender);
			sender.notice("Cancelled request to " + receiver.getName());
		} else {
			requests.put(sender, receiver);
			receiver.message(ChatFormat.GREEN + "You've received a teleportation request from " + ChatFormat.GOLD + sender.getName() + ChatFormat.GREEN + "!");
			receiver.message(ChatFormat.YELLOW + "Type /tpaccept to teleport " + sender.getName() + " to you.");
			sender.message(ChatFormat.GREEN + "Teleportation Request sent to " + ChatFormat.GOLD + receiver.getName() + ChatFormat.GREEN + ".");
		}
	}
	
	public void removeRequest(Player player) {
		if (requests.containsKey(player)) {
			requests.remove(player);
		}
		if (requests.containsValue(player)) {
			for (Player sender : requests.keySet()) {
				Player receiver = requests.get(sender);
				if (receiver.equals(player)) {
					requests.remove(sender);
					sender.notice("Cancelled request to " + receiver.getName());
				}
			}
		}
	}
	
	public void accept(Player receiver, Player sender) {
		if (sender == null) {
			for (Player tempSender : requests.keySet()) {
				if (requests.get(tempSender).equals(receiver)) {
					tempSender.teleportTo(receiver.getLocation());
					tempSender.message(ChatFormat.GREEN + receiver.getName() + " has accepted your teleportation request.");
					receiver.message(ChatFormat.GREEN + tempSender.getName() + " has been teleported to you.");
					removeRequest(tempSender);
					return;
				}
			}
			receiver.notice("No requests.");
		} else {
			if (requests.containsKey(sender)) {
				if (requests.get(sender).equals(receiver)) {
					sender.teleportTo(receiver.getLocation());
					sender.message(ChatFormat.GREEN + receiver.getName() + " has accepted your teleportation request.");
					receiver.message(ChatFormat.GREEN + sender.getName() + " has been teleported to you.");
					removeRequest(sender);
				} else {
					receiver.notice("No request by " + sender.getName());
				}
			} else {
				receiver.notice("No request by " + sender.getName());
			}
		}
	}
	
	public void deny(Player receiver, Player sender) {
		if (sender == null) {
			for (Player tempSender : requests.keySet()) {
				if (requests.get(tempSender).equals(receiver)) {
					tempSender.notice(receiver.getName() + " has denied your teleportation request.");
					receiver.notice("Denied request of " + tempSender.getName() + ".");
					removeRequest(tempSender);
					return;
				}
			}
			receiver.notice("No requests.");
		} else {
			if (requests.containsKey(sender)) {
				if (requests.get(sender).equals(receiver)) {
					sender.notice(receiver.getName() + " has denied your teleportation request.");
					receiver.notice("Denied request of " + sender.getName() + ".");
					removeRequest(sender);
				} else {
					receiver.notice("No request by " + sender.getName());
				}
			} else {
				receiver.notice("No request by " + sender.getName());
			}
		}
	}
	
	public ArrayList<Player> getAllRequests(Player receiver) {
		ArrayList<Player> senders = new ArrayList<Player>();
		for (Player sender : requests.keySet()) {
			if (requests.get(sender).equals(receiver)) {
				senders.add(sender);
			}
		}
		return senders;
	}
	
	@Command(aliases = { "tpa" }, description = "Request to teleport to a player", permissions = { "request.request" }, toolTip = "/tpa <playername>")
	public void TpaCommand(MessageReceiver caller, String[] parameters) {
		if (parameters.length >= 2) {
			Player sender = Canary.getServer().getPlayer(caller.getName());
			Player receiver = Canary.getServer().getPlayer(parameters[1]);
			if (receiver != null) {
				newRequest(sender, receiver);
			} else {
				sender.notice(parameters[1] + " is not online.");
			}
		} else {
			caller.notice("Usage: /tpa <playername>");
		}
	}
	
	@Command(aliases = { "tpdeny" }, description = "Deny a pending teleport request to you", permissions = { "request.deny" }, toolTip = "/tpdeny [playername]")
	public void TpdenyCommand(MessageReceiver caller, String[] parameters) {
		Player player = Canary.getServer().getPlayer(caller.getName());
		ArrayList<Player> allRequests = getAllRequests(player);
		if (allRequests.size() < 0) {
			player.notice("No pending requests.");
		} else if (allRequests.size() == 1) {
			deny(player, null);
		} else {
			if (parameters.length >= 2) {
				Player sender = Canary.getServer().getPlayer(parameters[1]);
				if (sender != null) {
					deny(player, sender);
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
	
	@Command(aliases = { "tpaccept" }, description = "Accept a pending teleport request to you", permissions = { "request.accept" }, toolTip = "/tpaccept [playername]")
	public void TpacceptCommand(MessageReceiver caller, String[] parameters) {
		Player player = Canary.getServer().getPlayer(caller.getName());
		ArrayList<Player> allRequests = getAllRequests(player);
		if (allRequests.size() < 0) {
			player.notice("No pending requests.");
		} else if (allRequests.size() == 1) {
			accept(player, null);
		} else {
			if (parameters.length >= 3) {
				Player sender = Canary.getServer().getPlayer(parameters[2]);
				if (sender != null) {
					accept(player, sender);
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
