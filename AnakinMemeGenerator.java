public class AnakinMemeGenerator extends ImageGenerator{
  
  String verb,prefix;
  
  //constructors start
  
  public AnakinMemeGenerator(String sourceFileName, String verb, String prefix){
    super(sourceFileName);
    toggleTopOneLine();
    this.verb =verb.toUpperCase();
    this.prefix=prefix.toUpperCase();
    createText();
  }
  
  public AnakinMemeGenerator(String sourceFileName, String verb, String prefix, String outputName){
    this(sourceFileName,verb,prefix);
    setOutputName(outputName);
  }
  //constructors end
  
  @Override
  public void createText(){
   setTopText("I " + verb + " THEM. I " + verb + " THEM ALL.");
   setBottomText("AND NOT JUST THE " + prefix +"MEN, BUT THE " + prefix +"WOMEN AND THE "  + prefix + "CHILDREN TOO.");
  }
  

  
  
  public static void main(String args[]){
    
    AnakinMemeGenerator g;
    
    switch(args.length){
      case(2):
        g = new AnakinMemeGenerator("anakinlol.jpg", args[0], args[1], "custom.png");
        break;
      case(3):
        g = new AnakinMemeGenerator("anakinlol.jpg", args[0], args[1], args[2]);
        break;
      default:
        g = new AnakinMemeGenerator("anakinlol.jpg", "paid", "work");
        break;
    }
    g.generateMeme();
  }
}