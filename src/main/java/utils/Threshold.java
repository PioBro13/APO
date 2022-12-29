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
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Threshold {
    private static int MAX_VALUE = 255;
    private static int MAX_TYPE = 5;
    private static int MAX_BINARY_VALUE = 255;
    private static final String WINDOW_NAME = "Threshold Demo";
    private static final String TRACKBAR_TYPE = "<html><body>Type: <br> 0: Binary <br> "
            + "1: Binary Inverted <br> 2: Truncate <br> "
            + "3: To Zero <br> 4: To Zero Inverted <br> 5: Otsu</body></html>";
    private static final String TRACKBAR_VALUE = "Value";
    private int thresholdValue = 0;
    private int thresholdType = 3;
    private Mat src;
    private Mat srcGray = new Mat();
    private Mat dst = new Mat();
    private JFrame frame;
    private JLabel imgLabel;
    private boolean isAdaptive = false;


    public Threshold(File image) {

        String imagePath = image.getAbsolutePath();

        // Load an image
        src = Imgcodecs.imread(imagePath);
        if (src.empty()) {
            System.out.println("Empty image: " + imagePath);
            System.exit(0);
        }
        // Convert the image to Gray
        Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY);
        // Create and set up the window.
        frame = new JFrame(WINDOW_NAME);
        // Set up the content pane.
        Image img = HighGui.toBufferedImage(srcGray);
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
        sliderPanel.add(new JLabel(TRACKBAR_TYPE));
        // Create Trackbar to choose type of Threshold
        JSlider sliderThreshType = new JSlider(0, MAX_TYPE, thresholdType);
        sliderThreshType.setMajorTickSpacing(1);
        sliderThreshType.setMinorTickSpacing(1);
        sliderThreshType.setPaintTicks(true);
        sliderThreshType.setPaintLabels(true);
        sliderPanel.add(sliderThreshType);
        sliderPanel.add(new JLabel(TRACKBAR_VALUE));
        // Create Trackbar to choose Threshold value
        JSlider sliderThreshValue = new JSlider(0, MAX_VALUE, 0);
        sliderThreshValue.setMajorTickSpacing(50);
        sliderThreshValue.setMinorTickSpacing(10);
        sliderThreshValue.setPaintTicks(true);
        sliderThreshValue.setPaintLabels(true);
        sliderPanel.add(sliderThreshValue);
        // Create Text field for threshold value
        sliderPanel.add(new JLabel("Threshold level"));
        JTextField textField = new JTextField(5);
        sliderPanel.add(textField);
        JButton applyThresholdButton = new JButton("Apply threshold level from text field");
        sliderPanel.add(applyThresholdButton);
        JCheckBox checkBox = new JCheckBox("Adaptive threshold");
        sliderPanel.add(checkBox);
        sliderThreshType.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                thresholdType = source.getValue();
                update(isAdaptive);
            }
        });
        sliderThreshValue.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                thresholdValue = source.getValue();
                textField.setText(String.valueOf(thresholdValue));
                update(isAdaptive);
            }
        });
        applyThresholdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    int level = Integer.parseInt(textField.getText());
                    if(level < 0 || level > 255){
                        JOptionPane.showMessageDialog(null,
                                "Please enter number bigger than 0 or smaller than 256", "Wrong threshold level",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        thresholdValue = level;
                        sliderThreshValue.setValue(level);
                        update(isAdaptive);
                    }
                } catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(null, "Incorrect data type!\nPlease enter integer number.",
                            "Wrong threshold level", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isAdaptive = checkBox.isSelected();
                update(isAdaptive);
            }
        });
        pane.add(sliderPanel, BorderLayout.PAGE_START);
        imgLabel = new JLabel(new ImageIcon(img));
        pane.add(imgLabel, BorderLayout.CENTER);
        /*JButton showHistogramButton = new JButton("Show histogram");
        sliderPanel.add(showHistogramButton);
        showHistogramButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new Histogram().display(FileService.matToBuffered(dst));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }); */
    }
    private void update(boolean adaptive) {
        if (adaptive){
            Imgproc.adaptiveThreshold(srcGray, dst, thresholdValue,
                    Imgproc.ADAPTIVE_THRESH_MEAN_C,
                    Imgproc.THRESH_BINARY, 11, 12);
        }else if(thresholdType < 5){
            Imgproc.threshold(srcGray, dst, thresholdValue, MAX_BINARY_VALUE, thresholdType);
        }else{
            Imgproc.threshold(srcGray, dst, thresholdValue, MAX_BINARY_VALUE,  Imgproc.THRESH_OTSU);
        }

        Image img = HighGui.toBufferedImage(dst);
        imgLabel.setIcon(new ImageIcon(img));
        frame.repaint();
    }

}