package fr.momosced.hideandseek.Commands;

import fr.momosced.hideandseek.Main;

public class Commands {

    private Main plugin = Main.getInstance();

    public String cmd1 = "hsarena";
    public String cmd2 = "hsgame";

    public void onEnable(){
        registerCommands();
    }

    private void registerCommands() {
        this.plugin.getCommand(cmd1).setExecutor(new ArenaCommands());
        this.plugin.getCommand(cmd2).setExecutor(new GameCommands());
    }
}
