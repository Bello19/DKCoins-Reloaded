package ch.dkrieger.coinsystem.bungeecord.listeners;


import ch.dkrieger.coinsystem.bungeecord.BungeeCordCoinSystemBootstrap;
import ch.dkrieger.coinsystem.core.CoinSystem;
import ch.dkrieger.coinsystem.core.manager.MessageManager;
import ch.dkrieger.coinsystem.core.player.CoinPlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener{

	@EventHandler
	public void onLogin(final LoginEvent event){
		ProxyServer.getInstance().getScheduler().runAsync(BungeeCordCoinSystemBootstrap.getInstance(), ()->{
			CoinPlayer player = null;
			try {
				try{
					player = CoinSystem.getInstance().getPlayerManager().getPlayerSave(event.getConnection().getUniqueId());
				}catch (Exception ignored){}
				player = CoinSystem.getInstance().getPlayerManager().getPlayerSave(event.getConnection().getUniqueId());
			}catch (Exception exception){
				event.setCancelled(true);
				event.setCancelReason(new TextComponent("§cError"));
				exception.printStackTrace();
			}
			if(player == null) player = CoinSystem.getInstance().getPlayerManager().createPlayer(event.getConnection().getName()
					,event.getConnection().getUniqueId());
			else player.updateInfos(event.getConnection().getName(),CoinSystem.getInstance().getPlatform().getColor(player)
					,System.currentTimeMillis());
		});
	}

	@EventHandler
	public void onLeave(final PlayerDisconnectEvent event){
		ProxyServer.getInstance().getScheduler().runAsync(BungeeCordCoinSystemBootstrap.getInstance(),()->{
			CoinPlayer player = CoinSystem.getInstance().getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
			if(player != null) player.updateInfos(event.getPlayer().getName(),CoinSystem.getInstance().getPlatform().getColor(player)
					,System.currentTimeMillis());
		});
	}

	@EventHandler
	public void onPostLogin(PostLoginEvent event) {
		ProxyServer.getInstance().getScheduler().runAsync(BungeeCordCoinSystemBootstrap.getInstance(), ()-> {
			if(event.getPlayer().hasPermission("dkbans.admin")) {
				if(CoinSystem.getInstance().getUpdateChecker().hasNewVersion()){
					event.getPlayer().sendMessage(TextComponent.fromLegacyText(MessageManager.getInstance().prefix + "§7New version available §e" + CoinSystem.getInstance().getUpdateChecker().getLatestVersionString()));
				}
				BaseComponent[] messages = CoinSystem.getInstance().getUpdateChecker().getEndOfLifeMessage();
				if(messages != null){
					event.getPlayer().sendMessage(MessageManager.getInstance().prefix+" §7------------------------");
					for (BaseComponent message : messages) {
						event.getPlayer().sendMessage(message);
					}
					event.getPlayer().sendMessage(MessageManager.getInstance().prefix+" §7------------------------");
				}
			}
		});
	}
}
