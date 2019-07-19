package application;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// Open a new window that displays the wavform
public class Wavform {

    private int height = 600;
    private int width = 1200;
    private File file;

    public Wavform(File file) throws IOException, UnsupportedAudioFileException {
        this.file = file;
        drawWavform(FileReader.readWav(file));
    }

    private void drawWavform(int[] data) throws IOException, UnsupportedAudioFileException {

        List<Integer> list = Arrays.stream(data).boxed().collect(Collectors.toList());
        int max_value = Collections.max(list);
        int samplingStep = data.length / width; // As the screen resolution is finite, the sample displayed needs to fit in the display window.
        double time = FileReader.time;

        Stage stage = new Stage();
        stage.setTitle("Waveform Drawer");

        Text text1 = new Text("Number of samples of input file: " + data.length);
        text1.setX(width - 300);
        text1.setY(20);
        Text text2 = new Text("Number of samples of this waveform: " + data.length / samplingStep);
        text2.setX(width - 300);
        text2.setY(40);
        Text text3 = new Text("The maximum value: " + max_value);
        text3.setX(width - 300);
        text3.setY(60);

        NumberAxis xAxis = new NumberAxis(0, time, 0.05);
        xAxis.setLabel("Seconds");
        NumberAxis yAxis = new NumberAxis(-max_value-1000, max_value+1000, 2000);
        yAxis.setLabel("Voltage");

        LineChart linechart = new LineChart(xAxis, yAxis);
        linechart.setMinHeight(600);
        linechart.setMinWidth(1200);
        linechart.setCreateSymbols(false);
        linechart.setStyle("CHART_COLOR_1: #00006f");

        XYChart.Series series = new XYChart.Series();
        series.setName("Waveform of " + file.getName());

        // Draw a vertical line by locating two points: one on the x-axis and the other on the specific y with the same x.
        double x = 0;
        for(int i = 0; i <= data.length; i += samplingStep){
            series.getData().add(new XYChart.Data(x, 0));
            series.getData().add(new XYChart.Data(x, data[i]));
            x += time / (data.length / samplingStep);
        }
        linechart.getData().add(series);

        Group root = new Group(linechart, text1, text2, text3);

        stage.setScene(new Scene(root, width, height));
        stage.show();
    }
}
