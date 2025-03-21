package game.pkg_Util.pkg_Message.options;

import game.pkg_Util.pkg_Message.Message;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class Options {
    
    public abstract Object get();

    public void before(game.pkg_Util.pkg_Message.Message message, Graphics2D graphics, Rectangle rect) {}

    public void after(Message message, Graphics2D graphics, Rectangle rect) {}
}
