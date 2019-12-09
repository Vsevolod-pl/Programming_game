class Button{
  boolean pressed = false;
  color baseColor, highlightColor;
  float x,y,lx,ly;
  Button(float ix, float iy, float ilx, float ily){
    x = ix;
    y = iy;
    lx = ilx;
    ly = ily;
    baseColor = color(102);
    highlightColor = color(204);
  }
  
  void display(){
    noStroke();
    if(pointIn(mouseX,mouseY))
      fill(highlightColor);
    else
      fill(baseColor);
    rect(x,y,lx,ly);
  }
  
  boolean pointIn(float mx, float my){
    if(mx >= x && mx <= x+lx && my >= y && my <= y+ly){
      return true;
    }
    return false;
  }
  
  void update(){
    pressed = pointIn(mouseX, mouseY) && mousePressed;
    display();
  }
  
  boolean pressed(){
    update();
    return pressed;
  }
}
