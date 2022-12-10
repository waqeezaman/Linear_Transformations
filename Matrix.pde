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
    // scales a whole row in the matrix by a single value
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
  
  
  public void AddColumn(){
        matrix.add(new ArrayList<Float>());
        for(int i =0 ; i<RowNum; i++){
          matrix.get(matrix.size()-1).add(0.0);
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
