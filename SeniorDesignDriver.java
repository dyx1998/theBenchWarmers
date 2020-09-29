import javax.swing.*;

public class SeniorDesignDriver
{
    public static void main(String[] args)
    {
        controlPanel panel = new controlPanel();
        panel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.setSize(425, 200);
        panel.setVisible(true);
    }
}
