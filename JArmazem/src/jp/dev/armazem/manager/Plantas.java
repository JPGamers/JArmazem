package jp.dev.armazem.manager;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

public class Plantas {

	private String tipo;
	private UUID dono;
	private String PermVender;
	private String PermRecolher;
	private ItemStack planta;
	private ItemStack icon;
	private int quantidade = 0;
	private double preco;
	
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public ItemStack getIcon() {
		return icon;
	}
	public void setIcon(ItemStack icon) {
		this.icon = icon;
	}
	public String getPermVender() {
		return PermVender;
	}
	public void setPermVender(String permVender) {
		PermVender = permVender;
	}
	public String getPermRecolher() {
		return PermRecolher;
	}
	public void setPermRecolher(String permRecolher) {
		PermRecolher = permRecolher;
	}
	public UUID getDono() {
		return dono;
	}
	public void setDono(UUID dono) {
		this.dono = dono;
	}
	public ItemStack getPlanta() {
		return planta;
	}
	public void setPlanta(ItemStack planta) {
		this.planta = planta;
	}
	public int getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}
	public double getPreco() {
		return preco;
	}
	public void setPreco(double preco) {
		this.preco = preco;
	}

	
	
}
