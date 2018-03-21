public class PepperRidgeMemeGenerator extends ImageGenerator{

  String top,bot;
  
  public PepperRidgeMemeGenerator(String sourceFileName, String top){
    super(sourceFileName);
    toggleBottomOneLine();
    this.top =top.toUpperCase();
    createText();
  }
  
    public PepperRidgeMemeGenerator(String sourceFileName, String top,String outputName){
this(sourceFileName,top);
    setOutputName(outputName);
  }
  
  @Override
  public void createText(){
   setTopText("REMEMBER WHEN " + top);
    setBottomText("PEPPERRIDGE FARM REMEMBERS");
  }
 
  
  public static void main(String args[]){
    
    PepperRidgeMemeGenerator g;
    
    switch(args.length){
      case(1):
        g = new PepperRidgeMemeGenerator("pepper.png", args[0], "customPepper.png");
        break;
      case(2):
        g = new PepperRidgeMemeGenerator("pepper.png", args[0], args[1]);
        break;
      default:
        g = new PepperRidgeMemeGenerator("pepper.png", "when cars wern't invented yet?", "defaultPepper.png");
        break;
    }
    
    g.generateMeme();
    
  }
}