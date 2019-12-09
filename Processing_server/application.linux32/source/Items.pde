boolean clearThings=true;
class item {
  int x, y, lx, ly;
  String name;
  PImage img;

  item(int ix, int iy, String iname) {
    x=ix;
    y=iy;
    name=iname;
    img=loadImage(name+".png");
    lx=int(4*img.width*mx);
    ly=int(4*img.height*mx);
  }

  void use() {
  }

  void display() {
    image(img, x, y, lx, ly);
    fill(255);
    textSize(30*mx);
    textAlign(CENTER,BOTTOM);
    text(name, x+lx/2, y-ly/3);
  }

  item get(int nx, int ny) {
    return new item(nx, ny, name);
  }
}

class medKit extends item {
  medKit(int ix, int iy) {
    super(ix, iy, "medKit");
  }

  void use(colobok owner) {
    owner.hp=owner.maxhp;
  }

  item get(int nx, int ny) {
    return new medKit(nx, ny);
  }
}

class weaponChanger extends item {
  int type;
  weaponChanger(int x, int y, int t) {
    super(x, y, "weapon"+t);
    type=t;
  }

  void use(colobok owner) {
    owner.setWeapon(type);
  }

  item get(int nx, int ny) {
    return new weaponChanger(nx, ny, type);
  }
}
