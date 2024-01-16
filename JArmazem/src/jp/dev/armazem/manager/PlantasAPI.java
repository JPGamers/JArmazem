package jp.dev.armazem.manager;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import jp.dev.armazem.Main;
import jp.dev.armazem.api.ItemAPI;

public class PlantasAPI {

	public static PlantasManager manager;
	private Main plugin;
	
	public PlantasAPI(Main plugin) {
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	public void reload() {
		manager = new PlantasManager();
		if (plugin.plantacao.contains("Plantacoes")) {
			for (String path : plugin.plantacao.getConfig().getConfigurationSection("Plantacoes")
					.getKeys(false)) {
				ConfigurationSection sec = plugin.plantacao.getConfig()
						.getConfigurationSection("Plantacoes." + path);
				String tipo = path;
				int ID = sec.getInt("ID");
				short Data = (short) sec.getInt("Data");
				String Display = sec.getString("Display").replace("&", "§");
				int Preco = sec.getInt("Preco");
				int ID_Icon = sec.getInt("ItemDisplay.ID");
				short Data_Icon = (short) sec.getInt("ItemDisplay.Data");
				String Name_Icon = sec.getString("ItemDisplay.Name").replace("&", "§");
				ItemStack crop = ItemAPI.CriarItem(Material.getMaterial(ID), Display, Data);
				ItemStack icon = ItemAPI.CriarItem(Material.getMaterial(ID_Icon), Name_Icon, Data_Icon);
				Plantas planta = new Plantas();
				planta.setPlanta(crop);
				planta.setTipo(tipo);
				planta.setIcon(icon);
				planta.setPreco(Preco);
				manager.getPlantas().add(planta);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void save() {
		manager = new PlantasManager();
		if (manager == null) {
			return;
		}

		if (manager.getPlantas().isEmpty()) {
			return;
		}

		// Limpa a seção existente para evitar duplicatas
		plugin.plantacao.getConfig().set("Plantacoes", null);

		// Itera sobre a lista de plantas no manager
		for (int i = 0; i < manager.getPlantas().size(); i++) {
			Plantas planta = manager.getPlantas().get(i);
			String path = "Plantacoes." + i;

			// Configura as informações básicas da planta na seção
			plugin.plantacao.getConfig().set(path + ".ID", planta.getPlanta().getType().getId());
			plugin.plantacao.getConfig().set(path + ".Data", planta.getPlanta().getDurability());
			plugin.plantacao.getConfig().set(path + ".Display",
					planta.getPlanta().getItemMeta().getDisplayName());
			plugin.plantacao.getConfig().set(path + ".Preco", planta.getPreco());

			// Adiciona informações específicas do seu plugin
			plugin.plantacao.getConfig().set(path + ".PermissionVender", planta.getPermVender());
			plugin.plantacao.getConfig().set(path + ".PermissionRecolher", planta.getPermRecolher());

			// Adiciona informações do item de exibição no menu
			plugin.plantacao.getConfig().set(path + ".ItemDisplay.ID", planta.getIcon().getTypeId());
			plugin.plantacao.getConfig().set(path + ".ItemDisplay.Data", planta.getIcon().getData());
			plugin.plantacao.getConfig().set(path + ".ItemDisplay.Name",
					planta.getIcon().getItemMeta().getDisplayName());
		}

		// Salva as alterações no arquivo de configuração
		plugin.plantacao.saveConfig();

	}

}
