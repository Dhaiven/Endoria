package game.pkg_Loader.pkg_Tiled;

import game.pkg_Object.Vector2;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class TiledLoaderUtils {

    public static Document createDocument(File file) {
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

    public static File resolveAbsolutePath(File callBy, String to) {
        return new File(callBy.getParent() + File.separator + to);
    }

    public static List<Element> getAllElementNode(NodeList nodeList) {
        List<Element> elements = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                elements.add((Element) item);
            }
        }

        return elements;
    }

    public static Shape loadBaseShape(Element object, Vector2 roomScale) {
        int x = (int) Float.parseFloat(object.getAttribute("x"));
        int y = (int) Float.parseFloat(object.getAttribute("y"));

        return loadShape(object, x, y, roomScale);
    }

    public static Shape loadShape(Element object, int startAtX, int startAtY, Vector2 roomScale) {
        startAtX *= roomScale.x();
        startAtY *= roomScale.y();

        List<Element> polygons = getAllElementNode(object.getElementsByTagName("polygon"));
        if (!polygons.isEmpty()) {
            Element polygonElement = polygons.get(0);
            String pointsStr = polygonElement.getAttribute("points");
            String[] pointsArray = pointsStr.split(" ");

            int[] xPoints = new int[pointsArray.length];
            int[] yPoints = new int[pointsArray.length];

            for (int i = 0; i < pointsArray.length; i++) {
                String[] point = pointsArray[i].split(",");
                int localX = (int) Float.parseFloat(point[0]);
                int localY = (int) Float.parseFloat(point[1]);

                // Convertit les coordonnées relatives en absolues
                xPoints[i] = (int) (startAtX + (localX * roomScale.x()));
                yPoints[i] = (int) (startAtY + (localY * roomScale.y()));
            }

            return new Polygon(xPoints, yPoints, pointsArray.length);
        }

        List<Element> points = getAllElementNode(object.getElementsByTagName("point"));
        if (!points.isEmpty()) {
            return new Ellipse2D.Double(startAtX, startAtY, 0, 0);
        }

        int width = (int) ((int) Float.parseFloat(object.getAttribute("width")) * roomScale.x());
        int height = (int) ((int) Float.parseFloat(object.getAttribute("height")) * roomScale.y());

        List<Element> ellipses = getAllElementNode(object.getElementsByTagName("ellipse"));
        if (!ellipses.isEmpty()) {
            return new Ellipse2D.Float(startAtX, startAtY, width, height);
        }

        return new Rectangle(startAtX, startAtY, width, height);
    }

    public static Vector2 loadVector(Element object, Vector2 roomScale) {
        return new Vector2(
                Float.parseFloat(object.getAttribute("x")),
                Float.parseFloat(object.getAttribute("y"))
        ).pow(roomScale);
    }
}
