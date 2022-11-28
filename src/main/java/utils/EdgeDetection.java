package utils;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;


public class EdgeDetection {

    public static void sobel(File image) throws IOException {
        // First we declare the variables we are going to use
        Mat src, src_gray = new Mat();
        Mat grad = new Mat();
        String window_name = "Sobel Demo - Simple Edge Detector";
        int scale = 1;
        int delta = 0;
        int ddepth = CvType.CV_16S;


        // Load the image
        src = Imgcodecs.imread(image.getAbsolutePath());

        // Remove noise by blurring with a Gaussian filter ( kernel size = 3 )
        Imgproc.GaussianBlur( src, src, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT );
        // Convert the image to grayscale
        Imgproc.cvtColor( src, src_gray, Imgproc.COLOR_RGB2GRAY );
        Mat grad_x = new Mat(), grad_y = new Mat();
        Mat abs_grad_x = new Mat(), abs_grad_y = new Mat();
        //Imgproc.Scharr( src_gray, grad_x, ddepth, 1, 0, scale, delta, Core.BORDER_DEFAULT );
        Imgproc.Sobel( src_gray, grad_x, ddepth, 1, 0, 3, scale, delta, Core.BORDER_DEFAULT );
        //Imgproc.Scharr( src_gray, grad_y, ddepth, 0, 1, scale, delta, Core.BORDER_DEFAULT );
        Imgproc.Sobel( src_gray, grad_y, ddepth, 0, 1, 3, scale, delta, Core.BORDER_DEFAULT );
        // converting back to CV_8U
        Core.convertScaleAbs( grad_x, abs_grad_x );
        Core.convertScaleAbs( grad_y, abs_grad_y );
        Core.addWeighted( abs_grad_x, 0.5, abs_grad_y, 0.5, 0, grad );
        FileService.openImage(FileService.matToBuffered(grad));
    }

    public static void prewitt(File image) throws IOException {
        int kernelSize = 9;

        Mat source = Imgcodecs.imread(image.getAbsolutePath());
        Mat destination = new Mat(source.rows(),source.cols(),source.type());
        Mat kernel = new Mat(kernelSize,kernelSize, CvType.CV_32F){
            {
                put(0,0,-1);
                put(0,1,0);
                put(0,2,1);
                put(1,0-1);
                put(1,1,0);
                put(1,2,1);
                put(2,0,-1);
                put(2,1,0);
                put(2,2,1);
            }
        };
        Imgproc.filter2D(source, destination, -1, kernel);
        FileService.openImage(FileService.matToBuffered(destination));


    }

    public static void canny(File image) throws IOException {
        Mat src = Imgcodecs.imread(image.getAbsolutePath());
        //Creating an empty matrices to store edges, source, destination
        Mat gray = new Mat(src.rows(), src.cols(), src.type());
        Mat edges = new Mat(src.rows(), src.cols(), src.type());
        Mat dst = new Mat(src.rows(), src.cols(), src.type(), new Scalar(0));
        //Converting the image to Gray
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGB2GRAY);
        //Blurring the image
        Imgproc.blur(gray, edges, new Size(3, 3));
        //Detecting the edges
        Imgproc.Canny(edges, edges, 100, 100*3);
        //Copying the detected edges to the destination matrix
        src.copyTo(dst, edges);
        FileService.openImage(FileService.matToBuffered(dst));
    }

}
