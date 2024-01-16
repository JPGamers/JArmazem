package jp.dev.armazem.sqlite;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;

import jp.dev.armazem.Main;
import jp.dev.armazem.manager.Plantas;
import jp.dev.armazem.manager.PlantasAPI;

public class SQLite {

	public Main plugin;

	public SQLite(Main plugin) {
		this.plugin = plugin;
	}

	private Connection con;

	public boolean isConnected() {
		return this.con != null;
	}

	public void connect() {
		try {
			File file = new File(this.plugin.getDataFolder(), "plantacoes.db");
			Class.forName("org.sqlite.JDBC");
			this.con = DriverManager.getConnection("jdbc:sqlite:" + file);
			Bukkit.getConsoleSender().sendMessage("§a[JArmazem] - Conexão com SQLite foi um sucesso.");
			createTable();
		} catch (Exception var2) {
			Bukkit.getConsoleSender()
					.sendMessage("§c[JArmazem] - não foi possivel se conectar a o SQLite, desligando plugin.");
			this.plugin.getPluginLoader().disablePlugin(this.plugin);
		}

	}

	public void disconnect() {
		if (this.isConnected()) {
			try {
				this.con.close();
				Bukkit.getConsoleSender().sendMessage("§c[JArmazem] - Conexão finalizada com sucesso.");
			} catch (SQLException var2) {
				var2.printStackTrace();
			}
		}

	}

	public void createTable() {
		PreparedStatement stm = null;

		try {
			stm = this.con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS Armazem(uuid varchar(36), tipoplanta varchar(36), quantidade int)");
			stm.executeUpdate();
		} catch (Exception var11) {
			var11.printStackTrace();
		} finally {
			try {
				stm.close();
			} catch (SQLException var10) {
				var10.printStackTrace();
			}

		}
	}
	
	public void insertPlanta(UUID dono, String tipoplanta, int quantidade) {
	    try (PreparedStatement stm = this.con.prepareStatement("INSERT OR REPLACE INTO Armazem (uuid, tipoplanta, quantidade) VALUES (?, ?, ?)")) {
	        stm.setString(1, dono.toString());
	        stm.setString(2, tipoplanta);
	        stm.setInt(3, quantidade);
	        stm.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	

	public void addPlanta(UUID dono, String tipoplanta, int quantidade) {
	    if (exists(dono, tipoplanta)) {
	    	setPlanta(dono, tipoplanta, quantidade);
	    	return;
	    }else {
	    	insertPlanta(dono, tipoplanta, quantidade);
	    }
	}

	private boolean exists(UUID uuid, String tipoPlanta) {
	    try (PreparedStatement stm = this.con.prepareStatement("SELECT * FROM Armazem WHERE uuid = ? AND tipoplanta = ?")) {
	        stm.setString(1, uuid.toString());
	        stm.setString(2, tipoPlanta);
	        ResultSet rs = stm.executeQuery();
	        return rs.next();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}



	public void removePlanta(UUID dono, String tipoplanta) {
		PreparedStatement stm = null;
		try {
			stm = this.con.prepareStatement("DELETE FROM Armazem where uuid = ? AND tipoplanta = ?");
			stm.setString(1, dono.toString());
			stm.setString(2, tipoplanta);
			stm.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stm != null) {
					stm.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public void setPlanta(UUID uuid, String tipoPlanta, int quantidade) {
		PreparedStatement stm = null;

		try {
			stm = con.prepareStatement("UPDATE Armazem SET quantidade = ? WHERE uuid = ? AND tipoplanta = ?");
			stm.setInt(1, quantidade);
			stm.setString(2, uuid.toString());
			stm.setString(3, tipoPlanta);
			stm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stm != null) {
					stm.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void carregarTodosOsDados() {
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			stm = con.prepareStatement("SELECT * FROM Armazem");
			rs = stm.executeQuery();

			while (rs.next()) {
				UUID uuid = UUID.fromString(rs.getString("uuid"));
				String tipoPlanta = rs.getString("tipoplanta");
				int quantidade = rs.getInt("quantidade");

				Plantas planta = PlantasAPI.manager.getPlantas(tipoPlanta);
				planta.setDono(uuid);
				planta.setQuantidade(quantidade);
				Bukkit.getConsoleSender().sendMessage("§a[JArmazem] - Dados carregados com sucesso!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stm != null) {
					stm.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
	
	public int getQuantidadeTotal(UUID dono) {
	    int quantidadeTotal = 0;

	    try (PreparedStatement stm = con.prepareStatement("SELECT SUM(quantidade) as total FROM Armazem WHERE uuid = ?")) {
	        stm.setString(1, dono.toString());
	        ResultSet rs = stm.executeQuery();

	        if (rs.next()) {
	            quantidadeTotal = rs.getInt("total");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return quantidadeTotal;
	}


}
