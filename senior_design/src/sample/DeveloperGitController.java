package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public class DeveloperGitController
{

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private WebView view ;

    @FXML
    public void initialize()
    {
        final WebEngine web = view.getEngine() ;

        String link = "https://github.com/dyx1998/theBenchWarmers" ;

        web.load(link);
    }
}
