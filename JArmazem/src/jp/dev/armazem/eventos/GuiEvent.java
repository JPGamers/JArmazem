package jp.dev.armazem.eventos;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import jp.dev.armazem.Main;
import jp.dev.armazem.api.ItemAPI;
import jp.dev.armazem.manager.Plantas;
import jp.dev.armazem.manager.PlantasAPI;

public class GuiEvent implements Listener {

	public HashMap<Player, Plantas> responder = new HashMap<>();
	private Main plugin;

	public GuiEvent(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void AoClicar(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getInventory().getName().equalsIgnoreCase(
				plugin.config.getConfig().getString("Menu.Nome").replace("&", "§").replace("%player%", p.getName()))) {
			if (plugin.plantacao.getConfig().contains("Plantacoes")) {
				for (String path : plugin.plantacao.getConfig().getConfigurationSection("Plantacoes").getKeys(false)) {
					Plantas planta = PlantasAPI.manager.getPlantas(path);
					List<String> lore = plugin.config.getConfig().getStringList("Menu.Lore");

					lore.replaceAll(line -> {
						if (planta != null) {
							return line.replace("&", "§").replace("{quantia}", String.valueOf(planta.getQuantidade()))
									.replace("{unidade}", String.valueOf(planta.getPreco()))
									.replace("{total}", String.valueOf(planta.getQuantidade() * planta.getPreco()));
						}
						return line;
					});
					@SuppressWarnings("deprecation")
					ItemStack item = ItemAPI.CriarItem(planta.getIcon().getType(),
							planta.getIcon().getItemMeta().getDisplayName(), planta.getIcon().getData().getData(),
							lore);
					if (e.getCurrentItem().getType() != null && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(item.getItemMeta().getDisplayName())) {
						e.setCancelled(true);
						// Botao vender
						if (e.getClick() == ClickType.LEFT) {
							venderTudo(p, path);
						}
						// Recolher
						if (e.getClick() == ClickType.RIGHT) {
							if (planta.getQuantidade() == 0) {
								return;
							}
							p.sendMessage("");
							p.sendMessage("§7Digite §6§lTUDO §7para pegar tudo");
							p.sendMessage("§7Ou Digite a §6§lQTD §7desejada.");
							p.sendMessage("§7Digite §6§lcancelar §7para não pegar os drops.");
							p.sendMessage("");
							p.closeInventory();
							responder.put(p, planta);
						}
					} else {
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onResponder(AsyncPlayerChatEvent e) {
		if (responder.containsKey(e.getPlayer())) {
			Player p = e.getPlayer();
			String msg = e.getMessage();

			if (msg.equalsIgnoreCase("tudo")) {
				pegarCrop(p, responder.get(p), msg);
			} else if (msg.equalsIgnoreCase("cancelar")) {
				responder.remove(e.getPlayer());
			} else {
				try {
					pegarCrop(p, responder.get(p), msg);
				} catch (NumberFormatException ex) {
				}
			}

			responder.remove(e.getPlayer());
		}
	}

	public void pegarCrop(Player p, Plantas planta, String res) {
		int qtd;

		if (res.equalsIgnoreCase("tudo")) {
			qtd = Integer.MAX_VALUE;
			int espacoNoInventario = ItemAPI.invSpace(p.getInventory(),
					Material.valueOf(planta.getIcon().getType().toString()));
			if (espacoNoInventario > 0) {
				int quantidadePegar = Math.min(planta.getQuantidade(), espacoNoInventario);

				p.getInventory().addItem(
						new ItemStack(Material.valueOf(planta.getIcon().getType().toString()), quantidadePegar));
				p.sendMessage("§a§lARMAZEM §aVocê pegou todos " + planta.getIcon().getType() + ".");
				planta.setQuantidade(planta.getQuantidade() - quantidadePegar);
				plugin.sql.addPlanta(p.getUniqueId(), planta.getTipo(), planta.getQuantidade());
			} else {
				p.sendMessage("§c§lARMAZEM §cSeu inventário está cheio.");
			}
		} else {
			qtd = Integer.parseInt(res);
			if (qtd > 0 && planta.getQuantidade() >= qtd) {
				int espacoNoInventario = ItemAPI.invSpace(p.getInventory(),
						Material.valueOf(planta.getIcon().getType().toString()));

				if (espacoNoInventario >= qtd) {
					p.getInventory()
							.addItem(new ItemStack(Material.valueOf(planta.getIcon().getType().toString()), qtd));
					p.sendMessage("§a§lARMAZEM §aVocê pegou " + format(qtd) + " " + planta.getIcon().getType() + ".");
					planta.setQuantidade(planta.getQuantidade() - qtd);
					plugin.sql.addPlanta(p.getUniqueId(), planta.getTipo(), planta.getQuantidade());
				} else {
					p.sendMessage("§c§lARMAZEM §cSeu inventário está cheio.");
				}
			} else {
				p.sendMessage("§c§lARMAZEM §cQuantidade inválida ou insuficiente.");
			}
		}
	}

	public void venderTudo(Player p, String path) {
		Plantas planta = PlantasAPI.manager.getPlantas(path);
		if (planta != null) {
			if (planta.getQuantidade() == 0) {
				return;
			} else {
				double total = planta.getPreco() * planta.getQuantidade();
				plugin.economy.depositPlayer(p, total);
				p.sendMessage("§aVocê vendeu " + format(planta.getQuantidade()) + " drops de "
						+ planta.getIcon().getItemMeta().getDisplayName() + " por: §a$§f" + format(total));
				planta.setQuantidade(0);
				plugin.sql.addPlanta(p.getUniqueId(), path, planta.getQuantidade());
				p.closeInventory();
			}
		}
	}

	public static final double LOG = 6.907755278982137D;
	public static final Object[][] VALUES = {
			{ "", "K", "M", "B", "T", "Q", "QQ", "S", "SS", "O", "N", "D", "UN", "DD", "TR", "QT", "QN", "SD", "SPD",
					"OD", "ND", "VG", "UVG", "DVG", "TVG", "QTV" },
			{ 1D, 1000.0D, 1000000.0D, 1.0E9D, 1.0E12D, 1.0E15D, 1.0E18D, 1.0E21D, 1.0E24D, 1.0E27D, 1.0E30D, 1.0E33D,
					1.0E36D, 1.0E39D, 1.0E42D, 1.0E45D, 1.0E48D, 1.0E51D, 1.0E54D, 1.0E57D, 1.0E60D, 1.0E63D, 1.0E66D,
					1.0E69D, 1.0E72D } };

	public static final DecimalFormat FORMAT = new DecimalFormat("#,###.##",
			new DecimalFormatSymbols(new Locale("pt", "BR")));

	public static String format(double number) {
		if (number == 0)
			return FORMAT.format(number);
		int index = (int) (Math.log(number) / LOG);
		return FORMAT.format(number / (double) VALUES[1][index]) + VALUES[0][index];
	}

}
