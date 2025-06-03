package game.loader;

import game.world.World;

import java.io.File;

public interface WorldLoader extends Loader {

    String getExtension();

    World load(File file);
}
