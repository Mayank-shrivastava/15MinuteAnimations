package main;

import org.opencv.core.Core;

import characterCreation.CharacterCreationMenuWindow;

public class Main {
	public static void main(String[] args) {
		loadLibraries();
		new CharacterCreationMenuWindow();
	}
	
	public static void loadLibraries() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
}
