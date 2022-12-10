// code for this class from http://web20bp.com/kb/processing-button-class/
/*****************************************************************************************
 * 
 *   BUTTON CLASS
 * 
 ****************************************************************************************/
class Button
{
  int x, y, w, h;
  color c;
  color cOver;
  String txt;
  int txtSize = 12;

  /****************************************************************************
   
   CONSTRUCTOR
   
   ****************************************************************************/
  Button (int _x, int _y, int _w, int _h, color _c, color _cover, String _txt)
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
  void display()
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
  void setText (String _txt)
  {
    txt = _txt;
    display();
  }

  /****************************************************************************
   
   IS THE MOUSE OVER THE BUTTON?
   
   ****************************************************************************/
  boolean mouseOver()
  {
    return (mouseX >= x && mouseX <= (x + w) && mouseY >= y && mouseY <= (y + h));
  }
  
  
} // Button
