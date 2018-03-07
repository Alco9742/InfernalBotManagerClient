package net.nilsghesquiere.util;

public class ProgramUtil {
	public static String getCapitalizedString(boolean bool){
		String boolString = String.valueOf(bool);
		return boolString.substring(0, 1).toUpperCase() + boolString.substring(1);
	}
}
