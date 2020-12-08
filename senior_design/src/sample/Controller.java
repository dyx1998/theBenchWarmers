package sample;

import com.fazecast.jSerialComm.SerialPort;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart ;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javax.sound.sampled.*;

public class Controller
{
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private MenuItem aboutProject;

    @FXML
    private Button chooseMediaButton;

    @FXML
    private Label nowPlaying ;

    @FXML
    private MediaView mv ;

    @FXML
    private Slider progressBar ;

    @FXML
    private ToggleButton broadCastToggle ;

    @FXML
    private TextField timeInput ;

    @FXML
    private Slider pitchSlider;

    @FXML
    private Label pitchLabel;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Label volumeLabel;

    @FXML
    private TextField vcoField;

    @FXML
    private TextField frequencyField;

    @FXML
    private Hyperlink developerLink;

    @FXML
    private LineChart<String, Number> plot;


    private MediaPlayer mediaPlayer = null ;

    private Media media ;

    private static final int MAX_FREQ = 35 ;

    private static final int MIN_FREQ = 10 ;

    private static final int MAX_AMP = 30 ;

    private SerialPort sp = SerialPort.getCommPort(" ");


    @FXML
    public void goAboutProject(ActionEvent event)
    {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("About_Project.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("About Ultrasonic Speaker");
            stage.setScene(new Scene(root1));
            stage.show();
        }
        catch (Exception e)
        {
            System.out.println("Cannot load new window");
        }
    }

    @FXML
    public void chooseMedia(ActionEvent event)
    {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null) ;
        String path = file.toURI().toString() ;

        if (path != null)
        {
            if (mediaPlayer!=null)
            {
                mediaPlayer.stop();
            }

            media = new Media(path) ;
            mediaPlayer = new MediaPlayer(media) ;
            mv.setMediaPlayer(mediaPlayer);
            nowPlaying.setText("Now Playing: " + file.getName()) ;

            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<javafx.util.Duration>()
            {
                @Override
                public void changed(ObservableValue<? extends javafx.util.Duration> observable, javafx.util.Duration oldValue, javafx.util.Duration newValue)
                {
                    progressBar.setValue(newValue.toSeconds());
                }
            });

            progressBar.setOnMousePressed(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event) {
                    mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
                }
            });

            progressBar.setOnMouseDragged(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event) {
                    mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
                }
            });

            mediaPlayer.setOnReady(new Runnable() {
                @Override
                public void run() {
                    javafx.util.Duration total = media.getDuration();
                    progressBar.setMax(total.toSeconds());
                }
            });

            mediaPlayer.play();
        }
    }

    @FXML
    public void play(ActionEvent event)
    {
        mediaPlayer.play();
    }

    @FXML
    public void pause(ActionEvent event)
    {
        mediaPlayer.pause();
    }

    @FXML
    public void skip10(ActionEvent event)
    {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(10)));
    }

    @FXML
    public void back10(ActionEvent event)
    {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(-10)));
    }


    private boolean isTimeInputValid()
    {
        if ( timeInput.getText().matches("[0-9]+") )
        {
            return true ;
        }
        else
        {
            timeInput.setText("invalid");
            return false ;
        }
    }

    @FXML
    public void broadCastControl(ActionEvent event)
    {
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,44100,16,2,4,44100,false);

        DataLine.Info infoS = new DataLine.Info(SourceDataLine.class, format) ;

        DataLine.Info infoT = new DataLine.Info(TargetDataLine.class, format) ;

        final ByteArrayOutputStream out = new ByteArrayOutputStream() ;
        try
        {
            final SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(infoS) ;
            sourceLine.open();

            final TargetDataLine targetLine = (TargetDataLine)AudioSystem.getLine(infoT) ;
            targetLine.open();

            Thread sourceThread = new Thread()
            {
                @Override public void run()
                {
                    sourceLine.start();
                    while(true)
                    {
                        sourceLine.write(out.toByteArray(), 0, out.size()) ;
                    }
                }
            };

            Thread targetThread = new Thread()
            {
                @Override public void run()
                {
                    targetLine.start();
                    byte[] data = new byte[targetLine.getBufferSize()/5] ;
                    int readBytes ;
                    while (true)
                    {
                        readBytes = targetLine.read(data, 0, data.length) ;
                        out.write(data, 0, readBytes);
                    }
                }
            };

            if (broadCastToggle.isSelected())
            {
                if ( isTimeInputValid() )
                {
                    broadCastToggle.setText("STOP  ");

                    targetThread.start();

                    System.out.println("speaking and recording now....");

                    int t = Integer.parseInt(timeInput.getText()) * 1000 ;

                    Thread.sleep(t);

                    targetLine.stop();
                    targetLine.close();

                    System.out.println("End recording");
                    System.out.println("Start playback...");

                    sourceThread.start();

                    Thread.sleep(t);

                    sourceLine.stop();
                    sourceLine.close();
                    System.out.println("play ended");
                    broadCastToggle.setText("RECORD");
                }
            }
        }
        catch (LineUnavailableException lue)
        {
            lue.printStackTrace();
        }
        catch (InterruptedException ie){
            ie.printStackTrace();
        }
    }

    @FXML
    public void showPlot(ActionEvent event)
    {
        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>() ;

        double f = 0.0 ;
        double a = 0.0 ;

        double y = 0 ;
        double x = 0 ;
        double dx = 2 ;

        if (frequencyField.getText().matches("[-+]?\\d*\\.?\\d+") )
        {
            f = Double.parseDouble(frequencyField.getText()) ;

            if (vcoField.getText().matches("[-+]?\\d*\\.?\\d+"))
            {
                a = Double.parseDouble(vcoField.getText()) ;

                // TO DO:
                Double freqInput = f ;
                Double ampInput = a ;
                /*
                try {
                    sp.getOutputStream().write(freqInput.byteValue());
                    sp.getOutputStream().write(ampInput.byteValue());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    sp.getOutputStream().flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                System.out.println("Sent carrier frequency: " + freqInput + " kHz") ;
                System.out.println("Sent VCO amplitude: " + ampInput + " V") ;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } */

                double angel = 2 * Math.PI * f/1000 ;

                while (x < 60)
                {
                    y = a * Math.sin(angel * x) ;

                    series.getData().add(new XYChart.Data<String, Number>(Double.toString(Math.round(x)), y)) ;

                    x += dx ;
                }
            }
            else
            {
                vcoField.setText("please enter valid number");
            }
        }
        else
        {
            frequencyField.setText("please enter valid number");
        }
        plot.getData().add(series) ;
    }

    @FXML
    public void goDeveloperLink(ActionEvent event)
    {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("developerGit.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Developer GitHub");
            stage.setScene(new Scene(root1));
            stage.show();
        }
        catch (Exception e)
        {
            System.out.println("Cannot load new window");
        }
    }

    @FXML
    public void initialize()
    {
        assert aboutProject != null : "fx:id=\"aboutProject\" was not injected: check your FXML file 'sample.fxml'.";
        assert pitchSlider != null : "fx:id=\"pitchSlider\" was not injected: check your FXML file 'sample.fxml'.";
        assert vcoField != null : "fx:id=\"vcoField\" was not injected: check your FXML file 'sample.fxml'.";
        assert volumeSlider != null : "fx:id=\"volumeSlider\" was not injected: check your FXML file 'sample.fxml'.";
        assert volumeLabel != null : "fx:id=\"volumeLabel\" was not injected: check your FXML file 'sample.fxml'.";
        assert chooseMediaButton != null : "fx:id=\"chooseAudioButton\" was not injected: check your FXML file 'sample.fxml'.";
        assert pitchLabel != null : "fx:id=\"pitchLabel\" was not injected: check your FXML file 'sample.fxml'.";
        assert frequencyField != null : "fx:id=\"frequencyField\" was not injected: check your FXML file 'sample.fxml'.";

        if (sp.closePort()) {
            System.out.println("Port is closed");
        } else {
            System.out.println("Failed to close port");
        }

        // default connection settings for Arduino
        sp.setComPortParameters(9600, 8, 1, 0);

        // block until bytes can be written
        sp.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

        // test if the Arduino port is opened
        if (sp.openPort())
        {
            System.out.println("Port is opened");
        }
        else
        {
            System.out.println("Failed to open port");
        }

        pitchSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                int value = (int) Math.round(pitchSlider.getValue());

                pitchLabel.setText("Pitch:  " + value);

                Double intValue = new Double(value * (MAX_FREQ - MIN_FREQ) / pitchSlider.getMax());
                /*
                try {
                    sp.getOutputStream().write(intValue.byteValue());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    sp.getOutputStream().flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                System.out.println("Sent frequency: " + intValue + " kHz");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } */
            }
        });

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                int value = (int) Math.round(volumeSlider.getValue());

                volumeLabel.setText("Pitch:  " + value);

                Double intValue = new Double(value * (MAX_AMP) / volumeSlider.getMax());
                /*
                try {
                    sp.getOutputStream().write(intValue.byteValue());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    sp.getOutputStream().flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                System.out.println("Sent vco amplitude: " + intValue+ " V");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }*/
            }
        });
    }

}



