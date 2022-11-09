package utils;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

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

    public static BufferedImage histogramEqualize(File image) throws IOException {
        BufferedImage bufferedImage = FileService.imageToBuffered(image);
        Histogram2 histogram = new Histogram2(bufferedImage);
        int h[][] = histogram.getRGB();
        int r[] = new int[3], hint[] = new int[3];
        int left[][] = new int[3][histogram.getLevels()];
        int right[][] = new int[3][histogram.getLevels()];
        int newValue[][] = new int[3][histogram.getLevels()];

        for (int z = 0; z<histogram.getLevels(); ++z) {
            for (int ch = 0;ch<3;++ch) {
                left[ch][z] = r[ch];
                hint[ch] += h[ch][z];
                while (hint[ch] > histogram.getHRGBAvg()[ch]) {
                    hint[ch] -= histogram.getHRGBAvg()[ch];
                    ++r[ch];
                }
                right[ch][z] = r[ch];
                newValue[ch][z] = right[ch][z] - left[ch][z];
            }
        }

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        final byte[] a = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        for (int p = 0; p < width*height*histogram.getChannels(); p+=histogram.getChannels() ) {

            for (int ch = 0;ch<histogram.getChannels();++ch) {
                int chInv = histogram.getChannels()-1-ch;
                int i = a[p+chInv] & 0xFF;
                int avg = getAverage(a, width, height, histogram.getChannels(), p);
                if(avg < left[ch][i]) a[p+chInv] = (byte) (left[ch][i] & 0xFF);
                else if(avg > right[ch][i]) a[p+chInv] = (byte) (right[ch][i] & 0xFF);
                else a[p+chInv] = (byte) (avg & 0xFF);
            }
        }

        return bufferedImage;

    }

    private static int getAverage(byte[] a, int width, int height, int ch, int p) {
        if (p<width*ch || p>a.length-width*ch-3 || p%width*ch <ch || p%width*ch>width*ch-ch) return a[p] & 0xFF;
        int sum;
        try {
            sum = a[p-((width+1)*ch)] & 0xFF + a[p-((width)*ch)] & 0xFF + a[p-((width-1)*ch)] & 0xFF +
                    a[p-ch] & 0xFF + a[p+ch] & 0xFF +
                    a[p+((width+1)*ch)] & 0xFF + a[p+((width)*ch)] & 0xFF + a[p+((width-1)*ch)] & 0xFF;
        } catch (ArrayIndexOutOfBoundsException e) {
            return a[p] & 0xFF;
        }
        return sum/8;
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

