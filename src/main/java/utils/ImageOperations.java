package utils;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

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
}
