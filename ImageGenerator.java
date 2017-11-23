import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.font.TextLayout;
import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.BasicStroke;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;



public class ImageGenerator{
  
  public static final String chosenFont = "Impact";
  public static final int xOffset = 20;//space between left and right edges of image
  public static final int yOffset = 4; //space between top and bottom edges of image
  public static final int lineSpacing = 1; //line spacing between text lines
  public static final double scaleX = 1.4;
  public static final double scaleY = 1.4;
  public static final double maxImageCoveragePerText = 0.4; //maximum amount of space a segment of text can cover
  // since the entire width of the image is used when writing text, this is really just a height limitation
  
  
  BufferedImage meme;
  Graphics2D textDrawer;
  String verb, prefix,topText,bottomText, outputName;
  int maxLines,maxTextHeight, maxTextWidth;
  FontMetrics metrics;
  
  public ImageGenerator(String sourceFileName, String verb, String prefix){
    this.verb = verb.toUpperCase(); // has to be uppercase
    this.prefix = prefix.toUpperCase();
    createTopText();
    createBottomText();
    outputName = "meme.png";
    try{
      meme = ImageIO.read(new File(sourceFileName));

    }
    catch(IOException e){
    }
          scaleImage();
     calcTextConstraints();
  }
  
    public ImageGenerator(String sourceFileName, String verb, String prefix, String outputName){
    this.verb = verb.toUpperCase(); // has to be uppercase
    this.prefix = prefix.toUpperCase();
    createTopText();
    createBottomText();
    this.outputName = outputName;
    try{
      meme = ImageIO.read(new File(sourceFileName));
    
    }
    catch(IOException e){
    }
      scaleImage();
       calcTextConstraints();
  }
  
  
    public void scaleImage(){

    AffineTransform aMatrix = new AffineTransform();
    aMatrix.scale(scaleX,scaleY);
    AffineTransformOp op = new AffineTransformOp(aMatrix, AffineTransformOp.TYPE_BILINEAR);
    meme = op.filter(meme,null);
  
    }
  
  
  
  //saves the image into a new file
  public void saveImage(){
    try{
      File outputFile = new File(outputName);
      ImageIO.write(meme, "png", outputFile);
    }
    catch(IOException e){}
  }
  
  //creates strings to draw on the image
  public void createTopText(){
    topText ="I " + verb + " THEM. I " + verb + " THEM ALL.";
  }
  public void createBottomText(){
    bottomText ="AND NOT JUST THE " + prefix +"MEN, BUT THE " + prefix +"WOMEN AND THE "  + prefix + "CHILDREN TOO.";
  }
  
  
  public void setupGraphics(){
    textDrawer = meme.createGraphics();
    Font font = new Font(chosenFont, Font.BOLD, 90);
    textDrawer.setFont(font);
    textDrawer.setColor(Color.WHITE);
    textDrawer.setStroke(new BasicStroke(5));
  }
  
  public void adjustFontWidth(){//lowers the font size based on width
  getMetrics();
  calcMaxLines();
    //if the top text exceeds the image, make the font smaller
    //if the total width of the bottom text exceeds the max number of lines * width of image, make the font smaller
    //for this meme the top text should be on one line, bottom text uses full amount of space
    if((metrics.stringWidth(topText) > maxTextWidth) || (metrics.stringWidth(bottomText) > (maxLines * maxTextWidth)))
    {
     decrementFontSize();
      adjustFontWidth();
    }
  }
  
  public void adjustFontHeight(){ //lowers font size based on height
    getMetrics();
    calcMaxLines();
    if((maxLines*getFontHeight()) + (maxLines*lineSpacing) + yOffset > maxTextHeight){
      System.out.println(getFontHeight());
    decrementFontSize();
      adjustFontHeight();
    }
  }
  
  public void adjustFont(){
  adjustFontHeight();
  adjustFontWidth();
  }

  public void decrementFontSize(){
    Font font = new Font(chosenFont, Font.BOLD, textDrawer.getFont().getSize()-1);
    textDrawer.setFont(font);
  }
  
  public void getMetrics(){
  metrics = textDrawer.getFontMetrics(textDrawer.getFont());
  }
  
  public void calcMaxLines(){ //calculates how many lines of text can fit within the maximum allowed space
    maxLines = maxTextHeight/getFontHeight(); 
  }
  
  
  public int getFontHeight(){ //returns font height
    return metrics.getMaxAscent();
  }
  
  public void calcTextConstraints(){ //calculates max width and height of text 
  double temp = meme.getHeight()*maxImageCoveragePerText;
  maxTextHeight = (int)temp;
  maxTextWidth = meme.getWidth() - (xOffset*2);
  }
  
  public ArrayList<String> splitText(String text){ //splits text segments into smaller lines 
    ArrayList<String> arr = new ArrayList<String>();
    StringTokenizer tok = new StringTokenizer(text);
    String stringBuilder = "";
    String temp = "";
    
    while(tok.hasMoreTokens()){
      temp = tok.nextToken();
      
      if(metrics.stringWidth(stringBuilder+" " +temp) > maxTextWidth){
        arr.add(stringBuilder);
        stringBuilder = temp;
      }
      
      else{
        stringBuilder = stringBuilder + " " + temp;
        if(!tok.hasMoreTokens()){ //cant forget about the last sentence
          arr.add(stringBuilder);
        }
      } 
    }
  return arr;
  }
  
  public void writeTopText(){
    int x = xOffset;
    int y = yOffset + getFontHeight();
    
    TextLayout outlineLayout = new TextLayout(topText,textDrawer.getFont(),textDrawer.getFontRenderContext());
    textDrawer.setColor(Color.BLACK);
    textDrawer.translate(x,y);
    textDrawer.draw(outlineLayout.getOutline(null));
    textDrawer.setColor(Color.WHITE);
    textDrawer.drawString(topText,0,0);
    textDrawer.translate(-x,-y);
  }
  
  
  public void writeBottomText(){
  int x = xOffset;
  int y = meme.getHeight()-yOffset; 
  ArrayList<String> arr = splitText(bottomText);
  
  textDrawer.translate(x,y);
  //write in reverse order
  for(int i = arr.size()-1; i>=0; i--){
    
    TextLayout outlineLayout = new TextLayout(arr.get(i),textDrawer.getFont(),textDrawer.getFontRenderContext());
    textDrawer.setColor(Color.BLACK);
    textDrawer.draw(outlineLayout.getOutline(null));
    textDrawer.setColor(Color.WHITE);
    textDrawer.drawString(arr.get(i),0,0);
    textDrawer.translate(0,-getFontHeight()-lineSpacing);
 
  }}
  
  
  public void generateMeme(){ //makes the image
   setupGraphics();
   adjustFont();
   writeTopText();
   writeBottomText();
   saveImage();
  }
  
  
  
  
  public static void main(String args[]){
    ImageGenerator g = new ImageGenerator("anakinlol.jpg","paid","work");
    g.generateMeme();
    g = new ImageGenerator("anakinlol.jpg","looooooooooong text","Loooooooonnnnngg text", "test1.png");
    g.generateMeme();
    g = new ImageGenerator("anakinlol.jpg","called","congress", "test2.png");
    g.generateMeme();
    
    
  }  
  
}
  
  
