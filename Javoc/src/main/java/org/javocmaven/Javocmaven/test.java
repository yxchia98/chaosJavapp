package org.javocmaven.Javocmaven;

public class test {
	public static void main(String[] args) {
		String s1 = "abCsssss";
		String s2 = "s";
		System.out.println(processRegionMatches(s1, s2));
	}
	public static boolean processRegionMatches(String src, String dest) {
		for (int i = src.length() - dest.length(); i >= 0; i--)
			if (src.regionMatches(true, i, dest, 0, dest.length()))
				return true;
		return false;
	}

}
