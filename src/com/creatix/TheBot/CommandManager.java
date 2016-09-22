package com.creatix.TheBot;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import com.creatix.TheBot.chat.BMessageManager;
import com.creatix.TheBot.objects.Command;
import com.creatix.TheBot.objects.Subject;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.User;

public class CommandManager {
	
	public static void registerCommand(Command command) {
		if(command.name.length() <= 0)
			return;
		if(!SystemCore.commands.contains(command)){
			SystemCore.commands.add(command);
			System.out.println("Command '"+command.name+"' was success added.");
		}
	}
	public static void init() {
		
		registerCommand(new Command("monitor","mon" ,"Monitor subject by id or name", true, (msg, args, guild) -> {
			if(args.length >= 1){
				if(args[0].startsWith("*")){
					//MonitorOptions
					String option = args[0].substring(1);
					System.out.println("options"+option);
					switch(option){
						case ("channel"): case ("chan"):{
							List<User> u = guild.getVoiceStatusOfUser(msg.getAuthor()).getChannel().getUsers();
							System.out.println("chan");
							String ret = "";
							for(User us : u){
								Subject subj = new Subject(us);
								ret += "```"+subj.Monitor(guild)+"```";
							}
							BMessageManager.sendMessage(msg.getChannel(), ret);
							break;
						}
						case ("server"): case ("serv"):{
							break;
						}
						case ("registered"): case ("reg"):{
							break;
						}
						default:{
						}
					}
				}
				else{
					List<User> list = new ArrayList<User>();
					list.addAll(guild.getUsersByName(args[0]));
					list.add(guild.getUserById(args[0]));
					if(list.isEmpty()){
						BMessageManager.reply(msg, "User not fount with ID/Name '"+args[0]+"'.");
						return;
					}else{
						Subject subj = new Subject(list.get(0));
						BMessageManager.sendMessage(msg.getChannel(), "```"+subj.Monitor(guild)+"```");
					}
				}
			}else{return;}
		}));
		registerCommand(new Command("classification","class" ,"Show all users with [args[0]] classification", true, (msg, args, guild) -> {
			if(args.length >= 1){
				if(UserManager.getClassByName(args[0]) == null)
					return;
				List<String> users = new ArrayList<>();
				for(String id : UserManager.Users){
					if(UserManager._Humans.get(id).equals(UserManager.getClassByName(args[0])))
						users.add(id);
				}
				if(users.isEmpty()){
					BMessageManager.reply(msg, "Not fount users.");
					return;
				}
				String ret = "Searching...\n";
				ret += "Search arguments: Classification["+args[0]+"]\n";
				for(String id : users){
					User _s = guild.getUserById(id);
					ret += "  - "+_s.getUsername()+" : "+_s.getOnlineStatus().name()+"\n";
				}
				BMessageManager.sendMessage(msg.getChannel(), "```"+ret+"```");
			}else{return;}
		}));
		registerCommand(new Command("commands","help" ,"Show all commands", false, (msg, args, guild) -> {
			if(args.length >= 1){
				if(getCommandByName(args[0]) == null){
					commandList(msg);
					return;
				}else{
					Command c = getCommandByName(args[0]);
					String ret = "Searching information about command '"+args[0]+"'...\n";
					ret += "Execute Name: "+c.name+"\n";
					ret += "Short Execute Name: "+c.auxname+"\n";
					ret += "AccessLevel:  "+(c.admin ? 0.5F : 0.0F) + "["+(c.admin ? "ADMIN" : "USERS") +"]\n";
					ret += "Description: \n"+"  "+c.desc+"\n";
					BMessageManager.sendMessage(msg.getChannel(), "```"+ret+"```");
				}
			}else{
				commandList(msg);
			}
		}));
		registerCommand(new Command("status","Show bot status", true, (msg, args, guild) -> {
			int mb = 1024*1024;
			Runtime runtime = Runtime.getRuntime();
			long used = (runtime.totalMemory() - runtime.freeMemory()) / mb;
			long memorySize = ((com.sun.management.OperatingSystemMXBean) ManagementFactory
			        .getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
			BMessageManager.reply(msg, "I\'am  alive\nI\'am using "+used+"mb RAM of "+memorySize / mb+"mb!\nI\'am working with "+Thread.currentThread().getId()+" thread");
		}));
	}
	private static void commandList(Message msg){
		String ret = "";
		if(SystemCore.commands.isEmpty()){
			ret += "No commands registered.";
		}else{
			ret += "Commands: \n";
			for(Command cmd : SystemCore.commands){
				ret += " - "+cmd.name+"\n";
			}
			BMessageManager.sendMessage(msg.getChannel(), "```"+ret+"```");
		}
	}
	
	public static Command getCommandByName(String name)
	{
		for(Command c : SystemCore.commands)
		{
			if(name.toLowerCase().equals(c.name) || name.toLowerCase().equals(c.auxname))
				return c;
		}
		return null;
	}
}