package utils;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static utils.FileService.matToBuffered;
import static utils.FileService.openImage;

public class ImageOperations {

    public static void negation(File image) throws IOException {
        Mat matrix = Imgcodecs.imread(image.getAbsolutePath());
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
        openImage(matToBuffered(matrix));
    }

    public static void pictureThresholding(File image,int threshold,  boolean modeIsBinary) throws IOException {
        Mat img = Imgcodecs.imread(image.getAbsolutePath());
        Mat result = img.clone();
        int rows = img.rows();
        int cols = img.cols();

        for(int i = 0; i < rows; ++i) {
            for(int j = 0; j < cols; ++j) {
                double[] data = img.get(i, j);
                if (!modeIsBinary) {
                    data[0] = data[0] > (double)threshold ? data[0] : 0.0D;
                    data[1] = data[1] > (double)threshold ? data[1] : 0.0D;
                    data[2] = data[2] > (double)threshold ? data[2] : 0.0D;
                } else {
                    data[0] = data[0] > (double)threshold ? 255.0D : 0.0D;
                    data[1] = data[1] > (double)threshold ? 255.0D : 0.0D;
                    data[2] = data[2] > (double)threshold ? 255.0D : 0.0D;
                }

                result.put(i, j, data);
            }
        }

        new Histogram().display(matToBuffered(result));
    }

    public static BufferedImage resizeImage(BufferedImage image, int sizeChange) throws IOException {
        Mat source = FileService.BufferedImage2Mat(image);
        Mat resized = new Mat();
        Size size = new Size(sizeChange,sizeChange);
        Imgproc.resize(source,resized,size);
        return FileService.matToBuffered(resized);
    }

}
