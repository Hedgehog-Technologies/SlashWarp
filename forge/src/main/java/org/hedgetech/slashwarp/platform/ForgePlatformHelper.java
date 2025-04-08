package org.hedgetech.slashwarp.platform;

import org.hedgetech.slashwarp.platform.services.IPlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

/**
 * Forge Platform Helper
 */
public class ForgePlatformHelper implements IPlatformHelper {
    /**
     * Default Constructor - nothing special
     */
    public ForgePlatformHelper() { }

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }
}