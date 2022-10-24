package utils;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FileService {


    public static void openImage(File image) throws IOException {
        BufferedImage bufImage = imageToBuffered(image);
        //Instantiate JFrame
        JFrame frame = new JFrame();
        //Set Content to the JFrame
        frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
        frame.pack();
        frame.setVisible(true);
        System.out.println("Image Loaded");
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

    public static ArrayList<int[]> tableLUT(File image){
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


}
