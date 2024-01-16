package jp.dev.armazem.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlantasManager {

	private ArrayList<Plantas> plantas = new ArrayList<Plantas>();
	private Map<String, Plantas> plantaCache = new HashMap<String, Plantas>();
	
	public Plantas getPlantas(String nome) {
		for (Plantas planta : this.plantas) {
			if (planta.getTipo().equalsIgnoreCase(nome))
				return planta;
		}
		return null;
	}
	
	public ArrayList<Plantas> getPlantas() {
		return plantas;
	}
	public void setPlantas(ArrayList<Plantas> plantas) {
		this.plantas = plantas;
	}
	public Map<String, Plantas> getPlantaCache() {
		return plantaCache;
	}
	public void setPlantaCache(Map<String, Plantas> plantaCache) {
		this.plantaCache = plantaCache;
	}
	
	
	
}
