float mx=1;

class spot {
  PImage img;
  float x, y, life;
  spot(float ix, float iy, PImage nimg) {
    img=nimg;
    life=255;
    x=ix;
    y=iy;
  }

  void update() {
    life-=6;
    display();
  }

  void display() {
    imageMode(CENTER);
    tint(255, life);
    image(img, x, y, 80, 80);
    tint(255,255);
  }
}

class BloodSystem {
  ArrayList<spot> blood_spots = new ArrayList<spot>();
  PImage[] spotImgs;
  int dx, dy;
  BloodSystem() {
    spotImgs=new PImage[2];
    for (int i=0; i<spotImgs.length; i++) {
      spotImgs[i]=loadImage((i+1)+".png");
    }
  }

  void add(float x, float y) {
    dx=int(random(40)-20);
    dy=int(random(40)-20);
    blood_spots.add(new spot(x+dx, y+dy, spotImgs[int(random(spotImgs.length))]));
  }

  void display() {
    for (spot s : blood_spots) {
      s.display();
    }
  }

  void update() {
    for (int i=0; i<blood_spots.size (); i++) {
      spot s=(spot) blood_spots.get(i);
      s.update();
      if (s.life<0) {
        blood_spots.remove(i--);
      }
    }
  }
  
  void clear(){
    blood_spots.clear();
  }
}
