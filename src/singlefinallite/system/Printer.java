package singlefinallite.system;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Printer utility class. Uses the AWT library to print a string.
 * This printer is hard coded to landscape mode and only writes 
 * strings
 *
 * @author Chris Vergaray
 */
public class Printer
   implements Printable
{
   /**
    * Member variable of the String to print
    */
   private String mStringToPrint = "";

   /**
    * Creates a new Printer object.
    *
    * @param pToPrint The string to be printed
    */
   public Printer(String pToPrint)
   {
      mStringToPrint = pToPrint;
   }

   /**
    * Sets the string to be printed to the given string
    *
    * @param pToPrint The string to print
    */
   public void setStringToPrint(String pToPrint)
   {
      mStringToPrint = pToPrint;
   }

   /**
    * Overrided print function. Is called to actually run the printer 
    * and create the printed page 
    *
    * @param g A graphics element to be printed
    * @param pf A standard page format
    * @param page An integer indicating which page is to be printed
    *
    * @return An integer indicating whether the page is included or not
    *
    * @throws PrinterException Exception if there was some issue printing.
    */
   @Override
   public int print(Graphics g, PageFormat pf, int page)
      throws PrinterException
   {
      System.out.println("Inside needed Print Function");

      // We have only one page, and 'page'
      // is zero-based
      if (page != 0)
      {
         return NO_SUCH_PAGE;
      }

      /* User (0,0) is typically outside the imageable area, so we must
       * translate by the X and Y values in the PageFormat to avoid clipping
       */
      java.awt.Graphics2D g2d = (Graphics2D) g;
      g2d.translate(pf.getImageableX(), pf.getImageableY());

      Font f = new Font("Monospaced", Font.PLAIN, 10);
      g.setFont(f);

      int x = 50;
      int y = 50;
      /* Now we perform our rendering */
      System.out.println("Rendering");

      for (String line : mStringToPrint.split("\n"))
      {
         g.drawString(line, x, y += g.getFontMetrics().getHeight());
      }

      /* tell the caller that this page is part of the printed document */
      System.out.println("Page Exists\n");

      return PAGE_EXISTS;
   }

   /**
    * Function to perform the print action. Launches the dialog and prints.
    * Exit codes are returned so that the GUI can provide feedback to the
    * user.
    *
    * @return int indicating the status of the print
    */
   public int performPrint()
   {
      //Default exit code
      int exitCode = 3;
      java.awt.print.PrinterJob job = java.awt.print.PrinterJob.getPrinterJob();
      PageFormat pf = job.defaultPage();
      //Change the orientation to landscape
      pf.setOrientation(PageFormat.LANDSCAPE);
      job.setPrintable(this, pf);

      boolean ok = false;

      try
      {
	 //Get print options from the user
         ok = job.printDialog();
      }
      catch (Exception e)
      {
         System.out.println("Failed to launch dialog " + e.getMessage());
         exitCode = 4;
      }

      if (ok)
      {
         try
         {
            System.out.println("Made it to print area");
            job.print();
            System.out.println("Just Printed!");
         }
         catch (PrinterException ex)
         {
            System.out.println("Failed to print " + ex.getMessage());
            /* The job did not successfully complete */
            exitCode = 1;
         }
      }
      else
      {
         exitCode = 2;
      }

      return exitCode;
   }

   /**
    * Reads a file froma given URL and reads it in as a 
    * single string including newline characters
    *
    * @param filename String representing the URL to the fileName
    *
    * @return The string of all the text in the file
    */
   public static String readFile(String filename)
   {
      String content = null;
      File file = new File(filename); //for ex foo.txt

      try
      {
         FileReader reader = new FileReader(file);
         char[] chars = new char[(int) file.length()];
         reader.read(chars);
         content = new String(chars);
         reader.close();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

      return content;
   }

   /**
    * If there is a file indicated on the command line, it is printed.
    * 
    * @param args the command line arguments
    */
   public static void main(String[] args)
   {
      int exit = -1;
      System.out.println("Inside main printer!!");

      if (args.length > 0)
      {
         Printer myPrinter = new Printer(readFile(args[0]));
         exit = myPrinter.performPrint();
      }
      else
      {
         Printer myPrinter = new Printer("This is a test of the printer");
         exit = myPrinter.performPrint();
      }

      System.exit(exit);
   }
}
