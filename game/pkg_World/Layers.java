package game.pkg_World;

import java.util.ArrayList;
import java.util.List;

public class Layers {

    private final int groundLayer;
    private final int decorationLayer;
    private final int entityLayer;
    private final int decorationBehindEntityLayer;

    public static LayersBuilder builder() {
        return new LayersBuilder();
    }

    private Layers(
            int groundLayer,
            int decorationLayer,
            int entityLayer,
            int decorationBehindEntityLayer
    ) {
        this.groundLayer = groundLayer;
        this.decorationLayer = decorationLayer;
        this.entityLayer = entityLayer;
        this.decorationBehindEntityLayer = decorationBehindEntityLayer;
    }

    public List<Integer> allSorted() {
        List<Integer> list = new ArrayList<>();
        list.add(groundLayer);
        list.add(decorationLayer);
        list.add(entityLayer);
        list.add(decorationBehindEntityLayer);

        return list.stream().sorted().toList();
    }

    public int size() {
        return allSorted().size();
    }

    public int getGroundLayer() {
        return groundLayer;
    }

    public int getDecorationLayer() {
        return decorationLayer;
    }

    public int getEntityLayer() {
        return entityLayer;
    }

    public int getDecorationBehindEntityLayer() {
        return decorationBehindEntityLayer;
    }

    public static class LayersBuilder {
        private int groundLayer;
        private int decorationLayer;
        private int entityLayer;
        private int decorationBehindEntityLayer;

        public LayersBuilder setGroundLayer(int groundLayer) {
            this.groundLayer = groundLayer;
            return this;
        }

        public LayersBuilder setDecorationLayer(int decorationLayer) {
            this.decorationLayer = decorationLayer;
            return this;
        }

        public LayersBuilder setEntityLayer(int entityLayer) {
            this.entityLayer = entityLayer;
            return this;
        }

        public LayersBuilder setDecorationBehindEntityLayer(int decorationBehindEntityLayer) {
            this.decorationBehindEntityLayer = decorationBehindEntityLayer;
            return this;
        }
        
        public Layers build() {
            return new Layers(
                    groundLayer,
                    decorationLayer,
                    entityLayer,
                    decorationBehindEntityLayer
            );
        }
    }
}
