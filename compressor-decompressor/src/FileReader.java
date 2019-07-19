package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class FileReader {

    public static int frameLength;
    public static int frameSize;
    public static double time; // duration of the audio.
    public final static int headerSize = 54;
    public final static int bitmap_width_index = 18; // index of byte that tells the horizontal resolution in standard wav file.
    public final static int bitmap_height_index = 22; //index of byte that tells the vertical resolution in standard wav file.
    public static int width; // number of pixel in x dimention.
    public static int height; // number of pixel in y dimention.

    // Load into the program every byte of the input WAV file.
    public static byte[] readWavToByte(File wavFile) throws IOException, UnsupportedAudioFileException {

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);
        frameLength = (int) audioInputStream.getFrameLength();
        frameSize = audioInputStream.getFormat().getFrameSize();
        time = (frameLength + 0.0) / audioInputStream.getFormat().getFrameRate();

        byte[] bytes = new byte[frameLength * frameSize];
        audioInputStream.read(bytes);

        return bytes;
    }

    public static int[] readWavToInt(File wavFile) throws IOException, UnsupportedAudioFileException {
        byte[] bytes = readWavToByte(wavFile);
        return bytesToFrame_mono(bytes);
    }

    // Load into the program every byte of the input BMP file and convert to a bitmap containing color information of pixels.
    public static PixelColor[][] readBmp(File bmpFile) throws IOException {

        FileInputStream fileInputStream = new FileInputStream(bmpFile);

        byte[] bytes = fileInputStream.readAllBytes();
        int[] data = new int[bytes.length];

        for(int i = 0; i < bytes.length; i++) {
            data[i] = bytes[i] & 0xff;
        }

        width = data[bitmap_width_index] + data[bitmap_width_index+1] * 256;
        height = data[bitmap_height_index] + data[bitmap_height_index+1] * 256;

        // Get rid of the header in the raw data file.
        int[] bitarray = Arrays.copyOfRange(data, headerSize, data.length);
        PixelColor[][] bitmap = new PixelColor[height][width];

        // Fill in the 2-d bitmap from the 1-d data array.
        int index = 0;
        for(int i = height-1; i >= 0; i--){
            for(int j = 0; j < width; j++){
                bitmap[i][j] = new PixelColor(bitarray[index+2], bitarray[index+1], bitarray[index]);
                index += 3;
            }
        }

        return bitmap;
    }

    public static byte[] readIM3(File im3File) throws IOException{

        FileInputStream fileInputStream = new FileInputStream(im3File);
        byte[] bytes = fileInputStream.readAllBytes();
        return bytes;
    }

    // Take every two bytes as an integer sample value (for mono).
    private static int[] bytesToFrame_mono(byte[] bytes) {

        int[] data = new int[frameLength];
        int index = 0;
        for (int i = 0; i < bytes.length; i += 2) {
            int lowerBits = (int) bytes[i];
            int upperBits = (int) bytes[i + 1];
            int sample = (upperBits << 8) + (lowerBits & 0x00ff);
            data[index] = sample;
            index++;
        }
        return data;
    }
}
