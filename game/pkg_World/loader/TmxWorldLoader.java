package game.pkg_World.loader;

import game.pkg_Entity.Sprite;
import game.pkg_Tile.Tile;
import game.pkg_Util.FileUtils;
import game.pkg_World.Layers;
import game.pkg_World.World;
import org.w3c.dom.*;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TmxWorldLoader {

    private static final String GROUND_LAYER = "ground";
    private static final String DECORATION_LAYER = "decoration";
    private static final String ENTITY_LAYER = "entity";
    private static final String DECORATION_BEHIND_ENTITY_LAYER = "decorationBehindEntity";

    private static Document createDocument(File file) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static File resolveAbsolutePath(File callBy, String to) {
        return new File(callBy.getParent() + File.separator + to);
    }

    // Méthode pour extraire les textures depuis un fichier .tsx
    private static List<Sprite> extractTexturesFromTileset(File tsxFile) {
        List<Sprite> textures = new ArrayList<>();

        try {
            Document doc = createDocument(tsxFile);

            NodeList tilesNodes = doc.getElementsByTagName("tileset");
            if (tilesNodes.getLength() <= 0) {
                return textures;
            }

            Element tileElement = (Element) tilesNodes.item(0);
            int tileWidth = Integer.parseInt(tileElement.getAttribute("tilewidth"));
            int tileHeight = Integer.parseInt(tileElement.getAttribute("tileheight"));

            // Extraction du chemin de l'image à partir de la balise <image>
            NodeList imageNodes = doc.getElementsByTagName("image");
            if (imageNodes.getLength() > 0) {
                Element imageElement = (Element) imageNodes.item(0);
                String imagePath = imageElement.getAttribute("source");

                // Résoudre également le chemin de l'image
                File imageFile = resolveAbsolutePath(tsxFile, imagePath);
                BufferedImage sourceImage = ImageIO.read(imageFile);

                int imageWidth = Integer.parseInt(imageElement.getAttribute("width"));
                int imageHeight = Integer.parseInt(imageElement.getAttribute("height"));

                // Extraire toutes les sous-images correspondant aux tuiles
                for (int y = 0; y < imageHeight; y += tileHeight) {
                    for (int x = 0; x < imageWidth; x += tileWidth) {
                        textures.add(new Sprite(sourceImage.getSubimage(x, y, tileWidth, tileHeight)));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return textures;
    }

    // Méthode pour extraire toutes les textures à partir du fichier TMX
    public static World loadWorld(String name) {
        Layers.LayersBuilder layersBuilder = Layers.builder();

        // TODO: fix layers
        World world = new World(name, Layers.builder().build());
        try {
            File file = new File(FileUtils.WORLD_RESOURCES + name + "/" + name + ".tmx");
            Document doc = createDocument(file);

            // On récupère tous les tilesets
            NodeList tileSets = doc.getElementsByTagName("tileset");

            // Liste pour stocker toutes les textures extraites
            List<Sprite> allTextures = new ArrayList<>();

            for (int i = 0; i < tileSets.getLength(); i++) {
                Node tileSetNode = tileSets.item(i);
                if (tileSetNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element tileSetElement = (Element) tileSetNode;

                    // Extraction du chemin du fichier .tsx depuis l'attribut source
                    String tsxFilePath = tileSetElement.getAttribute("source");

                    // Charger les textures du fichier .tsx référencé
                    List<Sprite> tsxTextures = extractTexturesFromTileset(resolveAbsolutePath(file, tsxFilePath));
                    allTextures.addAll(tsxTextures);
                }
            }

            // Parcourir chaque layer et extraire les ID des tuiles
            NodeList layers = doc.getElementsByTagName("layer");
            for (int i = 0; i < layers.getLength(); i++) {
                Node layerNode = layers.item(i);
                if (layerNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element layerElement = (Element) layerNode;

                    String layerName = layerElement.getAttribute("name");
                    int layerId = Integer.parseInt(layerElement.getAttribute("id"));
                    switch (layerName) {
                        case GROUND_LAYER -> layersBuilder.setGroundLayer(layerId);
                        case DECORATION_LAYER -> layersBuilder.setDecorationLayer(layerId);
                        case ENTITY_LAYER -> layersBuilder.setEntityLayer(layerId);
                        case DECORATION_BEHIND_ENTITY_LAYER -> layersBuilder.setDecorationBehindEntityLayer(layerId);
                        default -> throw new IllegalArgumentException("Layer " + layerName + " is not supported");
                    }

                    // Extraire les données du CSV
                    Node dataNode = layerElement.getElementsByTagName("data").item(0);
                    String csvData = dataNode.getTextContent().trim();

                    // Découper le CSV en éléments séparés par des virgules
                    String[] ids = csvData.split(",");

                    int width = Integer.parseInt(layerElement.getAttribute("width"));
                    int height = Integer.parseInt(layerElement.getAttribute("height"));

                    List<Tile> tiles = new ArrayList<>();
                    // Pour chaque ID de tile, on associe l'image correspondante
                    for (int row = 0; row < height; row++) {
                        for (int column = 0; column < width; column++) {
                            int tileId = Integer.parseInt(ids[row * width + column].trim());
                            if (tileId > 0) {
                                int textureIndex = tileId - 1;  // L'ID commence à 1, les index de la liste commencent à 0
                                if (textureIndex < allTextures.size()) {
                                    world.setTile(
                                            new Tile(allTextures.get(textureIndex)),
                                            row,
                                            column,
                                            layerId
                                    );
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return world;
    }
}
