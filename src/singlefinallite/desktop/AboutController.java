package singlefinallite.desktop;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * The Controller Class for the about page.
 * @author Kyle DeWeese
 */
public class AboutController
   implements Initializable
{
   /**
    * logoHolder - location in the FXML where the image will be placed
    */
   @FXML
   private ImageView logoHolder = new ImageView();

   /**
    * initialize() - the scene initialization.
    *
    * @param url resource urls (not used)
    * @param rb  resource bundles (not used)
    */
   @Override
   public void initialize(URL url, ResourceBundle rb)
   {
      try
      {
         //"/src/singlefinallite/desktop/rocketLogo.png"
         Image logo = new Image(getClass().getResource("SFLLogo.png").toExternalForm());
         logoHolder.setImage(logo);
      }
      catch(Exception e)              
      {
         System.out.println(e.getMessage());
      }
   }
}