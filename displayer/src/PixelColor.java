package application;

import javafx.scene.paint.Color;

public class PixelColor {

    public int red;
    public int green;
    public int blue;
    public int grayscale;

    public PixelColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        grayscale = (int)(0.299*red + 0.587*green + 0.114*blue);
    }

    public Color getColor() {
        Color color = Color.rgb(red, green, blue);
        return color;
    }

    public Color asGrayscale() {
        return Color.grayRgb(grayscale);
    }

    // Color value of each channel is increased as 1.5 times larger as original, but not exceed 255.
    public Color getBrighterColor() {
        int red = (int)(this.red * 1.5);
        int green = (int)(this.green * 1.5);
        int blue = (int)(this.blue * 1.5);
        if(red > 255)
            red = 255;
        if(green > 255)
            green = 255;
        if(blue > 255)
            blue = 255;
        Color color = Color.rgb(red, green, blue);
        return color;
    }
}
