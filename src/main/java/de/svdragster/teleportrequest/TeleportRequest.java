package de.svdragster.teleportrequest;

import java.util.ArrayList;
import java.util.HashMap;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.chat.ChatFormat;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.logger.Logman;
import net.canarymod.plugin.Plugin;
import net.canarymod.tasks.ServerTaskManager;

public class TeleportRequest extends Plugin {
	
	Logman logger;
	RequestConfiguration config;
	private HashMap<Player, Player> requests = new HashMap<Player, Player>(); // Sender & Receiver of request
	private HashMap<Player, TimeoutTask> timers = new HashMap<Player, TimeoutTask>(); // timeouts for requests
	
	public TeleportRequest() {
		logger = getLogman();
		config = new RequestConfiguration(this);
	}

	public void disable() {
		requests.clear();
		timers.clear();
		ServerTaskManager.removeTasks(this);
	}

	public boolean enable() {
		Canary.hooks().registerListener(new RequestListener(this), this);
		try {
			Canary.commands().registerCommands(new RequestCommands(this), this, false);
		} catch (CommandDependencyException e) {
			logger.error(e);
			return false;
		}
		return true;
	}
	
	public void newRequest(Player sender, Player receiver)  {
		if (sender.equals(receiver)) {
			sender.notice("You cannot request a teleport to yourself!");
		} else
			if (requests.containsKey(sender) && requests.get(sender).equals(receiver)) {
			removeRequest(sender);
			sender.notice("Cancelled request to " + receiver.getName());
		} else {
			requests.put(sender, receiver);
			if(config.isTimeoutEnabled()){
				TimeoutTask t = new TimeoutTask(this, sender);
				timers.put(sender, t);
				ServerTaskManager.addTask(t);
			}
			receiver.message(ChatFormat.GREEN + "You've received a teleportation request from " + ChatFormat.GOLD + sender.getName() + ChatFormat.GREEN + "!");
			if(config.isTimeoutEnabled()){
				receiver.message(ChatFormat.GREEN + "Note this request will timeout in " + ChatFormat.GOLD + config.getTimeout() + ChatFormat.GREEN + " seconds.");
			}
			receiver.message(ChatFormat.YELLOW + "Type /tpaccept to teleport " + sender.getName() + " to you.");
			sender.message(ChatFormat.GREEN + "Teleportation Request sent to " + ChatFormat.GOLD + receiver.getName() + ChatFormat.GREEN + ".");
		}
	}
	
	public void removeRequest(Player player) {
		if (requests.containsKey(player)) {
			requests.remove(player);
		}
		if (timers.containsKey(player)) {
			ServerTaskManager.removeTask(timers.get(player));
			timers.remove(player);
		}
		if (requests.containsValue(player)) {
			for (Player sender : requests.keySet()) {
				Player receiver = requests.get(sender);
				if (receiver.equals(player)) {
					requests.remove(sender);
					if(timers.containsKey(sender)) {
						ServerTaskManager.removeTask(timers.get(sender));
						timers.remove(sender);
					}
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

}
