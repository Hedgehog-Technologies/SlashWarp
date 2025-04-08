package org.hedgetech.slashwarp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private Constants() {
		throw new UnsupportedOperationException("Static Utility class, no need to instantiate");
	}
}