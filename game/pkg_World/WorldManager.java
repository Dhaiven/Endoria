package game.pkg_World;

import game.pkg_World.loader.TmxWorldLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorldManager {

    private final List<World> worlds = new ArrayList<>();

    public WorldManager() {
        worlds.add(TmxWorldLoader.loadWorld("museum"));
    }

    public List<World> getWorlds() {
        return worlds;
    }

    public World getWorld(String name) {
        for (World world : worlds) {
            if (Objects.equals(world.getName(), name)) {
                return world;
            }
        }

        return null;
    }
}
