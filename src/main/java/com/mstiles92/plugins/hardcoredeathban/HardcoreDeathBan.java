/*
 * This document is a part of the source code and related artifacts for
 * HardcoreDeathBan, an open source Bukkit plugin for hardcore-type servers
 * where players are temporarily banned upon death.
 *
 * http://dev.bukkit.org/bukkit-plugins/hardcoredeathban/
 * http://github.com/mstiles92/HardcoreDeathBan
 *
 * Copyright (c) 2014 Matthew Stiles (mstiles92)
 *
 * Licensed under the Common Development and Distribution License Version 1.0
 * You may not use this file except in compliance with this License.
 *
 * You may obtain a copy of the CDDL-1.0 License at
 * http://opensource.org/licenses/CDDL-1.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the license.
 */

package com.mstiles92.plugins.hardcoredeathban;

import com.mstiles92.plugins.hardcoredeathban.commands.Credits;
import com.mstiles92.plugins.hardcoredeathban.commands.Deathban;
import com.mstiles92.plugins.hardcoredeathban.config.Config;
import com.mstiles92.plugins.hardcoredeathban.listeners.PlayerListener;
import com.mstiles92.plugins.hardcoredeathban.util.Bans;
import com.mstiles92.plugins.hardcoredeathban.util.Log;
import com.mstiles92.plugins.hardcoredeathban.util.RevivalCredits;
import com.mstiles92.plugins.stileslib.commands.CommandRegistry;
import com.mstiles92.plugins.stileslib.updates.UpdateChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * HardcoreDeathBan is the main class of this Bukkit plugin.
 * It handles enabling and disabling of this plugin, loading config
 * files, and other general methods needed for this plugin's operation.
 *
 * @author mstiles92
 */
public class HardcoreDeathBan extends JavaPlugin {
    private static HardcoreDeathBan instance;
    private static Config config;
    private UpdateChecker updateChecker;
    private CommandRegistry commandRegistry;

    public RevivalCredits credits = null;
    public Bans bans = null;

    @Override
    public void onEnable() {
        instance = this;
        config = new Config();

        try {
            credits = new RevivalCredits(this, "credits.yml");
            bans = new Bans(this, "bans.yml");
        } catch (Exception e) {
            Log.warning(ChatColor.RED + "Error opening a config file. Plugin will now be disabled.");
            getPluginLoader().disablePlugin(this);
        }

        commandRegistry = new CommandRegistry(this);
        commandRegistry.registerCommands(new Deathban());
        commandRegistry.registerCommands(new Credits());

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        if (config.shouldCheckForUpdates()) {
            updateChecker = new UpdateChecker(this, 42801, "hardcoredeathban", 216000);
            updateChecker.start();
        }
    }

    @Override
    public void onDisable() {
        credits.save();
        bans.save();
        config.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return commandRegistry.handleCommand(sender, command, label, args);
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    public static Config getConfigObject() {
        return config;
    }

    public static HardcoreDeathBan getInstance() {
        return instance;
    }
}
