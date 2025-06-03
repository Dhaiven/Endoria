package game.loader.tiled;

import game.image.AnimatedSprite;
import game.image.Sprite;
import game.image.StaticSprite;
import game.loader.Loader;
import game.object.Identifier;
import game.object.Vector2;
import game.tile.Collision;
import game.tile.CollisionType;
import game.tile.Tile;
import game.tile.property.TileProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static game.loader.tiled.TiledLoaderUtils.*;
import static game.loader.LoaderUtils.*;

public class TiledTileLoader implements Loader {

    private final Vector2 scale;
    private final List<Sprite> sprites = new ArrayList<>();

    public TiledTileLoader(Vector2 scale) {
        this.scale = scale;
    }

    @Override
    public String getExtension() {
        return "tsx";
    }

    // Méthode pour extraire les textures depuis un fichier .tsx
    public Map<Integer, Tile> load(File tsxFile) throws IOException, IllegalAccessException {
        if (!tsxFile.getName().endsWith(getExtension())) {
            throw new IllegalArgumentException("Ffile does not end with .tsx");
        }

        Map<Integer, Tile> textures = new HashMap<>();

        String fileName = tsxFile.getName().replace("." + getExtension(), "");

        Document doc = createDocument(tsxFile);
        for (Element tileElement : getAllElementNode(doc.getElementsByTagName("tileset"))) {
            int tileWidth = Integer.parseInt(tileElement.getAttribute("tilewidth"));
            int tileHeight = Integer.parseInt(tileElement.getAttribute("tileheight"));

            Map<Integer, Element> tileInfos = new HashMap<>();
            for (Element tileInfo : getAllElementNode(tileElement.getElementsByTagName("tile"))) {
                tileInfos.put(Integer.parseInt(tileInfo.getAttribute("id")), tileInfo);
            }

            // Extraction du chemin de l'image à partir de la balise <image>
            for (Element imageElement : getAllElementNode(tileElement.getElementsByTagName("image"))) {
                String imagePath = imageElement.getAttribute("source");

                // Résoudre également le chemin de l'image
                File imageFile = resolveAbsolutePath(tsxFile, imagePath);
                BufferedImage sourceImage = ImageIO.read(imageFile);

                int imageWidth = Integer.parseInt(imageElement.getAttribute("width"));
                int imageHeight = Integer.parseInt(imageElement.getAttribute("height"));

                // Extraire toutes les sous-images correspondant aux tuiles
                for (int y = 0; y < imageHeight; y += tileHeight) {
                    for (int x = 0; x < imageWidth; x += tileWidth) {
                        this.sprites.add(new StaticSprite(sourceImage.getSubimage(x, y, tileWidth, tileHeight)));
                    }
                }

                // Extraire toutes les tiles
                int id = 0;
                for (int y = 0; y < imageHeight; y += tileHeight) {
                    for (int x = 0; x < imageWidth; x += tileWidth) {
                        textures.put(id, loadTile(
                                new Identifier(fileName, id),
                                tileInfos.get(id)));
                        id++;
                    }
                }
            }
        }

        return textures;
    }

    private Tile loadTile(Identifier id) {
        return new Tile(id, this.sprites.get(id.id()));
    }

    private Tile loadTile(Identifier id, Element info) throws IllegalAccessException {
        if (info == null) {
            return loadTile(id);
        }

        // Collision
        List<Collision> collisions = new ArrayList<>();
        for (Element objectGroup : getAllElementNode(info.getElementsByTagName("objectgroup"))) {
            for (Element object : getAllElementNode(objectGroup.getElementsByTagName("object"))) {
                var type = findEnumProperty(object, CollisionType.class);
                if (type == null) type = CollisionType.IF_SAME_LAYER;
                collisions.add(new Collision(
                        loadBaseShape(object, this.scale),
                        type
                ));
            }
        }

        Sprite sprite;
        // Animations
        NodeList animations = info.getElementsByTagName("animation");
        if (animations.getLength() != 0) {
            Element animation = (Element) animations.item(0);

            List<Map.Entry<Sprite, Double>> sprites = new ArrayList<>();
            for (Element frame : getAllElementNode(animation.getElementsByTagName("frame"))) {
                int spriteTileId = Integer.parseInt(frame.getAttribute("tileid"));

                sprites.add(new AbstractMap.SimpleEntry<>(
                        this.sprites.get(spriteTileId),
                        Double.parseDouble(frame.getAttribute("duration"))
                ));
            }

            sprite = new AnimatedSprite(sprites);
        } else {
            sprite = this.sprites.get(id.id());
        }

        // Tiles properties
        List<TileProperty.TilePropertyValue<?, ?, ?>> properties = new ArrayList<>();
        for (var propertiesElement : getAllElementNode(info.getElementsByTagName("properties"))) {
            for (var propertyElement : getAllElementNode(propertiesElement.getElementsByTagName("property"))) {
                var tilePropertyValue = loadTileProperty(
                        propertyElement.getAttribute("name"),
                        propertyElement.getAttribute("value")
                );
                if (tilePropertyValue != null) {
                    properties.add(tilePropertyValue);
                }
            }
        }

        return new Tile(
                id,
                sprite,
                collisions.toArray(new Collision[0]),
                properties
        );
    }

    private static <T extends Enum<T>> T findEnumProperty(Element element, Class<T> clazz) {
        for (var propertiesElement : getAllElementNode(element.getElementsByTagName("properties"))) {
            for (var propertyElement : getAllElementNode(propertiesElement.getElementsByTagName("property"))) {
                var type = propertyElement.getAttribute("propertytype");
                if (type.equalsIgnoreCase(clazz.getSimpleName())) {
                    return loadEnum(propertyElement.getAttribute("value"), clazz);
                }
            }
        }

        return null;
    }
}

