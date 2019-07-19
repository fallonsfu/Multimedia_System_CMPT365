package application;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.sampled.UnsupportedAudioFileException;

// Main class that start the program.
public final class Main extends Application {

    @Override
    public void start(final Stage stage) {
        stage.setTitle("CMPT 365 Waves Drawer");

        final Button openButtonWav = new Button("Open a .wav file");
        final Button openButtonBmp = new Button("Open a .bmp file");

        openButtonWav.setLayoutX(50);
        openButtonWav.setLayoutY(100);
        openButtonBmp.setLayoutX(250);
        openButtonBmp.setLayoutY(100);

        // When click on the buttons, an open file dialog pop up, one can only select one file at a time.
        openButtonWav.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                configureFileChooser(fileChooser, ".wav");
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    try {
                        new Wavform(file);
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
                        new BmpImage(file);
                    } catch (IOException ex) {}
                }
            }
        });

        Group root = new Group(openButtonWav, openButtonBmp);

        stage.setScene(new Scene(root, 400, 200));
        stage.show();
    }

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException  {
        Application.launch(args);
    }

    private static void configureFileChooser(final FileChooser fileChooser, String category) {
        fileChooser.setTitle("Choose a " + category + " file");
        if(category == ".wav")
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("WAV", "*.wav"));
        else if(category == ".bmp")
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("BMP",  "*.bmp"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("ALL File",  "*"));
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home")));
    }
}
