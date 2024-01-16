package jp.dev.armazem.eventos;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemSpawnEvent;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;

import jp.dev.armazem.Main;
import jp.dev.armazem.api.ActionBarAPI;
import jp.dev.armazem.manager.Plantas;
import jp.dev.armazem.manager.PlantasAPI;

public class ArmazemEvent implements Listener {

	private Main plugin;
	
	public ArmazemEvent(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void cactoCrescer(ItemSpawnEvent e) {
		Item item = e.getEntity();
		Location loc = e.getLocation();
		PlotAPI plotAPI = new PlotAPI();
		Plot plot = plotAPI.getPlot(loc);

		if (plot != null) {
			UUID player = plot.getOwners().iterator().next();
			if (Bukkit.getPlayer(player) != null && Bukkit.getPlayer(player).isOnline()) {
				if (item.getItemStack().getType() == Material.CACTUS) {
					Plantas planta = PlantasAPI.manager.getPlantas("cacto");
					if (plugin.config.getConfig().getBoolean("Opcoes.ativar-limite")) {
						if (!ultrapassaLimite(Bukkit.getPlayer(player), planta, planta.getQuantidade())) {
							if (planta != null) {
								planta.setDono(player);
								planta.setQuantidade(planta.getQuantidade() + 1);
								e.setCancelled(true);
								PlantasAPI.manager.getPlantaCache().put("cacto", planta);
								plugin.sql.addPlanta(planta.getDono(), "cacto", planta.getQuantidade());
							}
						}else {
							ActionBarAPI.sendActionBar(Bukkit.getPlayer(player), "§cSeu armazem está cheio!");
							e.setCancelled(true);
						}
					}else {
						if (planta != null) {
							planta.setDono(player);
							planta.setQuantidade(planta.getQuantidade() + 1);
							e.setCancelled(true);
							PlantasAPI.manager.getPlantaCache().put("cacto", planta);
							plugin.sql.addPlanta(planta.getDono(), "cacto", planta.getQuantidade());
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void aoQuebrar(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Block block = e.getBlock();
		PlotAPI plotAPI = new PlotAPI();
		Plot plot = plotAPI.getPlot(block.getLocation());
		if (plot != null && plot.isOwner(p.getUniqueId())) {
			if (plugin.plantacao.getConfig().contains("Plantacoes")) {
				for (String path : plugin.plantacao.getConfig().getConfigurationSection("Plantacoes")
						.getKeys(false)) {
					Plantas planta = PlantasAPI.manager.getPlantas(path);
					if (planta != null) {
						if (block.getType() == planta.getPlanta().getType()) {
							addPlanta(p, planta, block, path, planta.getQuantidade());
						}
					}
				}
			}
		}
	}
	
	private void addPlanta(Player p, Plantas planta, Block crop, String tipo, int qnt) {
		if (planta != null) {
			if (plugin.config.getConfig().getBoolean("Opcoes.ativar-limite")) {
				if (!ultrapassaLimite(p, planta, qnt)) {
					if (p.getItemInHand().getType().name().endsWith("_PICKAXE") || p.getItemInHand().getType().name().endsWith("_AXE")) {
						if (p.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) > 0) {
							int level = p.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
							if (crop.getType() == planta.getPlanta().getType()) {
								crop.setType(Material.AIR);
								planta.setDono(p.getUniqueId());
								planta.setQuantidade(planta.getQuantidade() + (1 * level));
								PlantasAPI.manager.getPlantaCache().put(tipo, planta);
								plugin.sql.addPlanta(planta.getDono(), tipo, planta.getQuantidade());
							}else {
								if (crop.getType() == planta.getPlanta().getType()) {
									crop.setType(Material.AIR);
									planta.setDono(p.getUniqueId());
									planta.setQuantidade(planta.getQuantidade() + 1);
									PlantasAPI.manager.getPlantaCache().put(tipo, planta);
									plugin.sql.addPlanta(planta.getDono(), tipo, planta.getQuantidade());
								}
							}
						}
					}
				}
			}else {
				if (p.getItemInHand().getType().name().endsWith("_PICKAXE") || p.getItemInHand().getType().name().endsWith("_AXE")) {
					if (p.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) > 0) {
						int level = p.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
						if (crop.getType() == planta.getPlanta().getType()) {
							crop.setType(Material.AIR);
							planta.setDono(p.getUniqueId());
							planta.setQuantidade(planta.getQuantidade() + (1 * level));
							PlantasAPI.manager.getPlantaCache().put(tipo, planta);
							plugin.sql.addPlanta(planta.getDono(), tipo, planta.getQuantidade());
						}else {
							if (crop.getType() == planta.getPlanta().getType()) {
								crop.setType(Material.AIR);
								planta.setDono(p.getUniqueId());
								planta.setQuantidade(planta.getQuantidade() + 1);
								PlantasAPI.manager.getPlantaCache().put(tipo, planta);
								plugin.sql.addPlanta(planta.getDono(), tipo, planta.getQuantidade());
							}
						}
					}
				}
			}
		}
	}
	
	public boolean ultrapassaLimite(Player p, Plantas planta, int quantidadeNova) {
	    int limite = plugin.config.getConfig().getInt("Opcoes.Limite");
	    int totalPlantas = 0;


	    if (planta != null) {
		    totalPlantas += plugin.sql.getQuantidadeTotal(p.getUniqueId());
		    if (totalPlantas >= limite) {
		    	ActionBarAPI.sendActionBar(p, "§cSeu armazem está cheio!");
		        return true;
		    }
	    }

	    return false;
	}


}
