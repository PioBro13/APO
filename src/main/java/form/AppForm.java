package form;

import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.FileService;
import utils.Histogram;
import utils.ImageOperations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
                try {
                    int thresholdLevel = thresholdLevel();

                    if (thresholdLevel < -1 || thresholdLevel > 255) {
                        JOptionPane.showMessageDialog(null, "Wrong threshold given!\nPlease enter number greater or equal than 0 and smaller or equal than 255",
                                "Wrong threshold level", JOptionPane.ERROR_MESSAGE);
                    }else if(thresholdLevel == -1){
                        System.out.println("Wrong data type");
                    }else{
                        ImageOperations.pictureThresholding(openFile(), thresholdLevel,isBinary());
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
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
