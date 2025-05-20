package game.pkg_Loader.pkg_Tiled;

import game.pkg_Image.AnimatedSprite;
import game.pkg_Image.Sprite;
import game.pkg_Image.StaticSprite;
import game.pkg_Loader.Loader;
import game.pkg_Object.DrawType;
import game.pkg_Object.Vector2;
import game.pkg_Tile.CollisionType;
import game.pkg_Tile.Tile;
import game.pkg_Tile.TileProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;

import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static game.pkg_Loader.pkg_Tiled.TiledLoaderUtils.*;
import static game.pkg_Loader.LoaderUtils.*;

public class TiledTileLoader implements Loader {

    private final int firstId;
    private final Vector2 roomScale;
    private final List<Sprite> sprites = new ArrayList<>();

    public TiledTileLoader(final int firstId, final Vector2 roomScale) {
        this.firstId = firstId;
        this.roomScale = roomScale;
    }

    @Override
    public String getExtension() {
        return "tsx";
    }

    // Méthode pour extraire les textures depuis un fichier .tsx
    public Map<Integer, Tile> load(File tsxFile) throws IOException {
        if (!tsxFile.getName().endsWith(getExtension())) {
            throw new IllegalArgumentException("Ffile does not end with .tsx");
        }

        Map<Integer, Tile> textures = new HashMap<>();

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
                        textures.put(id, loadTile(id, tileInfos.get(id)));
                        id++;
                    }
                }
            }
        }

        return textures;
    }

    private Tile loadTile(int id) {
        return new Tile(firstId + id, this.sprites.get(id));
    }

    private Tile loadTile(int id, Element info) {
        if (info == null) {
            return loadTile(id);
        }

        // Collision
        List<Shape> collisions = new ArrayList<>();
        for (Element objectGroup : getAllElementNode(info.getElementsByTagName("objectgroup"))) {
            for (Element object : getAllElementNode(objectGroup.getElementsByTagName("object"))) {
                collisions.add(loadBaseShape(object, this.roomScale));
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
            sprite = this.sprites.get(id);
        }

        // Tiles properties
        CollisionType collisionType = CollisionType.IF_SAME_LAYER;
        DrawType drawType = DrawType.UNDER;
        List<TileProperty> properties = new ArrayList<>();
        for (var propertiesElement : getAllElementNode(info.getElementsByTagName("properties"))) {
            for (var propertyElement : getAllElementNode(propertiesElement.getElementsByTagName("property"))) {
                String name = propertyElement.getAttribute("name");

                TileProperty tileProperty = loadTileProperty(name);
                if (tileProperty != null) {
                    properties.add(tileProperty);
                } else {
                    switch (propertyElement.getAttribute("propertytype")) {
                        case "Collision" -> collisionType = loadEnum(propertyElement.getAttribute("value"), CollisionType.class);
                        case "DrawType" -> drawType = loadEnum(propertyElement.getAttribute("value"), DrawType.class);
                    }
                }
            }
        }

        return new Tile(
                firstId + id,
                sprite,
                collisionType,
                collisions.toArray(new Shape[0]),
                properties,
                drawType
        );
    }
}

