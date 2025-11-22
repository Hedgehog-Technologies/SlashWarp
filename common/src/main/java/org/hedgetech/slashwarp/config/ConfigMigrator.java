package org.hedgetech.slashwarp.config;

public final class ConfigMigrator {
    private ConfigMigrator() {}

    public static boolean migrate(WarpConfig cfg) {
        if (cfg == null) return false;

        var changed = false;
        var version = cfg.configVersion == null ? "1.0" : cfg.configVersion;

        // apply upgrades sequentially
        while (!version.equals(WarpConfig.CURRENT_CONFIG_VERSION)) {
            switch (version) {
                // Future migrations:
//                case "1.0":
//                    ...
//                    version = "1.1";
//                    cfg.configVersion = version;
//                    changed = true;
//                    break;

                default:
                    version = WarpConfig.CURRENT_CONFIG_VERSION;
                    break;
            }
        }

        return changed;
    }
}
