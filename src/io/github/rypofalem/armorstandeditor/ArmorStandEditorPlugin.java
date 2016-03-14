package io.github.rypofalem.armorstandeditor;

import io.github.rypofalem.armorstandeditor.protection.GPProtection;
//import io.github.rypofalem.armorstandeditor.protection.PlotSProtection;
import io.github.rypofalem.armorstandeditor.protection.Protection;
import io.github.rypofalem.armorstandeditor.protection.WGProtection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;

import me.ryanhamshire.GriefPrevention.GriefPrevention;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

//import com.intellectualcrafters.plot.PlotSquared;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class ArmorStandEditorPlugin extends JavaPlugin{
	CommandEx execute;
	public PlayerEditorManager editorManager;
	public Material editTool;
	boolean debug = true; //weather or not to broadcast messages via print(String message)
	double coarseRot;
	double fineRot;
	private ArrayList<Protection> protections;

	public void onEnable(){
		saveDefaultConfig();
		coarseRot = getConfig().getDouble("coarse");
		fineRot = getConfig().getDouble("fine");
		String tool = getConfig().getString("tool");
		editTool = Material.getMaterial(tool);
		editorManager = new PlayerEditorManager(this);
		execute = new CommandEx(this);
		getCommand("ase").setExecutor(execute);
		getServer().getPluginManager().registerEvents(editorManager, this);
		protections = new ArrayList<Protection>();
		if(isPluginEnabled("WorldGuard")){
			WorldGuardPlugin wgPlugin = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");
			addProtection(new WGProtection(wgPlugin));
		}
		
		if(isPluginEnabled("GriefPrevention")){
			GriefPrevention gpPlugin = (GriefPrevention) getServer().getPluginManager().getPlugin("GriefPrevention");
			addProtection(new GPProtection(gpPlugin));
		}
		
//		if(isPluginEnabled("PlotSquared")){
//			PlotSquared plotSPlugin = (PlotSquared) getServer().getPluginManager().getPlugin("PlotSquared");
//			addProtection(new PlotSProtection(plotSPlugin));
//		}
	}

	public void onDisable(){
		
	}

	void logError(Throwable throwable){
		for(World world : getServer().getWorlds()){
			for(Player player : world.getPlayers()){
				player.closeInventory();
			}
		}
		Writer writer = null;
		try {
			File logFileDir = new File("plugins/ArmorStandEditor/");
			logFileDir.mkdirs();
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("plugins/ArmorStandEditor/log.txt"), true)));
			writer.append("********"+ new Date(System.currentTimeMillis()).toString() + "********\n\n");
			writer.append("Plugins: " + listPlugins() + "\n\n\n");
			writer.append(throwable.getClass().getName() + "\n");
			for(StackTraceElement ste: throwable.getStackTrace()){
				writer.append(ste.toString());
				writer.append("\n");
			}
			writer.append("\n\n");
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {writer.close();} catch (Exception ex) {}
			getLogger().info("\n***********************\n***********************\n***********************\n"
					+ "ArmorStandEditor Encountered an error! Check plugins/ArmorStandEditor/log.txt"
					+"\nYou should send this file to the developer."
					+ "\n***********************\n***********************\n***********************");
		}
	}
	
	public boolean isPluginEnabled(String plugin){
		return getServer().getPluginManager().isPluginEnabled(plugin);
	}
	
	public void addProtection (Protection protection){
		protections.add(protection);
	}
	
	public ArrayList<Protection> getProtections(){
		return protections;
	}

	public void log(String message){
		this.getServer().getLogger().info("ArmorStandEditor: " + message);
	}
	
	public void print(String message){
		if(debug){
			this.getServer().broadcastMessage(message);
		}
	}
	
	public String listPlugins(){
		Plugin[] plugins = getServer().getPluginManager().getPlugins();
		String list = "";
		for(Plugin p : plugins){
			if(p!=null){
				list = list +" :" + p.getName() + " " + p.getDescription().getVersion() + ": ";
			}
		}
		return list;
	}
}
//todo: 
//Localisation
//Access to "DisabledSlots" data (probably simplified just a toggle enable/disable)
//Access to the "Marker" switch (so you can make the hitbox super small)
//*****Ability to rotate armorstands
//API so that developers can add their own means of having editing respect protected regions (the plugin already supports worldguard, greifprevention and plotsquared)
//Target a specific armorstand to edit, so solve the issue of editing armorstands close to each other and to make the Marker switch a viable option.
//Ability for users to set a per-user amount for coarse/fine adjustment
//Force an update packet when an armorstand moves a small amount so it feels more responsive.
//Place items in the head and arm slots via menu
//Menu description
//Dual wielding