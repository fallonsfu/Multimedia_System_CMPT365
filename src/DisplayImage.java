package application;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DisplayImage {

    private int height = 600;
    private int width = 1200;
    private int imageWidth; // horizontal resolution
    private int imageHeight; // vertical resolution
    private int imageNum;
    private Stage stage = new Stage();
    private StackPane pane1 = new StackPane();
    private StackPane pane2 = new StackPane();
    Text ratio = new Text();
    Text label1 = new Text();
    Text label2 = new Text();
    private PixelColor[][] uncompressed;
    private PixelColor[][] compressed;
    private double compressRatio;

    public DisplayImage(LossyImageCoding compressor, int count) {
        uncompressed = compressor.uncompressed;
        compressed = compressor.compressed;
        imageWidth = compressor.imageWidth;
        imageHeight = compressor.imageHeight;
        compressRatio = compressor.compressRatio;
        imageNum = count;
    }

    // Display the original colored image.
    private void drawOriginal() {

        WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < imageHeight; y++){
            for (int x = 0; x < imageWidth; x++)
                pixelWriter.setColor(x, y, uncompressed[y][x].getColor()); // write to a pixel with the same color as original
        }

        ImageView imageView = new ImageView(writableImage);
        pane1.getChildren().add(imageView);

    }

    private void drawCompressed() {

        WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < imageHeight; y++){
            for (int x = 0; x < imageWidth; x++)
                pixelWriter.setColor(x, y, compressed[y][x].getColor()); // write to a pixel with the same color as original
        }

        ImageView imageView = new ImageView(writableImage);
        pane2.getChildren().add(imageView);
    }

    public void drawImage() {
        stage.setTitle("Image Displayer");

        if(imageNum == 2) {
            drawOriginal();
            drawCompressed();
            ratio.setText("Lossy Compression Ratio: " + compressRatio);
            label1.setText("Uncompressed Image");
            label2.setText("Compressed Image");
            ratio.setY(25);
            ratio.setX(900);
            label1.setY(height - (height - imageHeight) / 2 + 20);
            label1.setX(width * 0.25 - 50);
            label2.setY(height - (height - imageHeight) / 2 + 20);
            label2.setX(width * 0.75 - 50);
            pane1.setLayoutX((width / 2 - imageWidth) / 2);
            pane1.setLayoutY((height - imageHeight) / 2);
            pane2.setLayoutX((width / 2 - imageWidth) / 2 + width / 2);
            pane2.setLayoutY((height - imageHeight) / 2);
        }
        if(imageNum == 1) {
            drawCompressed();
            label1.setText("IM3 Image");
            label1.setY(height - (height - imageHeight) / 2 + 20);
            label1.setX(width / 2 - 30);
            pane2.setLayoutX((width - imageWidth) / 2);
            pane2.setLayoutY((height - imageHeight) / 2);
        }
        Group root = new Group(pane1, pane2, ratio, label1, label2);
        stage.setScene(new Scene(root, width, height));
        stage.show();
    }
}
