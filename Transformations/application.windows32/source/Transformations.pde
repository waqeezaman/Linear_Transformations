import java.util.*;

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

Matrix XAxis= new Matrix(2, 2, new float[]{-float(GridRowNum/2)-50, float(GridRowNum/2)+50, 0, 0});
Matrix YAxis= new Matrix(2, 2, new float[]{0, 0, -float(GridColNum/2)-50, float(GridColNum/2)+50});

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

void settings() {
  size(GraphWidth+UIWidth, GraphHeight);
}

void setup() {
  Transform =new Matrix(2, 2, new float[]{1, 0,
    0, 1});

  CreateGridMatrices();

  InitialiseMatrixEntry();
}


void draw() {
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

void DrawUI() {
  stroke(0);
  fill(210);
  rect(GraphWidth, 0, width, height);


  DrawText();
  DrawMatrixEntry();
  TransformButton.display();
}

void mousePressed() {
  AddPoint();
  MatrixEntryMousePress();
  TransformButtonPress();
}

void keyPressed() {
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

void TransformButtonPress() {
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
void ResetTransformedGridMatrices() {
  TransformedGridMatrices=new ArrayList<Matrix>();
  TransformedXAxis=new Matrix();
  TransformedYAxis=new Matrix();
}


void AddPoint() {
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

void TransformPoints() {
  if (PointsMatrix.ColNum>0) {
    TransformedMatrix=PointsMatrix.Multiply(Transform);
  }
}

void TransformGridMatrices() {
  TransformedGridMatrices=new ArrayList<Matrix>();
  for (int m =0; m<GridMatrices.size(); m++) {
    Matrix t = new Matrix(GridMatrices.get(m));
    t=t.Multiply(Transform);

    TransformedGridMatrices.add(t);
  }
  TransformedXAxis=XAxis.Multiply(Transform);
  TransformedYAxis=YAxis.Multiply(Transform);
}
void CreateGridMatrices() {
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







void InitialiseMatrixEntry() {

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

void DrawMatrixEntry() {
  for (TEXTBOX t : MatrixEntry) {
    t.DRAW();
  }
}

void MatrixEntryKeyPress() {
  for (TEXTBOX t : MatrixEntry) {
    t.KEYPRESSED(key, keyCode);
  }
}
void MatrixEntryMousePress() {
  for (TEXTBOX t : MatrixEntry) {
    t.PRESSED(mouseX, mouseY);
  }
}

float[] GetMatrixInput() {
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
