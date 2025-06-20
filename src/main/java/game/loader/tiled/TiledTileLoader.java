package game.loader.tiled;

import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.sun.javafx.scene.traversal.SubSceneTraversalEngine;
import game.Season;
import game.loader.Loader;
import game.object.Identifier;
import game.object.Vector2;
import game.tile.Collision;
import game.tile.CollisionType;
import game.tile.Tile;
import game.tile.property.TileProperty;
import game.util.Utils;
import javafx.scene.image.WritableImage;
import javafx.util.Duration;
import kotlin.coroutines.jvm.internal.SuspendFunction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;

import static game.loader.tiled.TiledLoaderUtils.*;
import static game.loader.LoaderUtils.*;

public class TiledTileLoader implements Loader {

    private final Vector2 scale;
    private final Map<Integer, Map<Season, WritableImage>> sprites = new HashMap<>();

    public TiledTileLoader(Vector2 scale) {
        this.scale = scale;
    }

    @Override
    public String getExtension() {
        return "tsx";
    }

    // Méthode pour extraire les textures depuis un fichier .tsx
    public Map<Integer, SpawnData> load(File tsxFile) throws IOException {
        if (!tsxFile.getName().endsWith(getExtension())) {
            throw new IllegalArgumentException("Ffile does not end with .tsx");
        }

        Map<Integer, SpawnData> textures = new HashMap<>();

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

                Map<Season, BufferedImage> imageBaseBySeason = new HashMap<>();
                for (Season season : Season.values()) {
                    var newPath = imageFile.toString().replace("forest", season.getType());
                    File file = new File(newPath);
                    if (!file.exists()) {
                        file = imageFile;
                    }

                    imageBaseBySeason.put(season, ImageIO.read(file));
                }

                int imageWidth = Integer.parseInt(imageElement.getAttribute("width"));
                int imageHeight = Integer.parseInt(imageElement.getAttribute("height"));

                int id = 0;
                for (int y = 0; y < imageHeight; y += tileHeight) {
                    for (int x = 0; x < imageWidth; x += tileWidth) {
                        Map<Season, WritableImage> subImageBySeason = new HashMap<>();
                        for (Season season : Season.values()) {
                            var bufferedImage = imageBaseBySeason.get(season);
                            BufferedImage subImage = bufferedImage.getSubimage(x, y, tileWidth, tileHeight);
                            var scaleImage = subImage.getScaledInstance(
                                    Utils.TEXTURE_SIZE.x(),
                                    Utils.TEXTURE_SIZE.y(),
                                    Image.SCALE_DEFAULT
                            );

                            BufferedImage finalImage = new BufferedImage(
                                    scaleImage.getWidth(null),
                                    scaleImage.getHeight(null),
                                    BufferedImage.TYPE_INT_ARGB
                            );

                            // Dessine l'image dans le BufferedImage
                            Graphics2D g2d = finalImage.createGraphics();
                            g2d.drawImage(scaleImage, 0, 0, null);
                            g2d.dispose();

                            subImageBySeason.put(season, javafx.embed.swing.SwingFXUtils.toFXImage(finalImage, null));
                        }

                        sprites.put(id, subImageBySeason);
                        id++;
                    }
                }

                // Extraire toutes les tiles
                id = 0;
                for (int y = 0; y < imageHeight; y += tileHeight) {
                    for (int x = 0; x < imageWidth; x += tileWidth) {
                        /**textures.put(id, loadTile(
                                new Identifier(fileName, id),
                                tileInfos.get(id)));*/


                        Map<Season, Supplier<Texture>> seasonalTextures = new HashMap<>();
                        for (Season season : Season.values()) {
                            seasonalTextures.put(season, getSupplierTexture(tileInfos.get(id), id, season));
                        }

                        SpawnData data = new SpawnData();
                        data.put("seasonalTextures", seasonalTextures);

                        textures.put(id, data);
                        id++;
                    }
                }
            }
        }

        return textures;
    }

    private Tile loadTile(Identifier id, Element info) throws IllegalAccessException {
        if (info == null) {
            return null;
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

        return null;
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

    private Supplier<Texture> getSupplierTexture(Element info, int id, Season season) {
        Supplier<Texture> result = () -> new Texture(this.sprites.get(id).get(season));
        if (info == null) {
            return result;
        }

        NodeList animations = info.getElementsByTagName("animation");
        if (animations.getLength() != 0) {
            Element animation = (Element) animations.item(0);

            List<WritableImage> sprites = new ArrayList<>();
            Duration duration = Duration.ZERO;
            for (Element frame : getAllElementNode(animation.getElementsByTagName("frame"))) {
                int spriteTileId = Integer.parseInt(frame.getAttribute("tileid"));

                sprites.add(this.sprites.get(spriteTileId).get(season));
                duration = Duration.millis(Long.parseLong(frame.getAttribute("duration")));
            }

            Duration finalDuration = duration;
            return () -> {
                AnimatedTexture texture = new AnimatedTexture(new AnimationChannel(sprites, finalDuration));
                texture.loop();
                return texture;
            };
        }

        return result;
    }
}

