package main;

import form.AppForm;
import nu.pattern.OpenCV;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        OpenCV.loadLocally();
        JFrame form = new AppForm("APO Project");
        form.setVisible(true);
        form.setSize(500,300);

    }

}
