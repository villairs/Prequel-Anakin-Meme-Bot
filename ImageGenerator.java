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




public abstract class ImageGenerator{
  private boolean topOneLine,bottomOneLine; //flags to determine if the meme has extra requirements, this flag is for having text all on one line
  private String chosenFont = "Impact"; 
  private int xOffset = 20;//padding between left and right edges of image
  private int yOffset = 4; //padding between top and bottom edges of image
  private int lineSpacing = 1; //line spacing between text lines
  private double scaleX = 1.4;
  private double scaleY = 1.4;
  private double maxImageCoveragePerText = 0.4; //maximum amount of space a segment of text can cover in terms of percentage, default 40%
  private BufferedImage meme;
  private Graphics2D textDrawer;
  private String topText,bottomText, outputName;
  private int maxLines,maxTextHeight, maxTextWidth;
  
  
  //constructor 
  public ImageGenerator(String sourceFileName){
topOneLine = false;
bottomOneLine = false;
    createText();
    outputName = "default.png";
    try{
      meme = ImageIO.read(new File(sourceFileName));
    }
    catch(IOException e){
      System.out.println("File could not be read,");
    }
    scaleImage();
    calcTextConstraints();
  }
  
  //constructors end
  
  //scales the image
  private void scaleImage(){
    AffineTransform aMatrix = new AffineTransform();
    aMatrix.scale(scaleX,scaleY);
    AffineTransformOp op = new AffineTransformOp(aMatrix, AffineTransformOp.TYPE_BILINEAR);
    meme = op.filter(meme,null);
  }
  
  
  
   private void saveImage(){
    try{
      File outputFile = new File(outputName);
      ImageIO.write(meme, "png", outputFile);
    }
    catch(IOException e){
      System.out.println("Shouldn't reach here. Somehow failed to save image.");
    }
  }
  
  //subclasses must implement this 
  public abstract void createText();
  
  
  //sets up graphics2d
   private void setupGraphics(){
    textDrawer = meme.createGraphics();
    Font font = new Font(chosenFont, Font.PLAIN, 90);
    textDrawer.setFont(font);
    textDrawer.setColor(Color.WHITE);
    textDrawer.setStroke(new BasicStroke(4));
  }
  
  //lowers the font size based on width
   private void adjustFontWidth(){
    
    calcMaxLines();
    //if the top text is too long to fit into the image, make the font smaller
    //if the total width of the bottom text exceeds the max number of lines * width of image, make the font smaller
    boolean topCondition, bottomCondition;
    int topTextWidth, bottomTextWidth;
    //get width of text
    topTextWidth = textDrawer.getFontMetrics().stringWidth(topText);
    bottomTextWidth =textDrawer.getFontMetrics().stringWidth(bottomText);
    
    
    if(topOneLine)
      topCondition =  topTextWidth > maxTextWidth;
    else
      topCondition =  topTextWidth > (maxLines * maxTextWidth);
    if(bottomOneLine)
      bottomCondition = bottomTextWidth > maxTextWidth;
    else
      bottomCondition = bottomTextWidth > (maxLines * maxTextWidth);
 
    if(topCondition || bottomCondition)
    {
      decrementFontSize();
      adjustFontWidth();
    }
  }
  
  //lowers font size based on height
   private void adjustFontHeight(){ 
    calcMaxLines();
    //if the text cannot fit into the alotted space, make it smaller
    if((maxLines*getFontHeight()) + (maxLines*lineSpacing) + yOffset > maxTextHeight){
      decrementFontSize();
      adjustFontHeight();
    }
  }
  
  //sets font to working size
private void adjustFontSize(){
    adjustFontHeight();
    adjustFontWidth();
  }
  
   private void decrementFontSize(){
    Font font = new Font(chosenFont, Font.BOLD, textDrawer.getFont().getSize()-1);
    textDrawer.setFont(font);
  }
  
  
  //calculates how many lines of text can fit within the maximum allowed space given the current font
   private void calcMaxLines(){ 
    maxLines = (maxTextHeight/getFontHeight()); 
  }
  
  private int getFontHeight(){ 
    return textDrawer.getFontMetrics().getMaxAscent();
  }
  
  //calculates maximum height and width of text
   private void calcTextConstraints(){ 
    double temp = meme.getHeight()*maxImageCoveragePerText;
    maxTextHeight = (int)temp;
    maxTextWidth = meme.getWidth() - (xOffset*2);
  }
  

   private int calcJustifiedOffset(String text){
    int textWidth = textDrawer.getFontMetrics().stringWidth(text);
    return (maxTextWidth-textWidth)/2;//xoffset is already included in maxTextWidth
  }
  
  //splits strings into smaller strings
  //this is because graphics2d drawString will draw everything on a single line
  //if you want a string on multiple lines you have to split it up and draw each segment individually
   private ArrayList<String> splitText(String text){  
    ArrayList<String> arr = new ArrayList<String>();
    StringTokenizer tok = new StringTokenizer(text);
    StringBuilder stringBuilder = new StringBuilder() ;
    String temp = "";
    
    while(tok.hasMoreTokens()){
      temp = tok.nextToken();
      
      if(textDrawer.getFontMetrics().stringWidth(stringBuilder.toString()+" " +temp) > maxTextWidth){
        arr.add(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(temp);
      }
      
      else{
        stringBuilder.append(" ").append(temp);
      } 
              if(!tok.hasMoreTokens()){ //cant forget about the last sentence
          arr.add(stringBuilder.toString());
        }
    }
    return arr;
  }
  
  
   private void writeText(int x, int y, String[] text){
    int justifiedOffset;
    int i;
    
    textDrawer.translate(x,y);
    
    //draw in reverse order
    for( i= text.length-1; i >=0;i--){
      TextLayout outlineLayout = new TextLayout(text[i],textDrawer.getFont(),textDrawer.getFontRenderContext());
      Shape outline = outlineLayout.getOutline(null);
      justifiedOffset = calcJustifiedOffset(text[i]);
      textDrawer.translate(justifiedOffset,0); 
      textDrawer.setColor(Color.BLACK);
      textDrawer.draw(outline);
      textDrawer.setColor(Color.WHITE);
      textDrawer.drawString(text[i],0,0);
      textDrawer.translate(-justifiedOffset,0); 
      textDrawer.translate(0,-getFontHeight()-lineSpacing);    
    }
    //return to original position
    textDrawer.translate(-x,-y+((getFontHeight()+lineSpacing)*text.length));
  }
   
   
   
   private void write(){
    String[] topA;    
    String[] botA;
    if(topOneLine)
      topA = new String[]{topText};
    else
      topA = splitText(topText).toArray(new String[splitText(topText).size()]);
    if(bottomOneLine)
      botA = new String[]{bottomText};
    else
      botA = splitText(bottomText).toArray(new String[splitText(bottomText).size()]);
    
    
    writeText(xOffset,yOffset+((getFontHeight()+lineSpacing)*topA.length),topA);
    writeText(xOffset,meme.getHeight()-yOffset,botA );
   }
   

   
  
  //makes the image
  public void generateMeme(){ 
    setupGraphics();
    adjustFontSize();
    write();
    saveImage();
  }
  
     //mutator methods
   public void setTopText(String s){
   topText = s;
   }
   public void setBottomText(String s){
   bottomText =s;
   }
   public void setOutputName(String s){
   outputName = s;
   }
   public void toggleTopOneLine(){
   topOneLine = !topOneLine;
   }
   public void toggleBottomOneLine(){
   bottomOneLine = !bottomOneLine;
   }
}
  
  
