package form;

import org.opencv.core.Mat;
import utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AppForm extends JFrame{
    private JPanel mainPanel;
    private JButton openFileButton;
    private JButton histogramButton;
    private JButton duplicateButton;
    private JButton saveFileButton;
    private JButton negationButton;
    private JButton thresholdingButton;
    private JButton equalizedHistogramButton;
    private JButton stretchHistogramButton;
    private JButton smoothingGaussianButton;
    private JButton medianSmoothingButton;
    private File lastOpenedFile;

    public AppForm(String title){
        super(title);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File openedFile = openFile();
                    lastOpenedFile = openedFile;
                    FileService.openImage(openedFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        histogramButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(() -> {
                    //Better histogram
                    try {
                        new Histogram().display(openFile());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    // HistogramManual histogram = new HistogramManual(openFile());
                  //histogram.initialize();
                });
            }
        });


        duplicateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    FileService.openImage(lastOpenedFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        saveFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    FileService.saveFile(lastOpenedFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        negationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ImageOperations.negation(lastOpenedFile);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        thresholdingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Threshold(openFile());
            }
        });
        equalizedHistogramButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File file = openFile();
                    BufferedImage equalizedImage = HistogramOperations.histogramEqualize(file);
                    new Histogram().display(equalizedImage);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        stretchHistogramButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File file = openFile();
                    Mat stretchedMat = HistogramOperations.histogramStretch(file);
                    new Histogram().display(FileService.matToBuffered(stretchedMat),FileService.tableLUT(stretchedMat));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        smoothingGaussianButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new Histogram().display(ImageOperations.smoothingGaussian(openFile()));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        medianSmoothingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new Histogram().display(ImageOperations.smoothingMedian(openFile()));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public File openFile(){
        JFileChooser fc = new JFileChooser("src/main/resources");
        int returnVal = fc.showOpenDialog(this.mainPanel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            System.out.println("Opened file: " + file.getAbsolutePath());
            return file;
        } else {
            System.out.println("File not found");
            return null;
        }
    }

    public int thresholdLevel(){
        int givenLevel = -1;
        try {
             givenLevel = Integer.parseInt(JOptionPane.showInputDialog("Enter thresholding level"));
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Incorrect data type!\nPlease enter integer number.",
                    "Wrong threshold level", JOptionPane.ERROR_MESSAGE);
        }
        return givenLevel;
    }

    public boolean isBinary(){
        int n = JOptionPane.showConfirmDialog(
                this,
                "Do you want to make binary thresholding?",
                "Binary thresholding",
                JOptionPane.YES_NO_OPTION);
        return n == 0;
    }

}
