package org.sturgeon.light.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.sturgeon.light.Box;
import org.sturgeon.light.Box2dLightTest;
import org.sturgeon.light.BoxBox;
import org.sturgeon.light.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new Box(), config);
	}
}
