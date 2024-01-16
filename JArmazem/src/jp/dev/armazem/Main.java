package jp.dev.armazem;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import jp.dev.armazem.api.AsciiBox;
import jp.dev.armazem.api.ConfigAPI;
import jp.dev.armazem.comandos.ArmazemCommand;
import jp.dev.armazem.eventos.ArmazemEvent;
import jp.dev.armazem.eventos.GuiEvent;
import jp.dev.armazem.manager.PlantasAPI;
import jp.dev.armazem.sqlite.SQLite;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {

	public ConfigAPI config;
	public ConfigAPI plantacao;
	public SQLite sql;
	public Economy economy;
	private PlantasAPI plantaAPI;

	@Override
	public void onEnable() {
		this.plantaAPI = new PlantasAPI(this);
		// Verify Vault
        if (!setupEconomy()) {
            getLogger().severe("Vault não encontrado! Desativando o plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        if (!isPlotSquaredEnabled()) {
            getLogger().severe("PlotSquared não encontrado! Desativando o plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

		
		// Config.yml
		config = new ConfigAPI(this, "config.yml");
		config.saveDefaultConfig();
		
		// Plantacao.yml
		plantacao = new ConfigAPI(this, "plantacao.yml");
		plantacao.saveDefaultConfig();
		
		//SQlite
		sql = new SQLite(this);
		sql.connect();
		
		// Command /armazem & evento
		getCommand("armazem").setExecutor(new ArmazemCommand(this));
		Bukkit.getPluginManager().registerEvents(new ArmazemEvent(this), this);
		Bukkit.getPluginManager().registerEvents(new GuiEvent(this), this);
		plantaAPI.reload();
		// carregar dados!
		sql.carregarTodosOsDados();
		
		
		// Msg plugin
		
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

		String[] message = new String[] {
		    "",
		    "§e§lJArmazem",
		    "",
		    "§bPlugin de armazem",
		    "§bVersão §f1.0",
		    "",
		    "§bDesenvolvedor - §fJP",
		    "§bDiscord - §fjp__ofc",
		    "",
		};

		String render = new AsciiBox().size(50).borders("━", "┃").corners("┏", "┓", "┗", "┛").render(message);
		console.sendMessage(render);
		
	}

	@Override
	public void onDisable() {
		// Sql
		sql = new SQLite(this);
		sql.disconnect();

		// salvar config.yml!
		plantaAPI.save();
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}

		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}

		economy = rsp.getProvider();
		return economy != null;
	}

	private boolean isPlotSquaredEnabled() {
		Plugin plotSquaredPlugin = getServer().getPluginManager().getPlugin("PlotSquared");
		return plotSquaredPlugin != null && plotSquaredPlugin.isEnabled();
	}

}
