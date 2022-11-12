package utils;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
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

}
