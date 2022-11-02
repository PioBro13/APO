package utils;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class HistogramOperations {

    public static Mat histogramStretch(File image) {
        ArrayList<int[]> histData = FileService.intTableLUT(image);
        Mat originalPixels = Imgcodecs.imread(image.getAbsolutePath());
        Mat resultMatrix = originalPixels.clone();
        int[] minRGBK = new int[]{0, 0, 0, 0};
        int[] maxRGBK = new int[]{255, 255, 255, 255};

        int i;
        for(i = 0; i < 4; ++i) {
            HistogramOperations.Pair minMax = findMinMax(histData.get(i));
            minRGBK[i] = (Integer)minMax.min;
            maxRGBK[i] = (Integer)minMax.max;
        }

        for(i = 0; i < resultMatrix.rows(); ++i) {
            for(int j = 0; j < resultMatrix.cols(); ++j) {
                double[] data = resultMatrix.get(i, j);
                data[0] = 255.0D * (data[0] - (double)minRGBK[2]) / (double)(maxRGBK[2] - minRGBK[2]);
                data[1] = 255.0D * (data[1] - (double)minRGBK[1]) / (double)(maxRGBK[1] - minRGBK[1]);
                data[2] = 255.0D * (data[2] - (double)minRGBK[0]) / (double)(maxRGBK[0] - minRGBK[0]);
                resultMatrix.put(i, j, data);
            }
        }

        return resultMatrix;
    }

    public static Mat histogramEqualize(File image) throws IOException {
        ArrayList<int[]> histData = FileService.intTableLUT(image);
        Mat originalPixels = Imgcodecs.imread(image.getAbsolutePath());
        ArrayList<int[]> equalizationLUTs = new ArrayList();

        for(int i = 1; i < 4; ++i) {
            double[] distribution = getDistribution(histData.get(i), originalPixels.rows() * originalPixels.cols());
            equalizationLUTs.add(makeEqualizationLUT(distribution));
        }

        return applyLUT(originalPixels, equalizationLUTs);

    }

    public static double[] getDistribution(int[] histogram, int pixelCount) {
        double[] distribution = new double[histogram.length];
        distribution[0] = (double)histogram[0] / (double)pixelCount;

        int i;
        for(i = 1; i < histogram.length; ++i) {
            distribution[i] = (double)histogram[i] + distribution[i - 1];
        }

        for(i = 1; i < distribution.length; ++i) {
            distribution[i] /= (double)pixelCount;
        }

        return distribution;
    }

    public static Mat applyLUT(Mat pixels, ArrayList<int[]> equalizationLUTs) {
        Mat result = pixels.clone();
        int rows = pixels.rows();
        int cols = pixels.cols();

        for(int i = 1; i < rows; ++i) {
            for(int j = 1; j < cols; ++j) {
                double[] data = pixels.get(i, j);
                data[0] = (double)((int[])equalizationLUTs.get(2))[(int)data[0]];
                data[1] = (double)((int[])equalizationLUTs.get(1))[(int)data[1]];
                data[2] = (double)((int[])equalizationLUTs.get(0))[(int)data[2]];
                result.put(i, j, data);
            }
        }

        return result;
    }

    public static int[] makeEqualizationLUT(double[] distribution) {
        int[] LUT = new int[distribution.length];
        HistogramOperations.Pair minMax = findMinMax(distribution);
        double d0 = (Double)minMax.min;

        for(int i = 1; i < distribution.length; ++i) {
            LUT[i] = (int)(255.0D * ((distribution[i] - d0) / (1.0D - d0)));
        }

        return LUT;
    }

    public static HistogramOperations.Pair findMinMax(int[] vector) {
        int i;
        for(i = 0; vector[i] == 0; ++i) {
        }

        int min = i;

        for(i = vector.length - 1; vector[i] == 0; --i) {
        }

        return new HistogramOperations.Pair(vector[min], vector[i]);
    }
    public static HistogramOperations.Pair findMinMax(double[] vector) {
        int i;
        for(i = 0; vector[i] == 0.0D && i + 1 < vector.length - 1; ++i) {
        }

        int min = i;

        for(i = vector.length - 1; vector[i] == 0.0D && i - 1 > 0; --i) {
        }

        return new HistogramOperations.Pair(vector[min], vector[i]);
    }

    public static class Pair<T> {
        public T min;
        public T max;

        public Pair() {
            this.min = null;
            this.max = null;
        }

        public Pair(T min, T max) {
            this.min = min;
            this.max = max;
        }
    }

}

