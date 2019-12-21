class bullet {
  float x, y;
  int dmg, life, l0;
  PVector velocity;

  bullet(float ix, float iy, int idmg, int lf, PVector v) {
    x=ix;
    y=iy;
    dmg=idmg;
    velocity=v;
    life=lf;
    l0=lf;
  }
  
  String toString(){
    return "["+str(x)+", "+str(y)+"]";
  }

  void display() {
    stroke(0);
    strokeWeight(1);
    fill(255, 255*life/l0);
    ellipse(x, y, 10, 10);
  }
  
  void checkWalls(){
    while (PointIn(x, y)) {
      x-=velocity.x;
      y-=velocity.y;
      life=0;
    }
  }
  
  void Detonate(){
  }

  void move() {
    x+=velocity.x;
    y+=velocity.y;
    checkWalls();
  }

  void update() {
    if(min(x,y)<0 || max(x,y)>max(width_,height_))
      life=0;
    life-=1;
    //if (life<=0) x=width+100;
    move();
    display();
  }
}

class rocket extends bullet {
  PVector acc=new PVector();
  rocket(float ix, float iy, int idmg, PVector v) {
    super(ix, iy, idmg, 100, v);//2*width*height
  }

  void display() {
    noStroke();
    fill(0, 255, 0);

    pushMatrix();
    translate(x, y);
    rotate(velocity.heading());
    rect(-5, -2, 30, 4);
    popMatrix();
  }

  void Detonate() {
    for (float angle=0; angle<TWO_PI; angle+=PI/40) {
      for (float speed=3; speed>2.6; speed*=0.9) {
        bullets.add(new bullet(x, y, 20, int(10*mx), new PVector(speed*10*cos(angle), speed*10*sin(angle))));
      }
    }
    life=-1;
  }

  void move() {
    velocity.limit(30);
    super.move();
  }
}
