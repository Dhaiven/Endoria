package game.pkg_World;

import game.pkg_Util.FileUtils;
import game.pkg_World.loader.TmxWorldLoaderV2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorldManager {

    private final List<World> worlds = new ArrayList<>();

    public WorldManager() {
        worlds.add(new TmxWorldLoaderV2().loadWorld(
                new File(FileUtils.WORLD_RESOURCES + "museum" + "/" + "museum" + ".world")
        ));
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
