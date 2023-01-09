package utils;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
public class MomentsCalculation {
    private Mat srcGray = new Mat();
    private JFrame frame;
    private JLabel imgSrcLabel;
    private JLabel imgContoursLabel;
    private static final int MAX_THRESHOLD = 255;
    private int threshold = 100;
    private Random rng = new Random(12345);
    public MomentsCalculation(File image) {
        String filename = image.getAbsolutePath();
        Mat src = Imgcodecs.imread(filename);
        if (src.empty()) {
            System.err.println("Cannot read image: " + filename);
            System.exit(0);
        }
        Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(srcGray, srcGray, new Size(3, 3));
        // Create and set up the window.
        frame = new JFrame("Calculate moments");
        // Set up the content pane.
        Image img = HighGui.toBufferedImage(src);
        addComponentsToPane(frame.getContentPane(), img);
        // Use the content pane's default BorderLayout. No need for
        // setLayout(new BorderLayout());
        // Display the window.
        frame.pack();
        frame.setVisible(true);
        update();
    }
    private void addComponentsToPane(Container pane, Image img) {
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
        sliderPanel.add(new JLabel("Canny threshold: "));
        JSlider slider = new JSlider(0, MAX_THRESHOLD, threshold);
        slider.setMajorTickSpacing(20);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                threshold = source.getValue();
                update();
            }
        });
        sliderPanel.add(slider);
        JButton exportButton = new JButton("Export to excel");
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (PrintWriter writer = new PrintWriter("test.csv")) {

                    StringBuilder sb = new StringBuilder();
                    Mat cannyOutput = new Mat();
                    Imgproc.Canny(srcGray, cannyOutput, threshold, threshold * 2);
                    List<MatOfPoint> contours = new ArrayList<>();
                    Mat hierarchy = new Mat();
                    Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
                    List<Moments> mu = new ArrayList<>(contours.size());
                    for (int i = 0; i < contours.size(); i++) {
                        mu.add(Imgproc.moments(contours.get(i)));
                    }
                    List<Point> mc = new ArrayList<>(contours.size());
                    for (int i = 0; i < contours.size(); i++) {
                        //add 1e-5 to avoid division by zero
                        mc.add(new Point(mu.get(i).m10 / (mu.get(i).m00 + 1e-5), mu.get(i).m01 / (mu.get(i).m00 + 1e-5)));
                    }
                    Mat drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);
                    for (int i = 0; i < contours.size(); i++) {
                        Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
                        Imgproc.drawContours(drawing, contours, i, color, 2);
                        Imgproc.circle(drawing, mc.get(i), 4, color, -1);
                    }

                    sb.append("Contour");
                    sb.append(" , ");
                    sb.append("Moment");
                    sb.append(" , ");
                    sb.append("Area");
                    sb.append(" , ");
                    sb.append("Length");
                    sb.append(" , ");
                    sb.append("Aspect Ratio");
                    sb.append(" , ");
                    sb.append("Extent");
                    sb.append(" , ");
                    sb.append("Equivalent Diameter");
                    sb.append("\n");
                    for (int i = 0; i < contours.size(); i++) {
                        Rect h = Imgproc.boundingRect(new MatOfPoint2f(contours.get(i).toArray()));
                        float contourArea = (float)Imgproc.contourArea(contours.get(i));
                        float aspectRatio =  (float)h.width/h.height;
                        float rectangleArea = (float)h.width*h.height;
                        float extent = contourArea/rectangleArea;
                        MatOfInt hull = new MatOfInt();
                        Imgproc.convexHull(new MatOfPoint(contours.get(i).toArray()),hull,false);

                        //float solidity = (float) (contourArea/Imgproc.contourArea(hull));
                        float equivalentDiameter = (float) Math.abs((4*contourArea)/Math.PI);


                        sb.append(i);
                        sb.append(" , ");
                        sb.append(mu.get(i).m00);
                        sb.append(" , ");
                        sb.append(Imgproc.contourArea(contours.get(i)));
                        sb.append(" , ");
                        sb.append(Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()),true));
                        sb.append(" , ");
                        sb.append(aspectRatio);
                        sb.append(" , ");
                        sb.append(extent);
                        sb.append(" , ");
                        sb.append(equivalentDiameter);
                        sb.append("\n");

                    }

                    writer.write(sb.toString());

                    System.out.println("done!");

                } catch (FileNotFoundException error) {
                    System.out.println(error.getMessage());
                }
            }
        });
        sliderPanel.add(exportButton);
        pane.add(sliderPanel, BorderLayout.PAGE_START);
        JPanel imgPanel = new JPanel();
        imgSrcLabel = new JLabel(new ImageIcon(img));
        imgPanel.add(imgSrcLabel);
        Mat blackImg = Mat.zeros(srcGray.size(), CvType.CV_8U);
        imgContoursLabel = new JLabel(new ImageIcon(HighGui.toBufferedImage(blackImg)));
        imgPanel.add(imgContoursLabel);
        pane.add(imgPanel, BorderLayout.CENTER);
    }

    private void update() {
        Mat cannyOutput = new Mat();
        Imgproc.Canny(srcGray, cannyOutput, threshold, threshold * 2);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        List<Moments> mu = new ArrayList<>(contours.size());
        for (int i = 0; i < contours.size(); i++) {
            mu.add(Imgproc.moments(contours.get(i)));
        }
        List<Point> mc = new ArrayList<>(contours.size());
        for (int i = 0; i < contours.size(); i++) {
            //add 1e-5 to avoid division by zero
            mc.add(new Point(mu.get(i).m10 / (mu.get(i).m00 + 1e-5), mu.get(i).m01 / (mu.get(i).m00 + 1e-5)));
        }
        Mat drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);
        for (int i = 0; i < contours.size(); i++) {
            Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
            Imgproc.drawContours(drawing, contours, i, color, 2);
            Imgproc.circle(drawing, mc.get(i), 4, color, -1);
        }




        imgContoursLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(drawing)));
        frame.repaint();
        System.out.println("\t Info: Area and Contour Length \n");
        for (int i = 0; i < contours.size(); i++) {
            Rect h = Imgproc.boundingRect(new MatOfPoint2f(contours.get(i).toArray()));
            float contourArea = (float)Imgproc.contourArea(contours.get(i));
            float aspectRatio =  (float)h.width/h.height;
            float rectangleArea = (float)h.width*h.height;
            float extent = contourArea/rectangleArea;
            MatOfInt hull = new MatOfInt();
            Imgproc.convexHull(new MatOfPoint(contours.get(i).toArray()),hull,false);

            //float solidity = (float) (contourArea/Imgproc.contourArea(hull));
            float equivalentDiameter = (float) Math.abs((4*contourArea)/Math.PI);

            System.out.format(" * Contour[%d] - Area (M_00) = %.2f - Area OpenCV: %.2f - Length: %.2f - Aspect Ratio: %.2f - " +
                            "Extent: %.2f - Equivalent Diameter: %.2f\n", i,
                    mu.get(i).m00, Imgproc.contourArea(contours.get(i)),
                    Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()), true),
                    aspectRatio,extent,equivalentDiameter);
        }
    }
}
