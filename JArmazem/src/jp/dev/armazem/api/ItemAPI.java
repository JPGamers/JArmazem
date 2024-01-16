package jp.dev.armazem.api;


import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class ItemAPI {

	public static ItemStack CriarItem(Material mat, String nome, int data) {
		ItemStack it = new ItemStack(mat, 1 , (short) data);
		ItemMeta mt = it.getItemMeta();
		mt.setDisplayName(nome);
		it.setAmount(1);
		it.setItemMeta(mt);
		return it;
	}
	
	public static ItemStack CriarItem(Material mat, String nome, int data, List<String> lore) {
		ItemStack it = new ItemStack(mat, 1 , (short) data);
		ItemMeta mt = it.getItemMeta();
		mt.setDisplayName(nome);
		mt.setLore(lore);
		it.setAmount(1);
		it.setItemMeta(mt);
		return it;
	}	
	
	public static ItemStack CriarItem(Material mat, String nome, int data, List<String> lore, boolean glow) {
		ItemStack it = new ItemStack(mat, 1,(short) data);
		ItemMeta mt = it.getItemMeta();
		mt.setDisplayName(nome);
		mt.setLore(lore);
		if (glow == true) {
			it.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 1);
			mt.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		it.setAmount(1);
		it.setItemMeta(mt);
		return it;
	}
	
	public static ItemStack CriarHead(String nome, String url, List<String> lore) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        if (url == null || url.isEmpty())
            return head;
        ItemMeta skullMeta = head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        profileField.setAccessible(true);
        try {
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        skullMeta.setLore(lore);
        skullMeta.setDisplayName(nome);
        head.setItemMeta(skullMeta);
        return head;
    }
	
	public static int invSpace(PlayerInventory inv, Material m) {
		int count = 0;
		for (int slot = 0; slot < 36; slot++) {
			ItemStack is = inv.getItem(slot);
			if (is == null) {
				count += m.getMaxStackSize();
			}
			if (is != null) {
				if (is.getType() == m) {
					count += (m.getMaxStackSize() - is.getAmount());
				}
			}
		}
		return count;
	}	
	
}
