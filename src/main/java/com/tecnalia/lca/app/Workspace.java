package com.tecnalia.lca.app;

import java.io.File;

/**
 * The workspace configuration of openLCA. The workspace is located in the
 * folder "openLCA-data" in the user's home directory (system property
 * user.home).
 */
public class Workspace {

	private static File dir;

	/**
	 * Get the workspace directory. Returns null if the workspace was not yet
	 * initialized.
	 */
	public static File getDir() {
		if (dir == null)
			init();
		return dir;
	}

	/**
	 * Initializes the workspace of the application. Should be called only once
	 * when the application bundle starts.
	 */
	public static File init() {
		try {			
			File dir = getFromUserHome();
			Workspace.dir = dir;
			return dir;
		} catch (Exception e) {
			// no logging here as the logger is not yet configured
			e.printStackTrace();
			return null;
		}
	}

	private static File getFromUserHome() {
		String prop = System.getProperty("user.home");
		File userDir = new File(prop);
		File dir = new File(userDir, Config.WORK_SPACE_FOLDER_NAME);
		if (!dir.exists())
			dir.mkdirs();
		return dir;
	}
}
