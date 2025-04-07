package org.hedgetech.slashwarp.platform;

import org.hedgetech.slashwarp.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Fabric Platform Helper
 */
public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    /**
     * Default Constructor
     */
    public FabricPlatformHelper() { }
}
