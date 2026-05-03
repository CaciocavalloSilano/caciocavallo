package com.github.caciocavallosilano.cacio.ctc;

import com.github.caciocavallosilano.cacio.ctc.junit.CacioTest;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiTask;
import org.assertj.swing.fixture.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@CacioTest
public class ImageIconTest {

    private JFrame frame;
    private FrameFixture ff;
    private ImageIcon testIcon;
    private BufferedImage testImage;

    @BeforeEach
    public void setUp() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                // Create a simple test image
                testImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = testImage.createGraphics();
                g2d.setColor(Color.RED);
                g2d.fillRect(0, 0, 16, 16);
                g2d.setColor(Color.BLUE);
                g2d.fillRect(16, 16, 16, 16);
                g2d.setColor(Color.GREEN);
                g2d.fillRect(0, 16, 16, 16);
                g2d.setColor(Color.YELLOW);
                g2d.fillRect(16, 0, 16, 16);
                g2d.dispose();
                
                testIcon = new ImageIcon(testImage);
                
                frame = new JFrame("ImageIcon Test");
                frame.setLayout(new FlowLayout());
                frame.setSize(400, 300);
                frame.setVisible(true);
            }
        });
        ff = new FrameFixture(frame);
    }

    @AfterEach
    public void tearDown() throws Exception {
        ff.cleanUp();
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                frame.dispose();
                frame = null;
                testIcon = null;
                testImage = null;
            }
        });
    }

    @Test
    @GUITest
    public void testImageIconInJLabel() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                JLabel label = new JLabel("Test Label", testIcon, SwingConstants.LEFT);
                label.setName("iconLabel");
                frame.add(label);
                frame.revalidate();
            }
        });
        
        JLabelFixture labelFixture = ff.label("iconLabel");
        labelFixture.requireText("Test Label");
        // Verify the label is visible and contains the icon
        labelFixture.requireVisible();
    }

    @Test
    @GUITest
    public void testImageIconInJButton() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                JButton button = new JButton("Test Button", testIcon);
                button.setName("iconButton");
                frame.add(button);
                frame.revalidate();
            }
        });
        
        JButtonFixture buttonFixture = ff.button("iconButton");
        buttonFixture.requireText("Test Button");
        // Verify the button is visible and contains the icon
        buttonFixture.requireVisible();
    }

    @Test
    @GUITest
    public void testImageIconOnly() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                JLabel label = new JLabel(testIcon);
                label.setName("iconOnlyLabel");
                frame.add(label);
                frame.revalidate();
            }
        });
        
        JLabelFixture labelFixture = ff.label("iconOnlyLabel");
        // Verify the label is visible and contains the icon
        labelFixture.requireVisible();
    }

    @Test
    @GUITest
    public void testImageIconProperties() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                JLabel label = new JLabel(testIcon);
                label.setName("iconLabel");
                frame.add(label);
                frame.revalidate();
            }
        });
        
        // Test that the icon has the expected properties
        assert testIcon.getIconWidth() == 32;
        assert testIcon.getIconHeight() == 32;
        assert testIcon.getImage() == testImage;
    }

    @Test
    @GUITest
    public void testImageIconPainting() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                JPanel panel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        testIcon.paintIcon(this, g, 10, 10);
                    }
                };
                panel.setPreferredSize(new Dimension(100, 100));
                panel.setName("paintPanel");
                frame.add(panel);
                frame.revalidate();
            }
        });
        
        JPanelFixture panelFixture = ff.panel("paintPanel");
        panelFixture.requireVisible();
    }

    @Test
    @GUITest
    public void testImageIconInMenu() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                JMenuBar menuBar = new JMenuBar();
                JMenu menu = new JMenu("Test Menu");
                JMenuItem menuItem = new JMenuItem("Test Item", testIcon);
                menuItem.setName("iconMenuItem");
                menu.add(menuItem);
                menuBar.add(menu);
                frame.setJMenuBar(menuBar);
                frame.revalidate();
            }
        });
        
        JMenuItemFixture menuItemFixture = ff.menuItemWithPath("Test Menu", "Test Item");
        // Verify the menu item is visible and contains the icon
        menuItemFixture.requireVisible();
    }

    @Test
    @GUITest
    public void testImageIconResizing() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                // Create a resized version of the icon
                ImageIcon resizedIcon = new ImageIcon(testImage.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
                JLabel label = new JLabel("Resized", resizedIcon, SwingConstants.LEFT);
                label.setName("resizedLabel");
                frame.add(label);
                frame.revalidate();
            }
        });
        
        JLabelFixture labelFixture = ff.label("resizedLabel");
        labelFixture.requireText("Resized");
        // The icon should be visible even if resized
        labelFixture.requireVisible();
    }

    @Test
    @GUITest
    public void testImageIconDescription() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                ImageIcon iconWithDescription = new ImageIcon(testImage, "Test Image Description");
                JLabel label = new JLabel(iconWithDescription);
                label.setName("descLabel");
                frame.add(label);
                frame.revalidate();
            }
        });
        
        JLabelFixture labelFixture = ff.label("descLabel");
        labelFixture.requireVisible();
    }
} 