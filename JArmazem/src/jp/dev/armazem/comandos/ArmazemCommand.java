package jp.dev.armazem.comandos;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;

import jp.dev.armazem.Main;
import jp.dev.armazem.api.ItemAPI;
import jp.dev.armazem.manager.Plantas;
import jp.dev.armazem.manager.PlantasAPI;

public class ArmazemCommand implements CommandExecutor {

	private Main plugin;

	public ArmazemCommand(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		if (s instanceof Player) {
			if (c.getName().equalsIgnoreCase("armazem")) {
				Player p = (Player) s;
				if (a.length < 1) {
					PlotAPI plotAPI = new PlotAPI();
					Plot plot = plotAPI.getPlot(p);
					if (plot != null && plot.isOwner(p.getUniqueId())) {
						openGui(p);
					} else {
						p.sendMessage("§cErro! O comando /armazem só funciona em plots!");
					}
				}
			}
		}
		return false;
	}

	private void openGui(Player p) {
		Inventory inv = Bukkit.createInventory(p, plugin.config.getConfig().getInt("Menu.Tamanho"),
				plugin.config.getConfig().getString("Menu.Nome").replace("&", "§"));
		List<Integer> slots = plugin.config.getConfig().getIntegerList("Menu.Slots");
		int itemCount = 0;

		if (plugin.plantacao.getConfig().contains("Plantacoes")) {
			for (String path : plugin.plantacao.getConfig().getConfigurationSection("Plantacoes").getKeys(false)) {
				Plantas planta = PlantasAPI.manager.getPlantas(path);
				List<String> lore = plugin.config.getConfig().getStringList("Menu.Lore");
				ConfigurationSection sec = plugin.plantacao.getConfig().getConfigurationSection("Plantacoes." + path);
				boolean glow = sec.getBoolean("ItemDisplay.Glow");
				lore.replaceAll(line -> {
					if (planta != null) {
						return line.replace("&", "§").replace("{quantia}", String.valueOf(format(planta.getQuantidade())))
								.replace("{unidade}", String.valueOf(format(planta.getPreco())))
								.replace("{total}", String.valueOf(format(planta.getQuantidade() * planta.getPreco())));
					}
					return line;
				});

				if (itemCount < slots.size()) {
					int slot = slots.get(itemCount);
					if (slot >= 0 && slot < inv.getSize()) {
						@SuppressWarnings("deprecation")
						ItemStack item = ItemAPI.CriarItem(planta.getIcon().getType(),
								planta.getIcon().getItemMeta().getDisplayName(), planta.getIcon().getData().getData(),
								lore, glow);
						inv.setItem(slot, item);
					}
				}
				itemCount++;
			}
		}
		if (plugin.config.contains("Menu.Item")) {
			if (plugin.config.getConfig().getBoolean("Menu.Item.Armazem.CustomSkull")) {
				List<String> lore = plugin.config.getConfig().getStringList("Menu.Item.Armazem.Lore");
				lore.replaceAll(line -> {
					return line.replace("&", "§")
							.replace("{limite}", String.valueOf(format(plugin.config.getConfig().getInt("Opcoes.Limite"))))
							.replace("{quantia_total}", String.valueOf(format(plugin.sql.getQuantidadeTotal(p.getUniqueId()))));
				});
				ItemStack head = ItemAPI.CriarHead(
						plugin.config.getConfig().getString("Menu.Item.Armazem.Name").replace("&", "§"),
						plugin.config.getConfig().getString("Menu.Item.Armazem.URL"), lore);
				inv.setItem(plugin.config.getConfig().getInt("Menu.Item.Armazem.Slot"), head);
			} else {
				List<String> lore = plugin.config.getConfig().getStringList("Menu.Item.Armazem.Lore");
				lore.replaceAll(line -> {
					return line.replace("&", "§")
							.replace("{limite}", String.valueOf(format(plugin.config.getConfig().getInt("Opcoes.Limite"))))
							.replace("{quantia_total}", String.valueOf(format(plugin.sql.getQuantidadeTotal(p.getUniqueId()))));
				});
				@SuppressWarnings("deprecation")
				ItemStack item = ItemAPI.CriarItem(
						Material.getMaterial(plugin.config.getConfig().getInt("Menu.Item.ID")),
						plugin.config.getConfig().getString("Menu.Item.Name").replace("&", "§"),
						plugin.config.getConfig().getInt("Menu.Item.Data"), lore);
				inv.setItem(plugin.config.getConfig().getInt("Menu.Item.Armazem.Slot"), item);
			}
		}
		p.openInventory(inv);
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
