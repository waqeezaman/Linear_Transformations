void DrawPoint(float x,float y,float radius,Colour colour){
    stroke(colour.R,colour.G,colour.B);
    fill(colour.R,colour.G,colour.B);
    circle(x,y,radius);
}

void DrawLine(float x1, float y1,float x2,float y2,Colour colour,float thickness){
  stroke(colour.R,colour.G,colour.B);
  strokeWeight(thickness);
  line(x1,y1,x2,y2);
}


void DrawPointMatrix(Matrix points,Colour pointcolour,Colour linecolour,float linethickness,boolean drawline,boolean drawpoints){
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

void DrawGrid(){
  
  
  
  
  float xspacing = float(GraphWidth/GridColNum);
  float yspacing = float(GraphHeight/GridRowNum);
  
  for(int r =0; r<GridRowNum;r++){
    DrawLine(0,r*yspacing,GraphWidth,r*yspacing,GridLineColour,GridLinesThickness);
  }
  for(int c =0; c<GridColNum;c++){
    DrawLine(c*xspacing,0,c*xspacing,GraphHeight,GridLineColour,GridLinesThickness);
  }
  DrawLine(GraphWidth/2,0,GraphWidth/2,GraphHeight,GridLineColour,GridLinesThickness*2);
  DrawLine(0,GraphHeight/2,GraphWidth,GraphHeight/2,GridLineColour,GridLinesThickness*2);
  
  
} 

Point MapPointToCanvas(Point point){
  float x = point.x+float(GridColNum/2);
  x*=float(GraphWidth/GridColNum);
  float y = point.y*-1;
  y+=float(GridRowNum/2);
  y*=float(GraphHeight/GridRowNum);
  return new Point(x,y);
}
Point MapPointToCartesian(Point point){
  float x = point.x/float(GraphWidth/GridColNum);
  x-=float(GridColNum/2);
  
  float y = point.y/ float(GraphHeight/GridRowNum);
  y*=-1;
  y+=float(GridRowNum/2);
  return new Point(x,y);
}




Matrix MapToCanvas(Matrix points){
  Matrix transformedmatrix= new Matrix();
  
   
  
  transformedmatrix=points.ScaleRow(1,-1f);

  transformedmatrix=transformedmatrix.AddValueToRow(0,float(GridColNum/2));
  transformedmatrix=transformedmatrix.AddValueToRow(1,float(GridRowNum/2));
 
  
  float horizontalscale = (float(GraphWidth)/float(GridColNum));
  float verticalscale = (float(GraphHeight)/float(GridRowNum));
  
  transformedmatrix=transformedmatrix.ScaleRow(0,horizontalscale);
  transformedmatrix=transformedmatrix.ScaleRow(1,verticalscale);
  
  return transformedmatrix;
}

Matrix MapToCartesian(Matrix points){
  Matrix transformedmatrix= new Matrix();
  
  float horizontalscale = 1/(float(GraphWidth)/float(GridColNum));
  float verticalscale = 1/(float(GraphHeight)/float(GridRowNum));

  transformedmatrix=points.ScaleRow(0,horizontalscale);
  transformedmatrix=transformedmatrix.ScaleRow(1,verticalscale);
  
     // flip row
  transformedmatrix=transformedmatrix.ScaleRow(1,-1f);
  
  transformedmatrix=transformedmatrix.AddValueToRow(0,-float(GridColNum/2));
  transformedmatrix=transformedmatrix.AddValueToRow(1,float(GridRowNum/2));
 

  
  return transformedmatrix;
}

void DrawTransformedGridMatrices(){
  for (int i =0 ; i<TransformedGridMatrices.size();i++){
    DrawPointMatrix(TransformedGridMatrices.get(i),PointColour,PointsLineColour,0,true,false);
  }
}

void DrawText(){
  stroke(0);
  textFont(createFont("Arial", 20, true), 20);
  fill(0);
  text("Click to draw points", GraphWidth+20, 100);
  text("Press P to transform the points", GraphWidth+20, 200);
  text("Press R to reset the points", GraphWidth+20, 300);
  text("Press G to toggle the transformed grid", GraphWidth+20, 400);
  text("Input your own matrix below", GraphWidth+20, 500);

}
