package com.github.joelgodofwar.vwh;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.github.joelgodofwar.vwh.api.Workstation;

@SuppressWarnings("unused")
public class VillagerWorkstationHighlights extends JavaPlugin implements Listener{
	public final static Logger logger = Logger.getLogger("Minecraft");
	
	@Override // TODO:
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		consoleInfo("ENABLED");
	}
	
	@Override // TODO:
	public void onDisable() {
		consoleInfo("DISABLED");
	}
	
	public void consoleInfo(String state) {
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info("**************************************");
		logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " is " + state);
		logger.info("**************************************");
	}
	public  void log(String dalog){
		logger.info("" + this.getName() + " " + this.getDescription().getVersion() + " " + dalog);
	}
	public  void logDebug(String dalog){
		log(" [DEBUG] " + dalog);
	}
	public void logWarn(String dalog){
		log(" [WARNING] " + dalog);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (cmd.getName().equalsIgnoreCase("VWH")||cmd.getName().equalsIgnoreCase("VillagerWorkstationHighlights")){
			if(!(sender instanceof Player)){
				return false;
			}else{
				if (args.length == 0){
					Player player = (Player) sender;
					Entity entity = getNearestEntityInSight(player, 10);
					log("entity=" + entity.toString());
					
					if(entity instanceof Villager){
						Villager villager = (Villager) entity;
						log("villager=" + villager.toString());
						Location workstation = villager.getMemory(MemoryKey.JOB_SITE);
						log("workstation=" + workstation.toString());
						AreaEffectCloud cloud = (AreaEffectCloud) villager.getLocation().getWorld().spawnEntity(workstation.add(.5, 1, .5), EntityType.AREA_EFFECT_CLOUD);
						cloud.setParticle(Particle.HEART, null);
						cloud.setDuration(200);
						cloud.setReapplicationDelay(10);
						cloud.setRadius(0.5f);
						cloud.setRadiusPerTick(0f);
						cloud.setRadiusOnUse(0f);
						
						//villager.getWorld().spawnParticle(Particle.HEART, workstation.add(0, .5, 0), 2000);
						//villager.getWorld().spawnParticle(Particle.HEART, workstation.add(.5, 0, 0), 2000);
						//villager.getWorld().spawnParticle(Particle.HEART, workstation.add(0, 0, .5), 2000);
						//villager.getWorld().spawnParticle(Particle.HEART, workstation.add(0, .5, 0), 2000);
						//villager.getWorld().spawnParticle(Particle.HEART, workstation.add(.5, 0, 0), 2000);
						log("completed");
						//villager.getWorld().sp
						//BlockFace a = player.getLineOfSight(arg0, arg1);
						return true;
					}
				}
				if(args[0].equalsIgnoreCase("set")){
					// /vwh set x y z
					//       0  1 2 3
					//   1   2  3 4 5
					log("args.length=" + args.length);
					if(!(args.length >= 4)){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " Arguments needed \n/vwh set x y z");
						return false;
					}else{
						Player player = (Player) sender;
						Entity entity = getNearestEntityInSight(player, 10);
						log("entity=" + entity.toString());
						if(entity instanceof Villager){
							Villager villager = (Villager) entity;
							log("villager=" + villager.toString());
							Location workstation = new Location(villager.getWorld(), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
							villager.setMemory(MemoryKey.POTENTIAL_JOB_SITE, workstation);//.getMemory(MemoryKey.JOB_SITE);
							log("workstation=" + workstation.toString());
						}
					}
					
				}
			}
			
		}
		return false;
	}
	
    public static Entity getNearestEntityInSight(Player player, int range) {
        ArrayList<Entity> entities = (ArrayList<Entity>) player.getNearbyEntities(range, range, range);
        ArrayList<Block> sightBlock = (ArrayList<Block>) player.getLineOfSight((Set<Material>) null, range);
        ArrayList<Location> sight = new ArrayList<Location>();
        for (int i = 0;i<sightBlock.size();i++)
            sight.add(sightBlock.get(i).getLocation());
        for (int i = 0;i<sight.size();i++) {
            for (int k = 0;k<entities.size();k++) {
                if (Math.abs(entities.get(k).getLocation().getX()-sight.get(i).getX())<1.3) {
                    if (Math.abs(entities.get(k).getLocation().getY()-sight.get(i).getY())<1.5) {
                        if (Math.abs(entities.get(k).getLocation().getZ()-sight.get(i).getZ())<1.3) {
                            return entities.get(k);
                        }
                    }
                }
            }
        }
        return null; //Return null/nothing if no entity was found
    }
	
	private List<Entity> getEntitys(Player player){
	    List<Entity> entitys = new ArrayList<Entity>();
	    for(Entity e : player.getNearbyEntities(10, 10, 10)){
	        if(e instanceof LivingEntity){
	            if(getLookingAt(player, (LivingEntity) e)){
	                entitys.add(e);
	                log("added");
	            }
	        }
	    }

	    return entitys;
	}
	private boolean getLookingAt(Player player, LivingEntity livingEntity){
	    Location eye = player.getEyeLocation();
	    Vector toEntity = livingEntity.getEyeLocation().toVector().subtract(eye.toVector());
	    double dot = toEntity.normalize().dot(eye.getDirection());

	    return dot > 0.99D;
	}
	
	@EventHandler
	public void playerInteract(PlayerInteractEvent event){
	  //this will be called automatically by bukkit whenever a player interacts
	  if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
	    //the player right-clicked a block
	    Material material = event.getClickedBlock().getType(); //get the block type clicked
	    if(material.equals(Material.STONE)){
	      //the block clicked was stone.
	    }
	  }
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEntityEvent event){	
		Player player = event.getPlayer();
		ItemStack main = player.getInventory().getItemInMainHand();
		log("main.getType()=" + main.getType());
		ItemStack off = player.getInventory().getItemInOffHand();
		log("off.getType()=" + off.getType());
		if(Workstation.isWorkstation(main.getType())||Workstation.isWorkstation(off.getType())){
			log("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
			Entity clicked = event.getRightClicked();
			
			if(event.getRightClicked() instanceof Villager&&player.isSneaking()){
				//player.isSneaking()
				event.setCancelled(true);
				log("isVillager");
				Villager villager = (Villager) event.getRightClicked();
				Location workstation = villager.getMemory(MemoryKey.JOB_SITE);
				log("workstation=" + workstation.toString());
				if(workstation != null){
					log("workstation != null");
					if(!(workstation.getWorld().getNearbyEntities(workstation, .5, 1, .5) instanceof AreaEffectCloud)){
						AreaEffectCloud cloud = (AreaEffectCloud) villager.getLocation().getWorld().spawnEntity(workstation.add(.5, 1, .5), EntityType.AREA_EFFECT_CLOUD);
						cloud.setParticle(Particle.HEART, null);
						cloud.setDuration(200);
						cloud.setReapplicationDelay(10);
						cloud.setRadius(0.5f);
						cloud.setRadiusPerTick(0f);
						cloud.setRadiusOnUse(0f);
						log("AreaEffectCloud set");
						}
				}else{
					log("workstation = null");
				}
				
			}else{
				log("!isVillager");
			}
			log("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			//event.setCancelled(false);
		}
	}
}
