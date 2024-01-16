package jp.dev.armazem.api;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigAPI {

    private JavaPlugin plugin;
    private String name;
    private File file;
    private YamlConfiguration config;
    
    public ConfigAPI(JavaPlugin pl, String name) {
    	this.plugin = pl;
    	setName(name);
    	reloadConfig();
    }
    
    public void create(String path) {
    	getConfig().createSection(path);
    }
    
    public void remove(String path) {
    	set(path, null);
    }
    
    public void set(String path, Object value) {
    	getConfig().set(path, value);
    }
    
    public boolean contains(String path) {
    	return getConfig().contains(path);
    }
    
    public void saveConfig() {
    	try {
			getConfig().save(getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void saveDefault() {
    	getConfig().options().copyDefaults(true);
    	saveConfig();
    }
    
    public void saveDefaultConfig() {
    	getPlugin().saveResource(getName(), false);
    }
    
    public void reloadConfig() {
    	file = new File(getPlugin().getDataFolder(), getName());
    	config = YamlConfiguration.loadConfiguration(getFile());
    }
    
    public void deleteConfig() {
    	getFile().delete();
    }
    
    public boolean existConfig() {
		return getFile().exists();
    }
    
	public JavaPlugin getPlugin() {
		return plugin;
	}
	public void setPlugin(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public File getFile() {
		return file;
	}
	public YamlConfiguration getConfig() {
		return config;
	}
    
    
	
}
