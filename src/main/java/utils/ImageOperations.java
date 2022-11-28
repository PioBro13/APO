package utils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import static utils.FileService.matToBuffered;
import static utils.FileService.openImage;

public class ImageOperations {

    public static BufferedImage negation(BufferedImage image) throws IOException {
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer())
                .getData();

        // Create a Matrix the same size of image
        Mat matrix = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        matrix.put(0,0,pixels);
        double maxVal = 255.0D;
        int rows = matrix.rows();
        int cols = matrix.cols();

        for(int i = 0; i < rows; ++i) {
            for(int j = 0; j < cols; ++j) {
                double[] data = matrix.get(i, j);
                data[0] = maxVal - data[0];
                data[1] = maxVal - data[1];
                data[2] = maxVal - data[2];
                matrix.put(i, j, data);
            }
        }
        return (matToBuffered(matrix));
    }

    public static void threshold(Mat src, Mat dst, double threshold, double maxval, int type) {
        Mat img = src;
        Mat result = dst;

        for(int i = 0; i < maxval; ++i) {
            for(int j = 0; j < maxval; ++j) {
                double[] data = img.get(i, j);
                if (type == 0) {
                    data[0] = data[0] > threshold ? data[0] : 0.0D;
                    data[1] = data[1] > threshold ? data[1] : 0.0D;
                    data[2] = data[2] > threshold ? data[2] : 0.0D;
                } if (type == 1) {
                    data[0] = data[0] > threshold ? 255.0D : 0.0D;
                    data[1] = data[1] > threshold ? 255.0D : 0.0D;
                    data[2] = data[2] > threshold ? 255.0D : 0.0D;
                } if(type ==2){
                    data[0] = Math.min(data[0], threshold);
                    data[1] = Math.min(data[1], threshold);
                    data[2] = Math.min(data[2], threshold);
                } if (type == 3){
                    data[0] = data[0] < threshold ? data[0] : 0.0D;
                    data[1] = data[1] < threshold ? data[1] : 0.0D;
                    data[2] = data[2] < threshold ? data[2] : 0.0D;
                } if (type == 4){
                    data[0] = data[0] < threshold ? data[0] : 255.0D;
                    data[1] = data[1] < threshold ? data[1] : 255.0D;
                    data[2] = data[2] < threshold ? data[2] : 255.0D;
                }
                result.put(i, j, data);
            }
        }
    }

    public static BufferedImage resizeImage(BufferedImage image, int sizeChange) throws IOException {
        Mat source = FileService.BufferedImage2Mat(image);
        Mat resized = new Mat();
        Size size = new Size(sizeChange,sizeChange);
        Imgproc.resize(source,resized,size);
        return FileService.matToBuffered(resized);

    }

    public static BufferedImage smoothingGaussian(File image){
        Mat src = Imgcodecs.imread(image.getAbsolutePath());
        //Creating destination matrix
        Mat dst = new Mat(src.rows(), src.cols(), src.type());
        //Applying GaussianBlur on the Image
        Imgproc.GaussianBlur(src, dst, new Size(15, 15), 0);
        //Converting matrix to JavaFX writable image
        Image img = HighGui.toBufferedImage(dst);


        return FileService.toBufferedImage(img);
    }

    public static BufferedImage smoothingMedian(File image){
        Mat src = Imgcodecs.imread(image.getAbsolutePath());
        //Creating destination matrix
        Mat dst = new Mat(src.rows(), src.cols(), src.type());
        // Applying MedianBlur on the Image
        Imgproc.medianBlur(src, dst, 5);
        //Converting matrix to JavaFX writable image
        Image img = HighGui.toBufferedImage(dst);

        return FileService.toBufferedImage(img);
    }

    public static BufferedImage arithmeticOperation(File image, String operation, int value) throws IOException {
        BufferedImage buffImage = FileService.imageToBuffered(image);
        BufferedImage result = new BufferedImage(buffImage.getWidth(),buffImage.getHeight(),buffImage.getType());
        for(int x = 0; x < buffImage.getWidth(); x++){
            for(int y = 0; y < buffImage.getHeight(); y++) {
                int argb = buffImage.getRGB(x,y);
                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >>  8) & 0xFF;
                int b = (argb      ) & 0xFF;
                int aDiff = a;
                int rDiff = b;
                int gDiff = g;
                int bDiff = b;

                switch (operation) {
                    case "Addition" -> {
                        aDiff = (a + value)%255;
                        rDiff = (r + value)%255;
                        gDiff = (g + value)%255;
                        bDiff = (b + value)%255;
                    }
                    case "Division" -> {
                        aDiff = (a / value)%255;
                        rDiff = (r / value)%255;
                        gDiff = (g / value)%255;
                        bDiff = (b / value)%255;
                    }
                    case "Multiplication" -> {
                        aDiff = (a * value)%255;
                        rDiff = (r * value)%255;
                        gDiff = (g * value)%255;
                        bDiff = (b * value)%255;
                    }
                }
                int diff =
                        (aDiff << 24) | (rDiff << 16) | (gDiff << 8) | bDiff;
                result.setRGB(x, y, diff);

            }
        }

        return result;
    }

    public static BufferedImage mergeImages(File firstImage, File secondImage) throws IOException {
        BufferedImage image = FileService.imageToBuffered(firstImage);
        BufferedImage overlay = FileService.imageToBuffered(secondImage);

        // create the new image, canvas size is the max. of both image sizes
        int w = Math.max(image.getWidth(), overlay.getWidth());
        int h = Math.max(image.getHeight(), overlay.getHeight());
        BufferedImage combined = new BufferedImage(w, h, image.getType());

// paint both images, preserving the alpha channels
        Graphics g = combined.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.drawImage(overlay, 0, 0, null);

        g.dispose();

// Save as new image
         return  combined;

    }

    public static BufferedImage substractImages(File firstImage, File secondImage) throws IOException {
        BufferedImage image0 = FileService.imageToBuffered(firstImage);
        BufferedImage image1 = FileService.imageToBuffered(secondImage);
        BufferedImage result = new BufferedImage(image0.getWidth(),image0.getHeight(),image0.getType());

        for(int x = 0; x < image1.getWidth(); x++){
            for(int y = 0; y < image1.getHeight(); y++) {
                int argb0 = image0.getRGB(x, y);
                int argb1 = image1.getRGB(x, y);

                int a0 = (argb0 >> 24) & 0xFF;
                int r0 = (argb0 >> 16) & 0xFF;
                int g0 = (argb0 >>  8) & 0xFF;
                int b0 = (argb0      ) & 0xFF;

                int a1 = (argb1 >> 24) & 0xFF;
                int r1 = (argb1 >> 16) & 0xFF;
                int g1 = (argb1 >>  8) & 0xFF;
                int b1 = (argb1      ) & 0xFF;

                int aDiff = Math.abs(a1 - a0);
                int rDiff = Math.abs(r1 - r0);
                int gDiff = Math.abs(g1 - g0);
                int bDiff = Math.abs(b1 - b0);

                int diff =
                        (aDiff << 24) | (rDiff << 16) | (gDiff << 8) | bDiff;
                result.setRGB(x, y, diff);
            }
        }
        return result;

    }


}
