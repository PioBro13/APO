package utils;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
public class Sobel {
    private static final String[]  MASKS = { "E", "SE", "SW","W","NW","N","NE" };

    private int borderType;

    private Mat matImgSrc;
    private Mat matAfterMed = new Mat();
    private Mat matImgDst = new Mat();
    private Mat kernel;
    private int kernelSize = 3;
    int top, bottom, left, right;
    private JFrame frame;
    private JLabel imgLabel;
    public Sobel(File image) {
        String imagePath = image.getAbsolutePath();
        matImgSrc = Imgcodecs.imread(imagePath);
        // Create and set up the window.
        frame = new JFrame("Sharpening operation");
        // Set up the content pane.
        Image img = HighGui.toBufferedImage(matImgSrc);
        addComponentsToPane(frame.getContentPane(), img);
        // Use the content pane's default BorderLayout. No need for
        // setLayout(new BorderLayout());
        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    private void addComponentsToPane(Container pane, Image img) {
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
        sliderPanel.add(new JLabel("Kernel size:"));
        JComboBox<String> elementTypeBox = new JComboBox<>(MASKS);
        elementTypeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                @SuppressWarnings("unchecked")
                JComboBox<String> cb = (JComboBox<String>)e.getSource();
                switch (cb.getSelectedIndex()){
                    case(0) -> {
                        kernel = new Mat(kernelSize,kernelSize, CvType.CV_32F){
                            {
                                put(0,0,-1);
                                put(0,1,0);
                                put(0,2,1);

                                put(1,0,-1);
                                put(1,1,0);
                                put(1,2,1);

                                put(2,0,-1);
                                put(2,1,0);
                                put(2,2,1); // Leave it this way - don't uncomment
                            }
                        };
                    }
                    case(1) -> {
                        kernel = new Mat(kernelSize,kernelSize, CvType.CV_32F){
                            {
                                put(0,0,-1);
                                put(0,1,-1);
                                put(0,2,0);

                                put(1,0,-1);
                                put(1,1,0);
                                put(1,2,1);

                                put(2,0,0);
                                put(2,1,1);
                                put(2,2,1); // Leave it this way - don't uncomment
                            }
                        };
                    }
                    case(2) -> {
                        kernel = new Mat(kernelSize,kernelSize, CvType.CV_32F){
                            {
                                put(0,0,-1);
                                put(0,1,-1);
                                put(0,2,-1);

                                put(1,0,0);
                                put(1,1,0);
                                put(1,2,0);

                                put(2,0,1);
                                put(2,1,1);
                                put(2,2,1); // Leave it this way - don't uncomment
                            }
                        };
                    }
                    case(3) -> {
                        kernel = new Mat(kernelSize,kernelSize, CvType.CV_32F){
                            {
                                put(0,0,0);
                                put(0,1,-1);
                                put(0,2,-1);

                                put(1,0,1);
                                put(1,1,0);
                                put(1,2,-1);

                                put(2,0,1);
                                put(2,1,1);
                                put(2,2,0); // Leave it this way - don't uncomment
                            }
                        };
                    }
                    case(4) -> {
                        kernel = new Mat(kernelSize,kernelSize, CvType.CV_32F){
                            {
                                put(0,0,1);
                                put(0,1,0);
                                put(0,2,-1);

                                put(1,0,1);
                                put(1,1,0);
                                put(1,2,-1);

                                put(2,0,1);
                                put(2,1,0);
                                put(2,2,-1); // Leave it this way - don't uncomment
                            }
                        };
                    }
                    case(5) -> {
                        kernel = new Mat(kernelSize,kernelSize, CvType.CV_32F){
                            {
                                put(0,0,1);
                                put(0,1,1);
                                put(0,2,0);

                                put(1,0,1);
                                put(1,1,0);
                                put(1,2,-1);

                                put(2,0,0);
                                put(2,1,-1);
                                put(2,2,-1); // Leave it this way - don't uncomment
                            }
                        };
                    }
                    case(6) -> {
                        kernel = new Mat(kernelSize,kernelSize, CvType.CV_32F){
                            {
                                put(0,0,1);
                                put(0,1,1);
                                put(0,2,1);

                                put(1,0,0);
                                put(1,1,0);
                                put(1,2,0);

                                put(2,0,-1);
                                put(2,1,-1);
                                put(2,2,-1); // Leave it this way - don't uncomment
                            }
                        };
                    }
                    case(7) -> {
                        kernel = new Mat(kernelSize,kernelSize, CvType.CV_32F){
                            {
                                put(0,0,0);
                                put(0,1,1);
                                put(0,2,1);

                                put(1,0,-1);
                                put(1,1,0);
                                put(1,2,1);

                                put(2,0,-1);
                                put(2,1,-1);
                                put(2,2,0); // Leave it this way - don't uncomment
                            }
                        };
                    }

                }
                update();
            }
        });
        sliderPanel.add(elementTypeBox);
        pane.add(sliderPanel, BorderLayout.PAGE_START);
        imgLabel = new JLabel(new ImageIcon(img));
        pane.add(imgLabel, BorderLayout.CENTER);
    }
    private void update() {
        Imgproc.filter2D(matImgSrc,matImgDst,-1,kernel);
        Image img = HighGui.toBufferedImage(matImgDst);
        imgLabel.setIcon(new ImageIcon(img));
        frame.repaint();
    }

}