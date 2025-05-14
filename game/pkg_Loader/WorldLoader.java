package game.pkg_Loader;

import game.pkg_World.World;

import java.io.File;

public interface WorldLoader extends Loader {

    String getExtension();

    World load(File file);
}
