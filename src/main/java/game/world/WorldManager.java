package game.world;

import game.loader.WorldLoader;
import game.loader.tiled.TiledWorldLoader;
import game.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorldManager {

    private final List<WorldLoader> loaders = new ArrayList<>();
    private final List<World> worlds = new ArrayList<>();

    public WorldManager() {
        this.registerLoader(new TiledWorldLoader());

        loadWorld(new File(FileUtils.WORLD_RESOURCES + "exteriorWorld" + ".tmx"));
    }

    public List<World> getWorlds() {
        return worlds;
    }

    public World getWorld(String name) {
        for (World world : worlds) {
            if (world.getName().equals(name)) {
                //world.load();
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
                System.out.println("World loaded: " + world.getName());
                if (world != null) {
                    worlds.add(world);
                    return world;
                }
            }
        }

        return null;
    }
}
