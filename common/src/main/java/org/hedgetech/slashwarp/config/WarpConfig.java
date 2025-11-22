package org.hedgetech.slashwarp.config;

import com.google.gson.GsonBuilder;
import org.hedgetech.slashwarp.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WarpConfig {
    public static volatile WarpConfig CONFIG;

    public static final String CURRENT_CONFIG_VERSION = "1.0";

    public String configVersion = CURRENT_CONFIG_VERSION;
    public boolean allowCrossDimensionWarps = true;
    public boolean enableWarpBackToDeathPoint = false;

    private WarpConfig() {}

    public static synchronized void init() {
        if (CONFIG != null) return;
        CONFIG = loadConfig();
    }

    private static WarpConfig loadConfig() {
        var gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

        var configDir = Paths.get("config");
        var configPath = configDir.resolve("slashwarp.json");

        Constants.LOG.warn("Dir: {}; Path: {}", configDir, configPath);

        try {
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }

            if (!Files.exists(configPath)) {
                var defaultConfig = new WarpConfig();

                try (var writer = Files.newBufferedWriter(configPath)) {
                    gson.toJson(defaultConfig, writer);
                }

                return defaultConfig;
            }

            try (var reader = Files.newBufferedReader(configPath)) {
                var config = gson.fromJson(reader, WarpConfig.class);
                if (config == null) config = new WarpConfig();

                var changed = ConfigMigrator.migrate(config);

                if (config.configVersion == null) {
                    config.configVersion = CURRENT_CONFIG_VERSION;
                    changed = true;
                }

                if (changed) {
                    try (var writer = Files.newBufferedWriter(configPath)) {
                        gson.toJson(config, writer);
                    }
                }

                return config;
            }
        } catch (IOException e) {
            Constants.LOG.error("Failed to load/write config: {}", e.getMessage());
            return new WarpConfig();
        }
    }

    public void save() throws IOException {
        var gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        var configPath = Paths.get("config").resolve("slashwarp.json");

        Files.createDirectories(configPath.getParent());

        try (var writer = Files.newBufferedWriter(configPath)) {
            gson.toJson(this, writer);
        }
    }
}
