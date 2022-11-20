package utils;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.ArrayList;

public class FileService {
    private static final double rPerc = 0.2;
    private static final double gPerc = 0.6;
    private static final double bPerc = 0.2;


    public static void openImage(File image) throws IOException {
        BufferedImage bufImage = imageToBuffered(image);
        //Instantiate JFrame
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout(FlowLayout.CENTER));
        JSlider jSlider = resizeSlider();
        JLabel shownImage = new JLabel(new ImageIcon(bufImage));
        //Set Content to the JFrame
        frame.getContentPane().add(jSlider);
        frame.getContentPane().add(shownImage);
        frame.pack();
        frame.setVisible(true);
        System.out.println("Image Loaded");
        jSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                System.out.println("Slider1: " + jSlider.getValue());
              /*  try {
                   bufImage =  ImageOperations.resizeImage(bufImage, jSlider.getValue());
                } catch (IOException ex) {
                    ex.printStackTrace();
                } */
            }
        });
    }

    public static void openImage(BufferedImage image) throws IOException {
        //Instantiate JFrame
        JFrame frame = new JFrame();
        //Set Content to the JFrame
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setVisible(true);
        System.out.println("Image Loaded");
    }

    public static BufferedImage imageToBuffered(File image) throws IOException {
        String path = image.getAbsolutePath();
        Mat img = Imgcodecs.imread(path);
        //Encoding the image
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", img, matOfByte);
        //Storing the encoded Mat in a byte array
        byte[] byteArray = matOfByte.toArray();
        //Preparing the Buffered Image
        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage bufImage = ImageIO.read(in);
        return bufImage;
    }

    public static Mat BufferedImage2Mat(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_LOAD_GDAL);
    }

    public static void saveFile(File image) throws IOException {
        Mat matrix = Imgcodecs.imread(image.getAbsolutePath());
        //Chooser to save file
        JFrame parentFrame = new JFrame();
        JFileChooser fileChooser = new JFileChooser("src/main/resources");
        fileChooser.setDialogTitle("Specify a file to save");

        int userSelection = fileChooser.showSaveDialog(parentFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            System.out.println("Save as file: " + fileToSave.getAbsolutePath());
            String file2 = fileToSave.getAbsolutePath();

            //Writing the image
            Imgcodecs.imwrite(file2, matrix);
            System.out.println("Image saved");
        }
    }

    public static BufferedImage matToBuffered(Mat matrix) throws IOException {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, matOfByte);
        //Storing the encoded Mat in a byte array
        byte[] byteArray = matOfByte.toArray();
        //Preparing the Buffered Image
        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage bufImage = ImageIO.read(in);
        return bufImage;
    }

    public static ArrayList<double[]> tableLUT(File image){
        Mat pix = Imgcodecs.imread(image.getAbsolutePath());
        return calculateLUT(pix);
    }

    public static ArrayList<double[]> tableLUT(Mat image){
        Mat pix = image;
        return calculateLUT(pix);
    }

    private static ArrayList<double[]> calculateLUT(Mat pix) {
        int ch = pix.channels();
        int rows = pix.rows();
        int cols = pix.cols();
        System.out.println("Number of channels: " + ch);
        double[] k = new double[256];
        double[] r = new double[256];
        double[] g = new double[256];
        double[] b = new double[256];
        int i;
        int j;
        double[] data;
        if (ch > 1) {
            for(i = 0; i < rows; ++i) {
                for(j = 0; j < cols; ++j) {
                    data = pix.get(i, j);
                    ++r[(int)data[2]];
                    ++g[(int)data[1]];
                    ++b[(int)data[0]];
                    double k_value = (data[0] + data[1] + data[2]) / 3.0D;
                    ++k[(int)k_value];
                }
            }
        } else {
            for(i = 0; i < rows; ++i) {
                for(j = 0; j < cols; ++j) {
                    data = pix.get(i, j);
                    ++k[(int)data[0]];
                }
            }
        }

        ArrayList<double[]> rgb = new ArrayList();
        rgb.add(k);
        if (ch > 1) {
            rgb.add(r);
            rgb.add(g);
            rgb.add(b);
        }

        return rgb;
    }

    public static ArrayList<int[]> intTableLUT(File image){
        Mat pix = Imgcodecs.imread(image.getAbsolutePath());
        int ch = pix.channels();
        int rows = pix.rows();
        int cols = pix.cols();
        System.out.println("Number of channels: " + ch);
        int[] k = new int[256];
        int[] r = new int[256];
        int[] g = new int[256];
        int[] b = new int[256];
        int i;
        int j;
        double[] data;
        if (ch > 1) {
            for(i = 0; i < rows; ++i) {
                for(j = 0; j < cols; ++j) {
                    data = pix.get(i, j);
                    ++r[(int)data[2]];
                    ++g[(int)data[1]];
                    ++b[(int)data[0]];
                    double k_value = (data[0] + data[1] + data[2]) / 3.0D;
                    ++k[(int)k_value];
                }
            }
        } else {
            for(i = 0; i < rows; ++i) {
                for(j = 0; j < cols; ++j) {
                    data = pix.get(i, j);
                    ++k[(int)data[0]];
                }
            }
        }

        ArrayList<int[]> rgb = new ArrayList();
        rgb.add(k);
        if (ch > 1) {
            rgb.add(r);
            rgb.add(g);
            rgb.add(b);
        }

        return rgb;
    }


    public static JSlider thresholdSlider(){
        JSlider jSlider = new JSlider(JSlider.HORIZONTAL, 0, 256, 0);
        return getSlider(jSlider);
    }

    private static JSlider getSlider(JSlider jSlider) {
        jSlider.setPaintTicks(true);
        jSlider.setMajorTickSpacing(25);
        jSlider.setMinorTickSpacing(5);
        jSlider.setPaintTicks(true);
        jSlider.setPaintLabels(true);
        jSlider.setBorder(
                BorderFactory.createEmptyBorder(0,0,256,0));
        Font font = new Font("Serif", Font.ITALIC, 15);
        jSlider.setFont(font);
        return jSlider;
    }

    public static JSlider resizeSlider(){
        JSlider jSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        return getSlider(jSlider);
    }

    public static BufferedImage rgbToGrayscale(BufferedImage bufferedImage){
        int channels = bufferedImage.getColorModel().getNumComponents();
        if (channels==1) return bufferedImage;

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        BufferedImage grayscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        final byte[] a = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        final byte[] gray = ((DataBufferByte) grayscaleImage.getRaster().getDataBuffer()).getData();
        for (int p = 0; p < width * height * channels; p += channels) {
            double r = a[p+2] & 0xFF;
            double g = a[p+1] & 0xFF;
            double b = a[p] & 0xFF;
            gray[p/channels] = (byte) Math.round((r * rPerc) + (g * gPerc) + (b * bPerc));

        }
        return grayscaleImage;
    }

    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static BufferedImage toBufferedImage(Image img){
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

}
