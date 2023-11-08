package xyz.gothaj.webserverhandler;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.gothaj.webserverhandler.utils.Webserver;

public final class WebAPI extends JavaPlugin {


    public Webserver server;

    @Override
    public void onEnable() {
        this.server = new Webserver(this, 59404);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
