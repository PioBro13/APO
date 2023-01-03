package utils;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
public class MorphologyOperations {
    private static final String[] ELEMENT_TYPE = { "Rectangle", "Cross", "Ellipse" };
    private static final String[] MORPH_OP = { "Erosion", "Dilatation", "Opening", "Closing" };
    private static final int MAX_KERNEL_SIZE = 21;
    private Mat matImgSrc;
    private Mat matImgDst = new Mat();
    private int elementType = Imgproc.CV_SHAPE_RECT;
    private int kernelSize = 0;
    private int erosionType;
    private JFrame frame;
    private JLabel imgLabel;
    public MorphologyOperations(File image) {
        String imagePath = image.getAbsolutePath();
        matImgSrc = Imgcodecs.imread(imagePath);
        // Create and set up the window.
        frame = new JFrame("Morphology operation");
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
        JComboBox<String> elementTypeBox = new JComboBox<>(ELEMENT_TYPE);
        elementTypeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                @SuppressWarnings("unchecked")
                JComboBox<String> cb = (JComboBox<String>)e.getSource();
                if (cb.getSelectedIndex() == 0) {
                    elementType = Imgproc.CV_SHAPE_RECT;
                } else if (cb.getSelectedIndex() == 1) {
                    elementType = Imgproc.CV_SHAPE_CROSS;
                } else if (cb.getSelectedIndex() == 2) {
                    elementType = Imgproc.CV_SHAPE_ELLIPSE;
                }
                update();
            }
        });
        sliderPanel.add(elementTypeBox);
        sliderPanel.add(new JLabel("Kernel size: 2n + 1"));
        JSlider slider = new JSlider(0, MAX_KERNEL_SIZE, 0);
        slider.setMajorTickSpacing(5);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                kernelSize = source.getValue();
                update();
            }
        });
        sliderPanel.add(slider);
        JComboBox<String> morphOpBox = new JComboBox<>(MORPH_OP);
        morphOpBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                @SuppressWarnings("unchecked")
                JComboBox<String> cb = (JComboBox<String>)e.getSource();
                erosionType = cb.getSelectedIndex();
                update();
            }
        });
        sliderPanel.add(morphOpBox);
        pane.add(sliderPanel, BorderLayout.PAGE_START);
        imgLabel = new JLabel(new ImageIcon(img));
        pane.add(imgLabel, BorderLayout.CENTER);
    }
    private void update() {
        Mat element = Imgproc.getStructuringElement(elementType, new Size(2 * kernelSize + 1, 2 * kernelSize + 1),
                new Point(kernelSize, kernelSize));
        switch (erosionType){
            case(0):
                Imgproc.erode(matImgSrc, matImgDst, element);
                break;
            case(1):
                Imgproc.dilate(matImgSrc, matImgDst, element);
                break;
            case (2):
                Imgproc.morphologyEx(matImgSrc,matImgDst,Imgproc.MORPH_OPEN,element);
                break;
            case(3):
                Imgproc.morphologyEx(matImgSrc,matImgDst,Imgproc.MORPH_CLOSE,element);
                break;
        }


        /*if (doErosion) {
            Imgproc.erode(matImgSrc, matImgDst, element);
        } else {
            Imgproc.dilate(matImgSrc, matImgDst, element);
        }*/
        Image img = HighGui.toBufferedImage(matImgDst);
        imgLabel.setIcon(new ImageIcon(img));
        frame.repaint();
    }

}