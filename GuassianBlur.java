import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.stream.IntStream;

public class GuassianBlur {

    static JButton button;
    static JFrame frame;
    static JLabel label = new JLabel();


    public static BufferedImage blur(BufferedImage image, int[] matrix, int matrixWidth) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        final int sum = IntStream.of(matrix).sum();
        // Returns an array of integer pixels in the default RGB color model
        int[] pixels_initial = image.getRGB(0, 0, width, height, null, 0, width);
        int[] pixels_final = new int[pixels_initial.length];
        final int pixelIndexOffset = width - matrixWidth;
        final int centerOffsetX = matrixWidth / 2;
        final int centerOffsetY = matrix.length / matrixWidth / 2;

        for (int h = height - matrix.length / matrixWidth + 1, w = width - matrixWidth + 1, y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int r = 0;
                int g = 0;
                int b = 0;
                for (int filterIndex = 0,
                     pixelIndex = y * width + x; filterIndex < matrix.length; pixelIndex += pixelIndexOffset) {
                    for (int fx = 0; fx < matrixWidth; fx++, pixelIndex++, filterIndex++) {
                        int col = pixels_initial[pixelIndex];
                        int factor = matrix[filterIndex];
                        r += ((col >>> 16) & 0xFF) * factor;
                        g += ((col >>> 8) & 0xFF) * factor;
                        b += (col & 0xFF) * factor;
                    }
                }
                r /= sum;
                g /= sum;
                b /= sum;
                pixels_final[x + centerOffsetX + (y + centerOffsetY) * width] = (r << 16) | (g << 8) | b | 0xFF000000;
            }
        }
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        result.setRGB(0, 0, width, height, pixels_final, 0, width);
        return result;
    }

    public static void Browse() {
        JButton button;
        JLabel label;
        frame = new JFrame();
        button = new JButton("Browse");
        button.setBounds(300, 300, 100, 40);
        label = new JLabel();
        label.setBounds(10, 10, 670, 250);
        frame.add(button);
        frame.add(label);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser file = new JFileChooser();
                file.setCurrentDirectory(new File(System.getProperty("user.home")));
                // filter the files
                FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg", "gif", "png");
                file.addChoosableFileFilter(filter);
                int result = file.showSaveDialog(null);
                // if the user click on save in Jfilechooser
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = file.getSelectedFile();
                    String path = selectedFile.getAbsolutePath();
                    BufferedImage picture = null;
                    frame.setVisible(false);
                    try {
                        picture = ImageIO.read(new File(path));
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    int[] matrix = { 1, 2, 1, 2, 4, 2, 1, 2, 1 };
                    int matrixWidth = 3;
                    BufferedImage blurred = blur(picture, matrix, matrixWidth);
                    display(blurred);
                    saveImage(blurred);
                }
                // if the user click on save in Jfilechooser

                else if (result == JFileChooser.CANCEL_OPTION) {
                    System.out.println("No File Select");
                }
            }
        });
        frame.setLayout(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(700, 400);
        frame.setVisible(true);

    }

    public static void display(BufferedImage image) {
        frame = new JFrame();
        button = new JButton("Save Image");
        button.setBounds(0, 0, 100, 40);
        frame.add(button);
        frame.setTitle("stained_image");
        frame.setSize(image.getWidth(), image.getHeight());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        label.setIcon(new ImageIcon(image));
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }


    static void saveImage(BufferedImage blurred) {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File outputfile = new File("Blurred.png");
                try {
                    ImageIO.write(blurred, "png", outputfile);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

    }


    public static void main(String[] args) {
        Browse();

    }

}
