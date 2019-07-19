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

    public int[] RGBtoYCbCr() {
        int y = (int)(0.299 * red + 0.587 * green + 0.114 * blue);
        int cb = (int)(128 - 0.169 * red - 0.331 * green + 0.50 * blue);
        int cr = (int)(128 + 0.500 * red - 0.419 * green - 0.081 * blue);
        int[] ycbcr = {y, cb, cr};
        return ycbcr;
    }

    public static PixelColor YCbCrtoRGB(LossyImageCoding.YCbCr yCbCr) {
        int r = (int)(yCbCr.y + 1.402 * (yCbCr.cr - 128));
        int g = (int)(yCbCr.y - 0.344 * (yCbCr.cb - 128) - 0.714 * (yCbCr.cr - 128));
        int b = (int)(yCbCr.y + 1.772 * (yCbCr.cb - 128));
        r = round(r);
        g = round(g);
        b = round(b);
        return new PixelColor(r, g, b);
    }

    public static int round(int num) {
        if(num > 255)
            num = 255;
        else if(num < 0)
            num = 0;
        return num;
    }
}
