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
import java.util.Objects;

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

    private JButton imagesSubstractButton;
    private JButton arithmeticOperations;
    private JButton mergeImagesButton;
    private JButton logicalOperationsButton;
    private JButton edgeDetectionButton;
    private JButton prewittDetectionButton;
    private JButton cannyDetectionButton;
    private JButton morphologyButton;
    private JButton smoothingButton;
    private JButton sobelButton;
    private JButton medianButton;
    private JButton sharpeningButton;
    private BufferedImage lastOpenedFile;

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
                    lastOpenedFile = FileService.imageToBuffered(openedFile);
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
        imagesSubstractButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    FileService.openImage(ImageOperations.substractImages(openFile(),openFile()));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        arithmeticOperations.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int value = arithmeticValue();
                String operation = operationType();


                try {
                    FileService.openImage(ImageOperations.arithmeticOperation(openFile(),operation,value));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        mergeImagesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    FileService.openImage(ImageOperations.mergeImages(openFile(),openFile()));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        logicalOperationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            File file = openFile();
            String operation = logicOperationType();

            if(Objects.equals(operation, "NOT")){
                try {
                    lastOpenedFile = ImageOperations.negation(FileService.imageToBuffered(file));
                    FileService.openImage(ImageOperations.negation(FileService.imageToBuffered(file)));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            }
        });
        edgeDetectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    EdgeDetection.sobel(openFile());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        prewittDetectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    EdgeDetection.prewitt(openFile());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        cannyDetectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    EdgeDetection.canny(openFile());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        morphologyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MorphologyOperations(openFile());

            }
        });
        smoothingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Smoothing(openFile());
            }
        });
        medianButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Median(openFile());
            }
        });
        sharpeningButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Sharpening(openFile());
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

    public int arithmeticValue(){
        int givenLevel = -1;
        try {
             givenLevel = Integer.parseInt(JOptionPane.showInputDialog("Enter integer value"));
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Incorrect data type!\nPlease enter integer number.",
                    "Wrong value", JOptionPane.ERROR_MESSAGE);
        }
        return givenLevel;
    }

    public String operationType(){
        JPanel panel = new JPanel();
        panel.add(new JLabel("Please make a selection:"));
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("Addition");
        model.addElement("Division");
        model.addElement("Multiplication");
        JComboBox comboBox = new JComboBox(model);
        panel.add(comboBox);

        int result = JOptionPane.showConfirmDialog(null, panel, "Flavor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        System.out.println(result);
        return comboBox.getSelectedItem().toString();
    }

    public String logicOperationType(){
        JPanel panel = new JPanel();
        panel.add(new JLabel("Please make a selection:"));
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("NOT");
        model.addElement("AND");
        model.addElement("OR");
        model.addElement("XOR");
        JComboBox comboBox = new JComboBox(model);
        panel.add(comboBox);

        int result = JOptionPane.showConfirmDialog(null, panel, "Flavor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        System.out.println(result);
        return comboBox.getSelectedItem().toString();
    }

}
