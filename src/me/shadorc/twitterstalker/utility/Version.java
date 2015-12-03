package me.shadorc.twitterstalker.utility;

import java.util.regex.Pattern;


public class Version {

	private String separator = ".";

	private int major;
	private int minor;
	private int macro;

	private boolean isBeta;

	public Version(String version, boolean isBeta) {
		this.isBeta = isBeta;

		//Pattern.quote avoid to use '.' as a regex expression
		String[] parse = version.split(Pattern.quote(separator));
		major = Integer.parseInt(parse[0]);
		minor = Integer.parseInt(parse[1]);
		macro = Integer.parseInt(parse[2]);
	}

	public Version(String version) {
		this(version, false);
	}

	public boolean isNewerThan(Version version) {
		//If the current version is a beta, don't check for update
		return Ressources.forceUpdate || 
				!version.isBeta() && (this.major > version.getMajor() || this.minor > version.getMinor() || this.macro > version.getMacro());
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getMacro() {
		return macro;
	}

	public boolean isBeta() {
		return isBeta;
	}

	@Override
	public String toString() {
		return major + separator + minor + separator + macro + (isBeta ? "-Beta" : "");
	}
}
