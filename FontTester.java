import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.font.TextLayout;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Shape;
import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.BasicStroke;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

//sets up graphics2d
public class FontTester{
  String testString = "m";
  static Font font = new Font("Impact", Font.PLAIN, 10);
  static FontMetrics fm = new FontMetrics(font);
   protected static void setFontSize(int size){
       font = new Font("Impact", Font.PLAIN, size);
   }

   
   public static void main(String args[]){
   System.out.println(FontTester.fm.getStringWidth(testString));
   
   }
   
}