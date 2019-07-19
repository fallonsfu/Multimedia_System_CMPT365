package application;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.sampled.UnsupportedAudioFileException;

// Main class that start the program.
public final class Main extends Application {

    @Override
    public void start(final Stage stage) {
        stage.setTitle("CMPT 365 Project II");

        Text text1 = new Text("Huffman Compression Ratio: ");
        Text text2 = new Text("LZW Compression Ratio: ");
        final Button openButtonWav = new Button("Open a .wav file");
        final Button openButtonBmp = new Button(".bmp or .IM3 file");

        text1.setX(100);
        text1.setY(50);
        text2.setX(100);
        text2.setY(100);
        openButtonWav.setLayoutX(100);
        openButtonWav.setLayoutY(150);
        openButtonBmp.setLayoutX(400);
        openButtonBmp.setLayoutY(150);

        // When click on the buttons, an open file dialog pop up, one can only select one file at a time.
        openButtonWav.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                configureFileChooser(fileChooser, ".wav");
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    try {
                        HuffmanCoding huffman = new HuffmanCoding(FileReader.readWavToInt(file));
                        LzwCoding lzw = new LzwCoding(FileReader.readWavToByte(file));
                        text1.setText("Huffman Compression Ratio: " + huffman.getCompressRatio());
                        text2.setText("LZW Compression Ratio: " + lzw.getCompressRatio());
                    } catch (UnsupportedAudioFileException | IOException ex) {}
                }
            }
        });

        openButtonBmp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                configureFileChooser(fileChooser, ".bmp");
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    try {
                        new LossyImageCoding(file);
                    } catch (IOException ex) {}
                }
            }
        });

        Group root = new Group(openButtonWav, openButtonBmp, text1, text2);

        stage.setScene(new Scene(root, 600, 300));
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private static void configureFileChooser(final FileChooser fileChooser, String category) {
        fileChooser.setTitle("Choose a " + category + " file");
        if(category == ".wav")
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("WAV", "*.wav"));
        else if(category == ".bmp")
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("BMP or IM3", "*.bmp", "*.IM3"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("ALL File",  "*"));
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home")));
    }
}
