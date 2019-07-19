package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class LossyImageCoding {

    public PixelColor[][] uncompressed;
    public PixelColor[][] compressed;
    public int imageWidth; // horizontal resolution
    public int imageHeight;
    public int headerSize = 4;
    public int subsampleRatio = 4;
    public double compressRatio;

    // YCbCr color space for downsampling
    class YCbCr {
        int y;
        int cb;
        int cr;
        public YCbCr(int[] ycbcr) {
            y = ycbcr[0];
            cb = ycbcr[1];
            cr = ycbcr[2];
        }
        public YCbCr() {}
        public void setY(int y) {this.y = y;}
        public void setCb(int cb) {this.cb = cb;}
        public void setCr(int cr) {this.cr = cr;}
    }

    public LossyImageCoding(File file) throws IOException {

        // Get the extension of the input file.
        String filename = file.getName();
        String extension = filename.substring(filename.indexOf("."));
        if(extension.equals( ".bmp")) {
            compressAndDisplay(file, filename);
        }
        else if(extension.equals(".IM3")) {
            decompressAndDisplay(file);
        }
    }

    public void compressAndDisplay(File file, String filename) throws IOException {
        uncompressed = FileReader.readBmp(file);
        imageWidth = uncompressed[0].length;
        imageHeight = uncompressed.length;

        // Convert from RGB to YCbCr
        YCbCr[][] color = convertToYCbCr();
        // 4:2:0 sampling of chroma components.
        byte[] sample = downSampling(color);
        // Add the information of image resolution into header.
        byte[] data = withHeader(sample);

        compressRatio = getRatio(file, data);
        // Save the file in current directory.
        Files.write(new File(filename.replaceFirst("[.][^.]+$", "")
                + ".IM3").toPath(), data);
        // Decompress the file to YCbCr space.
        YCbCr[][] yCbCrs = decompress(sample);
        // Convert from YCbCr to RGB.
        compressed = convertToRGB(yCbCrs);
        // Draw the images on screen.
        DisplayImage displayer = new DisplayImage(this, 2);
        displayer.drawImage();
    }

    public void decompressAndDisplay(File file) throws IOException {
        byte[] data = FileReader.readIM3(file);
        imageWidth = (data[0] & 0xFF) * 256 + (data[1] & 0xFF);
        imageHeight = (data[2] & 0xFF) * 256 + (data[3] & 0xFF);

        // Get rid of the header and retrieve the sample data.
        byte[] sample = Arrays.copyOfRange(data, headerSize, data.length);
        YCbCr[][] yCbCrs = decompress(sample);
        compressed = convertToRGB(yCbCrs);

        DisplayImage displayer = new DisplayImage(this, 1);
        displayer.drawImage();
    }

    public YCbCr[][] convertToYCbCr() {
        YCbCr[][] ycbcr = new YCbCr[imageHeight][imageWidth];
        for(int y = 0; y < imageHeight; y++) {
            for(int x = 0; x < imageWidth; x++)
                ycbcr[y][x] = new YCbCr(uncompressed[y][x].RGBtoYCbCr());
        }
        return ycbcr;
    }

    public PixelColor[][] convertToRGB(YCbCr[][] yCbCrs) {
        PixelColor[][] bitmap = new PixelColor[imageHeight][imageWidth];
        for(int y = 0; y < imageHeight; y++) {
            for(int x = 0; x < imageWidth; x++)
                bitmap[y][x] = PixelColor.YCbCrtoRGB(yCbCrs[y][x]);
        }
        return bitmap;
    }

    // Quantization that sets the data value to range (0, 15).
    public int quantize(int num) {
        return (int)Math.floor(num / 16.0);
    }

    // Reconstruct the data value to range (0, 255)
    public int inverseQuantize(int num) {
        return (num * 16  + 8);
    }

    // Every quantized data value is represented by 4 bits,
    // combine every two of them into one byte.
    public byte[] reduceTo4Bits(byte[] sample) {
        byte[] newSample = new byte[sample.length/2];
        int index = 0;
        for(int i = 0; i < sample.length; i+=2) {
            newSample[index++] = (byte)((sample[i]<<4) + sample[i+1]);
        }
        return newSample;
    }

    // Every byte contains two 4-bit data value, separate
    // them out as two single byte.
    public byte[] expandTo8bits(byte[] sample) {
        int index = 0;
        byte[] newSample = new byte[sample.length * 2];
        for(int i = 0; i < sample.length; i++) {
            int left = (sample[i] & 0xFF) >> 4;
            int right = sample[i] & 0x0F;
            newSample[index++] = (byte)inverseQuantize(left);
            newSample[index++] = (byte)inverseQuantize(right);
        }
        return newSample;
    }

    // Y component is reserved, while Cb and Cr components
    // have only one sample for each for every four pixels.
    public byte[] downSampling(YCbCr[][] ycbcr) {
        int length = imageHeight * imageWidth;
        byte[] sample = new byte[(int)(length * 1.5)];
        int index = 0;
        for(int i = 0; i < imageHeight; i++) {
            for(int j = 0; j < imageWidth; j++)
                sample[index++] = (byte)quantize(ycbcr[i][j].y);
        }
        for(int i = 0; i < imageHeight; i+=2) {
            for(int j = 0; j < imageWidth; j+=2) {
                int cb = (ycbcr[i][j].cb + ycbcr[i][j+1].cb + ycbcr[i+1][j].cb + ycbcr[i+1][j+1].cb) / 4;
                int cr = (ycbcr[i][j].cr + ycbcr[i][j+1].cr + ycbcr[i+1][j].cr + ycbcr[i+1][j+1].cr) / 4;
                sample[index] = (byte)quantize(cb);
                sample[index + length / subsampleRatio] = (byte)quantize(cr);
                index += 1;
            }
        }
        return reduceTo4Bits(sample);
    }

    public YCbCr[][] decompress(byte[] newSample) {
        byte[] sample = expandTo8bits(newSample);
        int length = imageHeight * imageWidth;
        YCbCr[][] ycbcr = new YCbCr[imageHeight][imageWidth];
        int index = 0;
        for(int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {
                ycbcr[i][j] = new YCbCr();
                ycbcr[i][j].setY(sample[index++] & 0xff);
            }
        }
        for(int i = 0; i < imageHeight; i+=2) {
            for (int j = 0; j < imageWidth; j+=2) {
                ycbcr[i][j].setCb((int) sample[index] & 0xff);
                ycbcr[i][j+1].setCb((int) sample[index] & 0xff);
                ycbcr[i+1][j].setCb((int) sample[index] & 0xff);
                ycbcr[i+1][j+1].setCb((int) sample[index] & 0xff);

                ycbcr[i][j].setCr((int) sample[index + length/4] & 0xff);
                ycbcr[i][j+1].setCr((int) sample[index + length/4] & 0xff);
                ycbcr[i+1][j].setCr((int) sample[index + length/4] & 0xff);
                ycbcr[i+1][j+1].setCr((int) sample[index + length/4] & 0xff);
                index += 1;
            }
        }
        return ycbcr;
    }

    // Put into compressed data the header.
    public byte[] withHeader(byte[] sample) {
        byte[] data = new byte[sample.length + headerSize];
        byte[] header = new byte[headerSize];
        header[0] = (byte)((imageWidth & 0xFFFF) >> 8);
        header[1] = (byte)(imageWidth & 0x00FF);
        header[2] = (byte)((imageHeight & 0xFFFF) >> 8);
        header[3] = (byte)(imageHeight & 0x00FF);
        System.arraycopy(header, 0, data, 0, headerSize);
        System.arraycopy(sample, 0, data, headerSize, sample.length);
        return data;
    }

    public double getRatio(File file, byte[] data) {
        return file.length() / (double)data.length;
    }

    public void measureMSE() {
        int sum = 0;
        for(int i = 0; i < imageHeight; i++){
            for(int j = 0; j < imageWidth; j++) {
                sum += Math.pow(uncompressed[i][j].red - compressed[i][j].red, 2);
            }
        }
        System.out.println("MSE: " + sum/(256*256));
    }
}
