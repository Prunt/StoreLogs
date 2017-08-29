package me.prunt.storelogs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Main extends Plugin {
    // Config
    private Configuration config;

    @Override
    public void onEnable() {
	// Tries to load config file
	try {
	    // Loads the config file
	    config = ConfigurationProvider.getProvider(YamlConfiguration.class)
		    .load(new File(getDataFolder(), "config.yml"));

	    // If it can't load the file
	} catch (IOException e) {
	    // If the folder doesn't exist
	    if (!getDataFolder().exists())
		// Creates one
		getDataFolder().mkdir();

	    // Gets the config file
	    File file = new File(getDataFolder(), "config.yml");

	    // If the file doesn't exist
	    if (!file.exists()) {
		// Tries to get the default config file
		try (InputStream in = getResourceAsStream("config.yml")) {
		    // Copies it to the plugin folder
		    Files.copy(in, file.toPath());

		    // Loads the config file
		    config = ConfigurationProvider.getProvider(YamlConfiguration.class)
			    .load(new File(getDataFolder(), "config.yml"));
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
	    }
	}

	// Creates a folder for logs if it doesn't exist
	File logFolder = new File("./" + config.getString("folder") + "/");
	if (!logFolder.exists()) {
	    logFolder.mkdir();
	}
    }

    @Override
    public void onDisable() {
	// Moves the log to specified folder
	moveLogs();
    }

    // Moves the log to specified folder
    private void moveLogs() {
	// Gets a list of all items in Bungeecord folder
	File[] fileList = new File("./").listFiles();

	// Figures out a name for the log file based on given format and current time
	DateFormat dateFormat = new SimpleDateFormat(config.getString("format"));
	Date date = new Date();
	String name = dateFormat.format(date);

	// Loops through the listed items
	for (int i = 0; i < fileList.length; i++) {
	    // If it's a file, not a folder
	    if (fileList[i].isFile()) {
		// Gets the file name
		String fileName = fileList[i].getName();

		// If the file is a log, but not the lock
		if (fileName.startsWith("proxy.log") && !fileName.endsWith(".lck")) {
		    // Moves the file to the new location
		    fileList[i].renameTo(new File("./" + config.getString("folder") + "/" + name + ".log"));
		}
	    }
	}
    }
}
