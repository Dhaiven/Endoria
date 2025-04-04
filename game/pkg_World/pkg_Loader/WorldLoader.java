package game.pkg_World.pkg_Loader;

import game.pkg_World.World;

import java.io.File;

public interface WorldLoader {

    String getExtension();

    World load(File file);
}
