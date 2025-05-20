package game.pkg_World;

import game.pkg_Loader.WorldLoader;
import game.pkg_Loader.pkg_Tiled.TiledWorldLoader;
import game.pkg_Util.FileUtils;
import game.pkg_World.pkg_Loader.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorldManager {

    private final List<WorldLoader> loaders = new ArrayList<>();
    private final List<World> worlds = new ArrayList<>();

    public WorldManager() {
        this.registerLoader(new TiledWorldLoader());

        loadWorld(new File(FileUtils.WORLD_RESOURCES + "forestWorld" + "/" + "forestWorld" + ".world"));
    }

    public List<World> getWorlds() {
        return worlds;
    }

    public World getWorld(String name) {
        for (World world : worlds) {
            if (world.getName().equals(name)) {
                return world;
            }
        }

        return null;
    }

    public void registerLoader(WorldLoader loader) {
        loaders.add(loader);
    }

    public World loadWorld(File file) {
        for (WorldLoader loader : loaders) {
            if (loader.getExtension().equals(file.getName().substring(file.getName().lastIndexOf('.') + 1))) {
                World world = loader.load(file);
                if (world != null) {
                    worlds.add(world);
                    return world;
                }
            }
        }

        return null;
    }
}
