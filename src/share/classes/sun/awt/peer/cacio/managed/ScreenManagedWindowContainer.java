package sun.awt.peer.cacio.managed;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.util.List;

class ScreenManagedWindowContainer extends AbstractManagedWindowContainer {

    private PlatformScreen screen;

    ScreenManagedWindowContainer(PlatformScreen ps) {
        screen = ps;
    }

    @Override
    public Graphics2D getClippedGraphics(Color fg, Color bg, Font font,
                                         List<Rectangle> clipRects) {
        return screen.getClippedGraphics(fg, bg, font, clipRects);
    }

    @Override
    public ColorModel getColorModel() {
        return screen.getColorModel();
    }

    @Override
    public GraphicsConfiguration getGraphicsConfiguration() {
        return screen.getGraphicsConfiguration();
    }

}
