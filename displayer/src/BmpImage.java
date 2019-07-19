package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.LineChart;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import javafx.scene.paint.Color;

// Open a new window that displays bmp images.
public class BmpImage {

    private int height = 600;
    private int width = 1200;
    private int pageNumber = 0;
    private final int NUM_PAGES = 5;
    private int imageWidth; // horizontal resolution
    private int imageHeight; // vertical resolution
    private int[][] dither = {{0,8,2,10},{12,4,14,6},{3,11,1,9},{15,5,13,7}}; // 4*4 dithering matrix
//    private int[][] dither = {{0,4,2,6},{8,1,10,4},{3,7,1,5},{11,2,9,3}};
    
    private Stage stage = new Stage();
    private StackPane pane = new StackPane();
    private Button nextPageButton = new Button("next page");
    private PixelColor[][] bitmap;

    public BmpImage(File file) throws IOException{

        bitmap = FileReader.readBmp(file);
        imageWidth = bitmap[0].length;
        imageHeight = bitmap.length;
        drawImage();
    }

    // When click on the button, this function is called which increments the page number.
    public void nextPage() {

        System.out.println("PageNumber " + (pageNumber + 1));
        pageNumber = (pageNumber + 1) % NUM_PAGES;

        switch(pageNumber) {
            case 1: drawOriginal();
                break;
            case 2: drawHistogram();
                break;
            case 3: drawBrighter();
                break;
            case 4: drawGrayscale();
                break;
            case 0: orderedDither(); // will be fifth page.  5 == 0 (mod 5)
                break;
        }
    }

    // Display the original colored image.
    private void drawOriginal() {

        pane.setLayoutX((width - imageWidth) / 2);
        pane.setLayoutY((height - imageHeight) / 2);

        WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < imageHeight; y++){
            for (int x = 0; x < imageWidth; x++)
                pixelWriter.setColor(x, y, bitmap[y][x].getColor()); // write to a pixel with the same color as original
        }

        ImageView imageView = new ImageView(writableImage);
        pane.getChildren().add(imageView);
        Group root = new Group(nextPageButton, pane);
        stage.setScene(new Scene(root, width, height));
    }

    // Display the image that is 1.5 times brighter.
     private void drawBrighter() {

        pane.setLayoutX((width - imageWidth) / 2);
        pane.setLayoutY((height - imageHeight) / 2);

        WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < imageHeight; y++){
            for (int x = 0; x < imageWidth; x++)
                pixelWriter.setColor(x, y, bitmap[y][x].getBrighterColor()); // write to a pixel with a equally brighter color
        }

        ImageView imageView = new ImageView(writableImage);
        pane.getChildren().add(imageView);
        Group root = new Group(nextPageButton, pane);
        stage.setScene(new Scene(root, width, height));
    }

    // Display the greyscale image of the original.
    private void drawGrayscale() {

        pane.setLayoutX((width - imageWidth) / 2);
        pane.setLayoutY((height - imageHeight) / 2);

        WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < imageHeight; y++){
            for (int x = 0; x < imageWidth; x++)
                pixelWriter.setColor(x, y, bitmap[y][x].asGrayscale()); // write to a pixel with only 8-bit grayscale color
        }

        ImageView imageView = new ImageView(writableImage);
        pane.getChildren().add(imageView);
        Group root = new Group(nextPageButton, pane);
        stage.setScene(new Scene(root, width, height));
    }

    // Apply ordered dithering on the greyscale image.
    private void orderedDither() {
        
        pane.setLayoutX((width - imageWidth) / 2);
        pane.setLayoutY((height - imageHeight) / 2);

        WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        // Compare every color intensity of pixel in a 4*4 block of pixels with the corresponding entry of the dithering matrix.
        // Draw a white dot if is greater, otherwise draw a black dot.
        for (int y = 0; y < imageHeight; y++){
            for (int x = 0; x < imageWidth; x++){
                int i = y % 4;
                int j = x % 4;
                int intensity = bitmap[y][x].grayscale / (256/17);
                if(intensity > dither[i][j])
                    pixelWriter.setColor(x, y, Color.WHITE);
                else
                    pixelWriter.setColor(x, y, Color.BLACK);
            }
        }

        ImageView imageView = new ImageView(writableImage);
        pane.getChildren().add(imageView);
        Group root = new Group(nextPageButton, pane);
        stage.setScene(new Scene(root, width, height));
    }

    // Display each channel (R,G,B) in seperate histograms.
    private void drawHistogram() {

        int[] countRed = new int[256];
        int[] countGreen = new int[256];
        int[] countBlue = new int[256];

        // Count the frequency of every rgb value(from 1 to 255).
        for(int i = 0; i < imageHeight; i++) {
            for(int j = 0; j < imageWidth; j++){
                countRed[bitmap[i][j].red] += 1;
                countGreen[bitmap[i][j].green] += 1;
                countBlue[bitmap[i][j].blue] += 1;
            }
        }
        NumberAxis xAxis1 = new NumberAxis();
        NumberAxis yAxis1 = new NumberAxis();
        NumberAxis xAxis2 = new NumberAxis();
        NumberAxis yAxis2 = new NumberAxis();
        NumberAxis xAxis3 = new NumberAxis();
        NumberAxis yAxis3 = new NumberAxis();
        
        LineChart histogramR = new LineChart(xAxis1, yAxis1);
        LineChart histogramG = new LineChart(xAxis2, yAxis2);
        LineChart histogramB = new LineChart(xAxis3, yAxis3);
        
        XYChart.Series seriesR = new XYChart.Series();
        XYChart.Series seriesG = new XYChart.Series();
        XYChart.Series seriesB = new XYChart.Series();
        seriesR.setName("Red");
        seriesG.setName("Green");
        seriesB.setName("Blue");
        
        histogramR.setTitle("Histogram of Red");
        histogramR.setCreateSymbols(false);
        histogramG.setTitle("Histogram of Green");
        histogramG.setCreateSymbols(false);
        histogramB.setTitle("Histogram of Blue");
        histogramB.setCreateSymbols(false);
        
        histogramR.setMaxHeight(300);
        histogramR.setMaxWidth(400);
        histogramG.setMaxHeight(300);
        histogramG.setMaxWidth(400);
        histogramB.setMaxHeight(300);
        histogramB.setMaxWidth(400);
        
        histogramR.setLayoutX(0);
        histogramR.setLayoutY(100);
        histogramG.setLayoutX(400);
        histogramG.setLayoutY(100);
        histogramB.setLayoutX(800);
        histogramB.setLayoutY(100);
        
        histogramR.setStyle("CHART_COLOR_1: #800000");
        histogramG.setStyle("CHART_COLOR_1: #008000");
        histogramB.setStyle("CHART_COLOR_1: #000080");
        
        for(int x = 0; x < countRed.length; x++){
            seriesR.getData().add(new XYChart.Data(x, 0));
            seriesG.getData().add(new XYChart.Data(x, 0));
            seriesB.getData().add(new XYChart.Data(x, 0));
            seriesR.getData().add(new XYChart.Data(x, countRed[x]));
            seriesG.getData().add(new XYChart.Data(x, countGreen[x]));
            seriesB.getData().add(new XYChart.Data(x, countBlue[x]));
        }
        histogramR.getData().add(seriesR);
        histogramG.getData().add(seriesG);
        histogramB.getData().add(seriesB);
        
        Group root = new Group(nextPageButton, histogramR, histogramG, histogramB);
        stage.setScene(new Scene(root, width, height));
    }

    private void drawImage() {

        stage.setTitle("Image Displayer");

        EventHandler<ActionEvent> callback = event->{nextPage();};
        nextPageButton.setOnAction(callback);
        nextPageButton.setLayoutX(1100);
        nextPageButton.setLayoutY(25);

        nextPage();   //Start from page 1, initially at page 0
        stage.show();
    }
}
