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
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
public class Smoothing {
    private static final String[] ELEMENT_TYPE = { "Blur", "Gaussian" };
    private static final String[] BORDERS = { "Constant", "Reflect", "Wrap"};
    private int borderType;

    private Mat matImgSrc;
    private Mat matImgDst = new Mat();
    private int kernelSize = 0;
    private int smoothingType;
    private JFrame frame;
    private JLabel imgLabel;
    public Smoothing(File image) {
        String imagePath = image.getAbsolutePath();
        matImgSrc = Imgcodecs.imread(imagePath);
        // Create and set up the window.
        frame = new JFrame("Smoothing operation");
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
        sliderPanel.add(new JLabel("Smoothing type:"));
        JComboBox<String> elementTypeBox = new JComboBox<>(ELEMENT_TYPE);
        elementTypeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                @SuppressWarnings("unchecked")
                JComboBox<String> cb = (JComboBox<String>)e.getSource();
                smoothingType = cb.getSelectedIndex();
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
                    case (2) -> {
                        if(smoothingType != 1){
                            JOptionPane.showMessageDialog(null,
                                    "You can't use wrap border in this operation", "Wrong border type",
                                    JOptionPane.ERROR_MESSAGE);
                        }else{
                            borderType = Core.BORDER_WRAP;
                        };
                    }
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

        Size kSize = new Size(2 * kernelSize + 1, 2 * kernelSize + 1);
        switch (smoothingType) {
            case (0) -> Imgproc.blur(matImgSrc, matImgDst, new Size(3, 3), new Point(0,0) ,borderType);
            case (1) -> Imgproc.GaussianBlur(matImgSrc, matImgDst, new Size(3, 3), 0, 0,borderType);
        }

        Image img = HighGui.toBufferedImage(matImgDst);
        imgLabel.setIcon(new ImageIcon(img));
        frame.repaint();
    }

}