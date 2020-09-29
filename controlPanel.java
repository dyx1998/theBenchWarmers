import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;


public class controlPanel extends JFrame
{
    private JSlider carrierFrequencySlider ;
    private JSlider VCO_amplitudeSlider ;
    private JSlider volumeSlider ;

    private JLabel frequencyLabel ;
    private JLabel amplitudeLabel ;
    private JLabel volumeLabel ;

    public controlPanel()
    {
        super("Ultrasonic Speaker Control Panel") ;

        setLayout(new FlowLayout()) ;

        carrierFrequencySlider = new JSlider(JSlider.HORIZONTAL, 15, 30, 15) ;
        carrierFrequencySlider.setMajorTickSpacing(1) ;
        carrierFrequencySlider.setPaintTicks(true) ;
        frequencyLabel = new JLabel("Carrier Frequency: 15 kHz") ;


        VCO_amplitudeSlider = new JSlider(JSlider.HORIZONTAL, 0, 30, 0) ;
        VCO_amplitudeSlider.setMajorTickSpacing(5) ;
        VCO_amplitudeSlider.setPaintTicks(true) ;
        amplitudeLabel = new JLabel("VCO input voltage amplitude: 0 cm") ;


        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 90, 0) ;
        volumeSlider.setMajorTickSpacing(10) ;
        volumeSlider.setPaintTicks(true) ;
        volumeLabel = new JLabel("Volume: 0 dB") ;


        ChangeListener listener1 = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = carrierFrequencySlider.getValue() ;
                frequencyLabel.setText("Carrier Frequency: " + value + " kHz") ;

                // TO DO:
            }
        } ;

        ChangeListener listener2 = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = VCO_amplitudeSlider.getValue() ;
                amplitudeLabel.setText("VCO input voltage amplitude: " + value + " cm") ;

                // TO DO:
            }
        } ;

        ChangeListener listener3 = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = volumeSlider.getValue() ;
                volumeLabel.setText("Volume: " + value + " dB") ;

                // TO DO:
            }
        } ;

        carrierFrequencySlider.addChangeListener(listener1);
        VCO_amplitudeSlider.addChangeListener(listener2);
        volumeSlider.addChangeListener(listener3);

        add(carrierFrequencySlider) ;
        add(frequencyLabel) ;

        add(VCO_amplitudeSlider) ;
        add(amplitudeLabel) ;

        add(volumeSlider) ;
        add(volumeLabel) ;
    }
}