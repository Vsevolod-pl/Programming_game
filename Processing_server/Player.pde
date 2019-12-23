class ClientPlayer extends colobok{
  float tx,ty;
  String name;
  Client client;
  
  ClientPlayer(Client ic){
    super(0);
    client = ic;
    while(name == null)
    name = client.readString();
  }
  
  ClientPlayer(Client ic, String iname){
    super(0);
    client = ic;
    name = iname;
    //maxhp = 10000;
    //hp = maxhp;
  }
  
  String toString(){
    return "{\"x\":"+str(x)+", \"y\":"+str(y)+", \"hp\":"+str(hp)+", \"name\":\""+name+"\"}";
  }
    
  void update(String info){
    String data = client.readString();
    if(data!=null){
      info = info+"\"you\":"+this.toString()+"}|";
      client.write(info);
      
      String[] commands = split(data, ';');
      for(int i=0;i < commands.length; i++){
        String com = commands[i]; 
        
        if(com.indexOf("move") != -1){
          String[][] args = matchAll(com, "\\d+\\.*\\d*");
          tx = float(args[0][0]);
          ty = float(args[1][0]);
        
        }else if(com.indexOf("shoot") != -1){
          String[][] args = matchAll(com, "\\d+\\.*\\d*");
          float angle = float(args[0][0]);
          shoot(angle);
        }else if(com.indexOf("stop") != -1){
          velocity.mult(0);
          tx = x;
          ty = y;
        }else
          println(com);
      }
    }
    moveTo(tx,ty);
    super.update();
  }
  
  void display(){
    super.display();
    fill(color(0,0,255));
    textSize(20);
    textAlign(CENTER, BOTTOM);
    text(name, x, y-r+4*mx);
  }
  
  void onDeath(){
    client.write("you died");
  }
    
}

class colobok {
  float x, y, r, hp, speed, regeneration=0, 
    regtime, maxhp,mass=2.5*mx;
  PVector velocity=new PVector(),
  inertia = new PVector();
  weapon weapon;

  colobok(int weapType) {
    this(0, 0, 30, 51, 2, weapType);
  }

  colobok(float ix, float iy, float ir, float ihp, float iv, int weapType) {
    x=ix;
    y=iy;
    r=ir*mx;
    hp=ihp;
    maxhp=ihp;
    speed=iv;
    setWeapon(weapType);
    //velocity
  }
  
  String toString(){
    return "["+str(x)+", "+str(y)+", "+str(hp)+"]";
  }

  void setWeapon(int type) {
    switch(type) {
    case 0:
      weapon=new weapon(this, 20, 5);
      break;
    case 1:
      weapon=new weapon(this, 1, 1);
      break;
    case 2:
      weapon=new shotgun(this, 90, 10);
      break;
    case 3:
      weapon=new rLauncher(this, 50, 100);
      break;
    case 4:
      weapon=new laser(this, 0, 1);
    }
  }

  boolean pointIn(float px, float py) {
    if (dist(px, py, x, y)<r) return true;
    else return false;
  }

  void move() {
    inertia.mult(0.99/mass);
    x+=velocity.x;
    y+=velocity.y;
    x+=inertia.x;
    y+=inertia.y;
    for(Polygon w:walls){
      while(w.intersect_circle(x,y,r)){
        x-=velocity.x+0.01;
        y-=velocity.y+0.01;
      }
    }
    if(x<r)
      x = r;
    if(x>width_-r)
      x = width_ - r;
    if(y<r)
      y = r;
    if(y>height_-r)
      y = height_ - r;
  }
  
  void shoot(float angle){
    weapon.shoot(angle);
  }

  void moveTo(float px, float py) {
    velocity.x=px-x;
    velocity.y=py-y;
    velocity.limit(speed);
  }
  void display() {
    noStroke();
    fill(175);
    ellipse(x, y, 2*r, 2*r);
    fill(256*(1-hp/maxhp), 256*hp/maxhp, 0);
    rect(x-r, y-r-10*mx, 2*r*hp/maxhp, 10*mx);
  }

  void update() {
    if (regtime>0 && hp<maxhp) {
      hp+=regeneration;
      regtime-=1;
    }
    move();
    //if (hp<maxhp) blood.add(int(x), int(y));
    display();
    weapon.update();
  }

  void onDeath() {
  }
}
