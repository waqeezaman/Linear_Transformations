import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Transformations extends PApplet {



char ResetKey='r';
char TransformKey='t';
char PrintKey='p';
char PrintCanvasKey='c';
char TransformGridKey='g';

Colour GridLineColour=new Colour(0, 20, 100);
int GridColNum=10; // gird col num and grid row num must be factors of graphheight and width and both should be even
int GridRowNum=10;
float GridLinesThickness=1;

Colour TransformedGridLineColour=new Colour(3, 169, 255);
float TransformedGridLinesThickness=1f;

Matrix XAxis= new Matrix(2, 2, new float[]{-PApplet.parseFloat(GridRowNum/2)-50, PApplet.parseFloat(GridRowNum/2)+50, 0, 0});
Matrix YAxis= new Matrix(2, 2, new float[]{0, 0, -PApplet.parseFloat(GridColNum/2)-50, PApplet.parseFloat(GridColNum/2)+50});

ArrayList<Matrix> GridMatrices=new ArrayList<Matrix>();
ArrayList<Matrix> TransformedGridMatrices = new ArrayList<Matrix>();

Matrix TransformedXAxis=new Matrix();
Matrix TransformedYAxis=new Matrix();

float PointRadius=10f;

Colour PointColour= new Colour(0, 0, 0);
Colour PointsLineColour = new Colour(255, 0, 0);
float  PointsLineThickness= 3;

Colour TransformedPointColour= new Colour(0, 255, 0);
Colour TransformedLineColour = new Colour(0, 0, 255);
float  TransformedLineThickness= 3;

int GraphWidth=1000;
int GraphHeight=1000;

Matrix PointsMatrix=new Matrix();
Matrix TransformedMatrix = new Matrix();
Matrix Transform;

int UIWidth=400;

TEXTBOX[] MatrixEntry = new TEXTBOX[4];
Button TransformButton= new Button(GraphWidth+20, GraphHeight-80, 100, 40, color(230, 230, 230), color(210, 210, 210), "Switch Matrix");

public void settings() {
  size(GraphWidth+UIWidth, GraphHeight);
}

public void setup() {
  Transform =new Matrix(2, 2, new float[]{1, 0,
    0, 1});

  CreateGridMatrices();

  InitialiseMatrixEntry();
}


public void draw() {
  background(255);

  //Draw Grid Matrices
  for (int i=0; i<GridMatrices.size(); i++) {
    DrawPointMatrix(GridMatrices.get(i), new Colour(0, 0, 0), GridLineColour, GridLinesThickness, true, false);
  }

  //Draw Transformed Grid Matrices
  for (int i=0; i<TransformedGridMatrices.size(); i++) {
    DrawPointMatrix(TransformedGridMatrices.get(i), new Colour(0, 0, 0), TransformedGridLineColour, TransformedGridLinesThickness, true, false);
  }

  //Draw Axis
  DrawPointMatrix(XAxis, new Colour(0, 0, 0), GridLineColour, GridLinesThickness*4, true, false);
  DrawPointMatrix(YAxis, new Colour(0, 0, 0), GridLineColour, GridLinesThickness*4, true, false);

  //Draw Transformed Axis
  DrawPointMatrix(TransformedXAxis, new Colour(0, 0, 0), TransformedGridLineColour, TransformedGridLinesThickness*4, true, false);
  DrawPointMatrix(TransformedYAxis, new Colour(0, 0, 0), TransformedGridLineColour, TransformedGridLinesThickness*4, true, false);

  // Draw points and transformed points
  DrawPointMatrix(PointsMatrix, PointColour, PointsLineColour, PointsLineThickness, true, true);
  DrawPointMatrix(TransformedMatrix, TransformedPointColour, TransformedLineColour, TransformedLineThickness, true, true);


  DrawUI();
}

public void DrawUI() {
  stroke(0);
  fill(210);
  rect(GraphWidth, 0, width, height);


  DrawText();
  DrawMatrixEntry();
  TransformButton.display();
}

public void mousePressed() {
  AddPoint();
  MatrixEntryMousePress();
  TransformButtonPress();
}

public void keyPressed() {
  if (key==ResetKey) {
    PointsMatrix=new Matrix();
    TransformedMatrix=new Matrix();
  } else if (key== TransformKey) {

    TransformPoints();
  } else if (key==PrintKey) {
    PointsMatrix.OutputMatrix();
    TransformedMatrix.OutputMatrix();
  } else if (key==PrintCanvasKey) {
    MapToCanvas(PointsMatrix).OutputMatrix();
    MapToCanvas(TransformedMatrix).OutputMatrix();
  } else if (key==TransformGridKey) {
    if (TransformedGridMatrices.size()>0) {
      ResetTransformedGridMatrices();
    } else {
      TransformGridMatrices();
    }
  }

  MatrixEntryKeyPress();
}

public void TransformButtonPress() {
  if (TransformButton.mouseOver()) {
    Transform=new Matrix(2, 2, GetMatrixInput());
    TransformPoints();
    if (TransformedGridMatrices.size()>0) {
      TransformGridMatrices();
    } else {
      ResetTransformedGridMatrices();
    }
  }
}
public void ResetTransformedGridMatrices() {
  TransformedGridMatrices=new ArrayList<Matrix>();
  TransformedXAxis=new Matrix();
  TransformedYAxis=new Matrix();
}


public void AddPoint() {
  if (mouseX<GraphWidth && mouseY < GraphHeight) {
    //PointList.add(new Point(mouseX,mouseY));
    Point point = new Point(mouseX, mouseY);
    point=MapPointToCartesian(point);
    ArrayList<Float> newPoint = new ArrayList<Float>();
    float mx=mouseX;
    float my =mouseY;
    newPoint.add(mx);
    newPoint.add(my);
    PointsMatrix.AddColumn(new Float[]{point.x, point.y});
  }
}

public void TransformPoints() {
  if (PointsMatrix.ColNum>0) {
    TransformedMatrix=PointsMatrix.Multiply(Transform);
  }
}

public void TransformGridMatrices() {
  TransformedGridMatrices=new ArrayList<Matrix>();
  for (int m =0; m<GridMatrices.size(); m++) {
    Matrix t = new Matrix(GridMatrices.get(m));
    t=t.Multiply(Transform);

    TransformedGridMatrices.add(t);
  }
  TransformedXAxis=XAxis.Multiply(Transform);
  TransformedYAxis=YAxis.Multiply(Transform);
}
public void CreateGridMatrices() {
  //  float xspacing = float(GraphWidth/GridColNum);
  // float yspacing = float(GraphHeight/GridRowNum);

  for (float r =-100; r<GridRowNum+100; r++) {
    Matrix m=new Matrix(2, 2, new float[]{-100, 100, r, r});
    // for(float c =-25; c<GridColNum+25;c++){
    //  m.AddColumn(new Float[]{0,r*yspacing});
    //}
    GridMatrices.add(m);
  }

  for (float c =-100; c<GridColNum+100; c++) {
    Matrix m = new Matrix(2, 2, new float[]{c, c, -100, 100});
    //  for(float r =-25; r<GridRowNum+25;r++){

    // m.AddColumn(new Float[]{r,c});
    //}
    GridMatrices.add(m);
  }
}







public void InitialiseMatrixEntry() {

  int xmargin =20;
  int ymargin=150;
  int padding=10;
  int boxwidth=100;
  int boxheight=50;

  MatrixEntry=new TEXTBOX[]{new TEXTBOX(), new TEXTBOX(), new TEXTBOX(), new TEXTBOX()};

  for (int i =0; i<MatrixEntry.length; i++) {
    MatrixEntry[i].W=boxwidth;
    MatrixEntry[i].H=boxheight;
  }

  MatrixEntry[0].X=GraphWidth+xmargin;
  MatrixEntry[1].X=GraphWidth+xmargin+padding+boxwidth;
  MatrixEntry[2].X=GraphWidth+xmargin;
  MatrixEntry[3].X=GraphWidth+xmargin+padding+boxwidth;

  MatrixEntry[0].Y=GraphHeight-ymargin-padding-boxheight;
  MatrixEntry[1].Y=GraphWidth-ymargin-padding-boxheight;
  MatrixEntry[2].Y=GraphWidth-ymargin;
  MatrixEntry[3].Y=GraphWidth-ymargin;
}

public void DrawMatrixEntry() {
  for (TEXTBOX t : MatrixEntry) {
    t.DRAW();
  }
}

public void MatrixEntryKeyPress() {
  for (TEXTBOX t : MatrixEntry) {
    t.KEYPRESSED(key, keyCode);
  }
}
public void MatrixEntryMousePress() {
  for (TEXTBOX t : MatrixEntry) {
    t.PRESSED(mouseX, mouseY);
  }
}

public float[] GetMatrixInput() {
  float[] input= new float[4];
  for (int i =0; i<input.length; i++) {
    try {
      input[i]=Float.parseFloat(MatrixEntry[i].Text);
    }
    catch(NumberFormatException e) {
      input[i]=0f;
    }
  }
  return input;
}
// code for this class from http://web20bp.com/kb/processing-button-class/
/*****************************************************************************************
 * 
 *   BUTTON CLASS
 * 
 ****************************************************************************************/
class Button
{
  int x, y, w, h;
  int c;
  int cOver;
  String txt;
  int txtSize = 12;

  /****************************************************************************
   
   CONSTRUCTOR
   
   ****************************************************************************/
  Button (int _x, int _y, int _w, int _h, int _c, int _cover, String _txt)
  {
    x = _x;
    y = _y;
    w = _w;
    h = _h;
    c = _c;
    cOver = _cover;
    txt = _txt;
  }

  /****************************************************************************
   
   DISPLAY THE BUTTON
   
   ****************************************************************************/
  public void display()
  {
    pushStyle();
    textAlign(CENTER);
    if (mouseOver())
      fill(cOver);
    else
      fill(c);
    stroke(100);
    strokeWeight(2);
    rect(x, y, w, h, 10);
    fill(0);
    textSize(txtSize);
    text(txt, x+w/2, y+h/2+txtSize/2);
    popStyle();
  }


  /****************************************************************************
   
   CHANGE THE TEXT ON THE BUTTON
   
   ****************************************************************************/
  public void setText (String _txt)
  {
    txt = _txt;
    display();
  }

  /****************************************************************************
   
   IS THE MOUSE OVER THE BUTTON?
   
   ****************************************************************************/
  public boolean mouseOver()
  {
    return (mouseX >= x && mouseX <= (x + w) && mouseY >= y && mouseY <= (y + h));
  }
  
  
} // Button
class Colour{
int R=0;
int G=0; 
int B=0;

  public Colour(int r,int g,int b){
    R=r;
    G=g;
    B=b;
  }

}
public void DrawPoint(float x,float y,float radius,Colour colour){
    stroke(colour.R,colour.G,colour.B);
    fill(colour.R,colour.G,colour.B);
    circle(x,y,radius);
}

public void DrawLine(float x1, float y1,float x2,float y2,Colour colour,float thickness){
  stroke(colour.R,colour.G,colour.B);
  strokeWeight(thickness);
  line(x1,y1,x2,y2);
}


public void DrawPointMatrix(Matrix points,Colour pointcolour,Colour linecolour,float linethickness,boolean drawline,boolean drawpoints){
  Matrix drawmatrix=new Matrix(MapToCanvas(points));
  if(drawmatrix.ColNum>2 && drawline){
    DrawLine(drawmatrix.matrix.get(0).get(0),drawmatrix.matrix.get(0).get(1),drawmatrix.matrix.get(drawmatrix.matrix.size()-1).get(0),drawmatrix.matrix.get(drawmatrix.matrix.size()-1).get(1),
             linecolour,linethickness);
  }
  
  for(int j =0 ;j<drawmatrix.ColNum;j++){
    
    if(j!=drawmatrix.ColNum-1 && drawline){
      DrawLine(drawmatrix.matrix.get(j).get(0),drawmatrix.matrix.get(j).get(1),drawmatrix.matrix.get(j+1).get(0),drawmatrix.matrix.get(j+1).get(1),linecolour,linethickness);
    }
    if(drawpoints){
      DrawPoint(drawmatrix.matrix.get(j).get(0),drawmatrix.matrix.get(j).get(1),PointRadius,pointcolour);

    }
    
    
  }
  

}

public void DrawGrid(){
  
  
  
  
  float xspacing = PApplet.parseFloat(GraphWidth/GridColNum);
  float yspacing = PApplet.parseFloat(GraphHeight/GridRowNum);
  
  for(int r =0; r<GridRowNum;r++){
    DrawLine(0,r*yspacing,GraphWidth,r*yspacing,GridLineColour,GridLinesThickness);
  }
  for(int c =0; c<GridColNum;c++){
    DrawLine(c*xspacing,0,c*xspacing,GraphHeight,GridLineColour,GridLinesThickness);
  }
  DrawLine(GraphWidth/2,0,GraphWidth/2,GraphHeight,GridLineColour,GridLinesThickness*2);
  DrawLine(0,GraphHeight/2,GraphWidth,GraphHeight/2,GridLineColour,GridLinesThickness*2);
  
  
} 

public Point MapPointToCanvas(Point point){
  float x = point.x+PApplet.parseFloat(GridColNum/2);
  x*=PApplet.parseFloat(GraphWidth/GridColNum);
  float y = point.y*-1;
  y+=PApplet.parseFloat(GridRowNum/2);
  y*=PApplet.parseFloat(GraphHeight/GridRowNum);
  return new Point(x,y);
}
public Point MapPointToCartesian(Point point){
  float x = point.x/PApplet.parseFloat(GraphWidth/GridColNum);
  x-=PApplet.parseFloat(GridColNum/2);
  
  float y = point.y/ PApplet.parseFloat(GraphHeight/GridRowNum);
  y*=-1;
  y+=PApplet.parseFloat(GridRowNum/2);
  return new Point(x,y);
}




public Matrix MapToCanvas(Matrix points){
  Matrix transformedmatrix= new Matrix();
  
   
  
  transformedmatrix=points.ScaleRow(1,-1f);

  transformedmatrix=transformedmatrix.AddValueToRow(0,PApplet.parseFloat(GridColNum/2));
  transformedmatrix=transformedmatrix.AddValueToRow(1,PApplet.parseFloat(GridRowNum/2));
 
  
  float horizontalscale = (PApplet.parseFloat(GraphWidth)/PApplet.parseFloat(GridColNum));
  float verticalscale = (PApplet.parseFloat(GraphHeight)/PApplet.parseFloat(GridRowNum));
  
  transformedmatrix=transformedmatrix.ScaleRow(0,horizontalscale);
  transformedmatrix=transformedmatrix.ScaleRow(1,verticalscale);
  
  return transformedmatrix;
}

public Matrix MapToCartesian(Matrix points){
  Matrix transformedmatrix= new Matrix();
  
  float horizontalscale = 1/(PApplet.parseFloat(GraphWidth)/PApplet.parseFloat(GridColNum));
  float verticalscale = 1/(PApplet.parseFloat(GraphHeight)/PApplet.parseFloat(GridRowNum));

  transformedmatrix=points.ScaleRow(0,horizontalscale);
  transformedmatrix=transformedmatrix.ScaleRow(1,verticalscale);
  
     // flip row
  transformedmatrix=transformedmatrix.ScaleRow(1,-1f);
  
  transformedmatrix=transformedmatrix.AddValueToRow(0,-PApplet.parseFloat(GridColNum/2));
  transformedmatrix=transformedmatrix.AddValueToRow(1,PApplet.parseFloat(GridRowNum/2));
 

  
  return transformedmatrix;
}

public void DrawTransformedGridMatrices(){
  for (int i =0 ; i<TransformedGridMatrices.size();i++){
    DrawPointMatrix(TransformedGridMatrices.get(i),PointColour,PointsLineColour,0,true,false);
  }
}

public void DrawText(){
  stroke(0);
  textFont(createFont("Arial", 20, true), 20);
  fill(0);
  text("Click to draw points", GraphWidth+20, 100);
  text("Press P to transform the points", GraphWidth+20, 200);
  text("Press R to reset the points", GraphWidth+20, 300);
  text("Press G to toggle the transformed grid", GraphWidth+20, 400);
  text("Input your own matrix below", GraphWidth+20, 500);

}
class Matrix{
  
  int RowNum;
  int ColNum;
  
  ArrayList<ArrayList<Float> > matrix = new ArrayList<ArrayList<Float>>();
  
  
  public Matrix(){
    // creates matrix object with nothing in it 
  }
  public Matrix(Matrix copy){
   CreateZeroMatrix(copy.RowNum,copy.ColNum);
   for (int j=0; j<ColNum;j++){
     for(int i =0 ; i<RowNum;i++){
       Set(i,j,copy.Get(i,j));
     }
   }
  }
  
  public Matrix(int rownum, int colnum){
    //creates an empty matrix
    RowNum=rownum;
    ColNum=colnum;
    CreateZeroMatrix(rownum,colnum);
    
  }
  
  public Matrix(int rownum, int colnum ,float[] values){
    // creates matrix and fills with values

    
    CreateZeroMatrix(rownum,colnum);


    for (int i=0; i<values.length;i++){
       Set(i/colnum,Math.floorMod(i,colnum),values[i]);
    }

    
    
    

  }
  
  private  void CreateZeroMatrix(int rownum,int colnum){
    
    
    matrix=new ArrayList<ArrayList<Float>>();
    RowNum=rownum;
    for (int c =0; c<colnum;c++){
     
      AddColumn();
    }
    ColNum=colnum;
  }
  
  
  //create an array to store the matrix
  
  
   public Matrix Addition(Matrix B){
     return this;
  }
  
  public Matrix Scale(float scalar){
    // scales each element in the matrix and returns a new matrix
    Matrix scaledmatrix=new Matrix(this);
    for(int j =0; j<ColNum;j++){
      for (int i =0; i<RowNum;i++){
        scaledmatrix.Set(i,j,scalar*Get(i,j));
      }
    }
    return scaledmatrix;
  }
  
  public Matrix ScaleRow(int row, Float scalar){
    
    Matrix scaledmatrix=new Matrix(this);
    for(int col =0 ; col<ColNum;col++){
      scaledmatrix.Set(row,col,scalar* Get(row,col));
    }
    return scaledmatrix;
  }
  
  public Matrix AddValue( float value){
    //adds a single value to each element and returns a new matrix
     Matrix addedmatrix = new Matrix(this);
     for(int j =0; j<ColNum;j++){
      for (int i =0; i<RowNum;i++){
        addedmatrix.Set(i,j,value+Get(i,j));
      }
    }
    return addedmatrix;
    
  }
  
  public Matrix AddValueToRow(int row,float value){
    Matrix addedmatrix=new Matrix(this);
    for(int col =0; col<matrix.size();col++){
      addedmatrix.Set(row,col,value+Get(row,col));
    }
    return addedmatrix;
    
  }
  
  public Matrix Multiply(Matrix transform){
    if(transform.ColNum!=RowNum){
      print("Error: trying to multiply matrices where sizes don't correspond");
      return null;
    }
    else{
      Matrix transformedmatrix= new Matrix(transform.RowNum,ColNum);
      
      
      for (int row=0;row<transform.RowNum;row++){
          for (int col=0; col<ColNum; col++){
            for (int common=0; common<transform.ColNum;common++){
              float sum=transformedmatrix.matrix.get(col).get(row);
              float a = transform.matrix.get(common).get(row);
              float b = matrix.get(col).get(common);
              transformedmatrix.Set(row,col,sum+a*b);
           //   transformedmatrix.Set();
               // transformedmatrix.Set(col,row,transformedmatrix.matrix.get(col).get(row) + B.matrix.get(col ).get(row) * matrix.get(point).get(col));
             // transformedmatrix.matrix.get(col).set(row,transformedmatrix.matrix.get(col).get(row) + B.matrix.get(col ).get(row) * matrix.get(point).get(col));
            }
          }
      }
          return transformedmatrix;
    }
  }
  
  public void Set(int row, int col, Float val){
           matrix.get(col).set(row,val);
  }
  
  public Float Get(int row, int col){
            return  matrix.get(col).get(row);
  }
  
  public void AddRow(){
    
    RowNum+=1;
  }
   public void AddRow(Float[] values){
     
    RowNum+=1;
  }
  
  public void AddColumn(){
        matrix.add(new ArrayList<Float>());
        for(int i =0 ; i<RowNum; i++){
          matrix.get(matrix.size()-1).add(0.0f);
        }
        ColNum+=1;
  }
  
  public void AddColumn(ArrayList<Float> values){
    if(ColNum==0){
       matrix.add(values);
       RowNum= values.size();
    }
    else if(RowNum!=values.size()){
      return;
    }
    else{
      matrix.add(values);
    }
    ColNum+=1;
    
  }
  public void AddColumn(Float[] values){
    
    if(ColNum==0){
        RowNum=values.length;
      }
    
    if(values.length==RowNum){
      
        
      
      AddColumn();
      for (int i=0; i<values.length;i++){
       Set(i,matrix.size()-1,values[i]);
      }
      
    }
    
  }
  
  public void SetRow(ArrayList<Float> newrow, int rowindex){
    if(newrow.size()==ColNum){
    }
    else{
      println("Error: tried adding row that doesnt fit size of matrix");
    }
  }
  
  public void OutputColumn(int col){
    println("Column: " + col);
    for(int i =0; i<matrix.get(col).size();i++){
      println(matrix.get(col).get(i));
    } 
  }
  
  public void OutputMatrix(){
    
    for(int i =0;i<RowNum;i++){
      String row="[";
      for(int j =0;j<ColNum;j++){
        row=row + matrix.get(j).get(i) +" , ";        
      }
      println(row);
    }
    
  }  

}
class Point{
  public float x;
  public float y; 
  

  public  Point(float xpos, float ypos){
    x=xpos;
    y=ypos;
  }
}
// code for this class from https://github.com/mitkonikov/Processing/blob/master/Text_Box/TEXTBOX.pde

public class TEXTBOX {
   public int X = 0, Y = 0, H = 35, W = 200;
   public int TEXTSIZE = 24;
   
   // COLORS
   public int Background = color(140, 140, 140);
   public int Foreground = color(0, 0, 0);
   public int BackgroundSelected = color(160, 160, 160);
   public int Border = color(30, 30, 30);
   
   public boolean BorderEnable = false;
   public int BorderWeight = 1;
   
   public String Text = "";
   public int TextLength = 0;

   private boolean selected = false;
   
   TEXTBOX() {
      // CREATE OBJECT DEFAULT TEXTBOX
   }
   
   TEXTBOX(int x, int y, int w, int h) {
      X = x; Y = y; W = w; H = h;
   }
   
   public void DRAW() {
      // DRAWING THE BACKGROUND
      if (selected) {
         fill(BackgroundSelected);
      } else {
         fill(Background);
      }
      
      if (BorderEnable) {
         strokeWeight(BorderWeight);
         stroke(Border);
      } else {
         noStroke();
      }
      
      rect(X, Y, W, H);
      
      // DRAWING THE TEXT ITSELF
      fill(Foreground);
      textSize(TEXTSIZE);
      text(Text, X + (textWidth("a") / 2), Y + TEXTSIZE);
   }
   
   // IF THE KEYCODE IS ENTER RETURN 1
   // ELSE RETURN 0
   public boolean KEYPRESSED(char KEY, int KEYCODE) {
      if (selected) {
         if (KEYCODE == (int)BACKSPACE) {
            BACKSPACE();
         } else if (KEYCODE == 32) {
            // SPACE
            addText(' ');
         } else if (KEYCODE == (int)ENTER) {
            return true;
         } else {
            // CHECK IF THE KEY IS A LETTER OR A NUMBER OR COMMA
            boolean isKeyCapitalLetter = (KEY >= 'A' && KEY <= 'Z');
            boolean isKeySmallLetter = (KEY >= 'a' && KEY <= 'z');
            boolean isKeyNumber = (KEY >= '0' && KEY <= '9');
            boolean isComma = (KEY ==',');
            boolean isFullStop=(KEY=='.');
            boolean isMinus=(KEY=='-');
      
          //  if (isKeyCapitalLetter || isKeySmallLetter || isKeyNumber || isComma || isFullStop) {
            if(isKeyNumber || isFullStop || isMinus){
               addText(KEY);
            }
         }
      }
  
      
      return false;
   }
   
   private void addText(char text) {
      // IF THE TEXT WIDHT IS IN BOUNDARIES OF THE TEXTBOX
      if (textWidth(Text + text) < W) {
         Text += text;
         TextLength++;
      }
   }
   
   private void BACKSPACE() {
      if (TextLength - 1 >= 0) {
         Text = Text.substring(0, TextLength - 1);
         TextLength--;
      }
   }
   
   // FUNCTION FOR TESTING IS THE POINT
   // OVER THE TEXTBOX
   private boolean overBox(int x, int y) {
      if (x >= X && x <= X + W) {
         if (y >= Y && y <= Y + H) {
            return true;
         }
      }
      
      return false;
   }
   
   public void PRESSED(int x, int y) {
      if (overBox(x, y)) {
         selected = true;
      } else {
         selected = false;
      }
   }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Transformations" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
