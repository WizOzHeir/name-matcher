package com.homeproject.namematcher.util;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Checker {
	public static final Character[] INVALID_WINDOWS_SPECIFIC_CHARS =
		{'"', '*', ':', '<', '>', '?', '\\', '|', 0x7F};
	public static final Character[] INVALID_UNIX_SPECIFIC_CHARS = {'\000'};
	public static final Integer MIN_INPUTNAME_LEN = 2;
	
	public static boolean isCorrectFileName(String fileName) {
		if (fileName == null || fileName.isEmpty() || fileName.length() > 255) {
			throw new IllegalArgumentException("Invalid directory name");
	    }
	    return Arrays.stream(getInvalidCharsByOS())
	      .noneMatch(ch -> fileName.contains(ch.toString()));
	}
	
	public static boolean isCorrectInputName(String inputName) {
		if(inputName == null || inputName.length() == 0 || inputName.length() > 255) {
			throw new IllegalArgumentException("Invalid input");
		}
		Pattern pattern = Pattern.compile("^[(?i)a-z ,.'-]+", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputName);
		inputName = inputName.strip();
		if(inputName.length() < MIN_INPUTNAME_LEN) {
			throw new IllegalArgumentException(String.format(
					"Must be at least %d characters in the name", MIN_INPUTNAME_LEN));
		}
		if(!matcher.matches()) {
			throw new IllegalArgumentException("Invalid input");
		}
		return true;
	}
	
	public static Character[] getInvalidCharsByOS() {
	    String os = System.getProperty("os.name").toLowerCase();
	    if (os.contains("win")) {
	        return INVALID_WINDOWS_SPECIFIC_CHARS;
	    } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
	        return INVALID_UNIX_SPECIFIC_CHARS;
	    } else {
	        return new Character[]{};
	    }
	}
	
	public static boolean isGoodScore(double a, double b) {
		if(a == b) return true;
		return ((Math.max(a, b) - Math.min(a, b)) / Math.max(a, b) * 100) < 25;
	}
}
