package org.hedgetech.slashwarp;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Constants Utility class
 */
public final class Constants {
	/**
	 * Mod Id - should be all lowercase
	 */
	public static final String MOD_ID = "slashwarp";

	/**
	 * Mod Name - Should be PascalCase
	 */
	public static final String MOD_NAME = "SlashWarp";

	/**
	 * Logger - To make more consistent logging instead of using stdout
	 */
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static final Set<Block> HARMFUL_FLOOR_BLOCKS = Set.of(
            Blocks.CACTUS,
            Blocks.MAGMA_BLOCK,
            Blocks.CAMPFIRE,
            Blocks.SOUL_CAMPFIRE,
            Blocks.FIRE,
            Blocks.LAVA,
            Blocks.POWDER_SNOW
    );

    public static final Set<String> RESERVED_WARP_NAMES = Set.of(
            "add", "back", "del", "list", "top"
    );

	private Constants() {
		throw new UnsupportedOperationException("Static Utility class, no need to instantiate");
	}
}
