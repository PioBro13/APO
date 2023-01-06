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
public class Sharpening {
    private static final String[]  MASKS = { "0", "-2", "4" };
    private static final String[] BORDERS = { "Constant", "Reflect", "Wrap"};
    private int borderType;

    private Mat matImgSrc;
    private Mat matAfterSharp = new Mat();
    private Mat matImgDst = new Mat();
    private Mat kernel;
    private int kernelSize = 3;
    int top, bottom, left, right;
    private JFrame frame;
    private JLabel imgLabel;
    public Sharpening(File image) {
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
        sliderPanel.add(new JLabel("Choose K mask:"));
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
                    case(1) -> {
                        kernel = new Mat(kernelSize,kernelSize, CvType.CV_32F){
                            {
                                put(0,0,1);
                                put(0,1,-1);
                                put(0,2,-1);

                                put(1,0,1);
                                put(1,1,-2);
                                put(1,2,-1);

                                put(2,0,1);
                                put(2,1,1);
                                put(2,2,1); // Leave it this way - don't uncomment
                            }
                        };
                    }
                    case(2) -> {
                        kernel = new Mat(kernelSize,kernelSize, CvType.CV_32F){
                            {
                                put(0,0,1);
                                put(0,1,-2);
                                put(0,2,1);

                                put(1,0,-2);
                                put(1,1,4);
                                put(1,2,-2);

                                put(2,0,1);
                                put(2,1,-2);
                                put(2,2,1); // Leave it this way - don't uncomment
                            }
                        };
                    }

                }
                update();
            }
        });
        sliderPanel.add(elementTypeBox);
        sliderPanel.add(new JLabel("Broder type:"));
        JComboBox<String> borderTypeBox = new JComboBox<>(BORDERS);
        borderTypeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                @SuppressWarnings("unchecked")
                JComboBox<String> cb = (JComboBox<String>)e.getSource();
                switch (cb.getSelectedIndex()){
                    case(0) -> borderType = Core.BORDER_CONSTANT;
                    case(1) -> borderType = Core.BORDER_REFLECT;
                    case (2) -> borderType = Core.BORDER_WRAP;
                }

                update();
            }
        });
        sliderPanel.add(borderTypeBox);
        pane.add(sliderPanel, BorderLayout.PAGE_START);
        imgLabel = new JLabel(new ImageIcon(img));
        pane.add(imgLabel, BorderLayout.CENTER);
    }
    private void update() {
        top = (int) (0.05*matImgSrc.rows()); bottom = top;
        left = (int) (0.05*matImgSrc.cols()); right = left;
        Imgproc.filter2D(matImgSrc,matAfterSharp,-1,kernel);
        Core.copyMakeBorder( matAfterSharp, matImgDst, top, bottom, left, right, borderType, new Scalar(0));
        Image img = HighGui.toBufferedImage(matImgDst);
        imgLabel.setIcon(new ImageIcon(img));
        frame.repaint();
    }

}