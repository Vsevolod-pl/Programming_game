import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.net.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Processing_server extends PApplet {

item[] typeItems=new item[7];
ArrayList<bullet> bullets = new ArrayList<bullet>();
ArrayList<item> items = new ArrayList<item>();
ArrayList<Polygon> walls;
PImage background;
ConnectionManager manager;
Button resetButton;
float width_, height_;
int port, nPlayers;

public void loadProperties(){
  XML properties = loadXML("properties.xml");
  nPlayers = properties.getInt("players");
  XML serverInfo = properties.getChild("server");
  port = serverInfo.getInt("port");
  height_ = properties.getFloat("height");
  width_ = properties.getFloat("width"); 
}

public void setup() {
  
  mx=max(width,height)/1280.0f;
  background = loadImage("grass.jpg");
  loadProperties();
  
  typeItems[0]=new medKit(-100,-100);
  for(int i=1; i<typeItems.length; i++){
    typeItems[i]=new weaponChanger(-100,-100,i);
  } 
  
  setupWalls();
  
  manager = new ConnectionManager(this, port, nPlayers);
  resetButton = new Button(width-130, 10, 120, 100);
}

public void reset(){
  bullets.clear();
  manager.reset();
  items.clear();
}

public void draw() {
  /*int bw=background.width,bh=background.height;
  for(int bx=-bw;bx<width;bx+=bw){
    for(int by=-bh;by<height;by+=bh){
      set(bx,by,background);
    }
  }*/
  background(0);
  if(resetButton.pressed()){
    reset();
  }
  noFill();
  stroke(150);
  strokeWeight(5);
  rect(0,0,width_,height_);
  displayWalls();
  
  stroke(0);
  fill(255);
  textSize(30*mx);
  textAlign(LEFT,TOP);
  text("Players : "+str(manager.players.size())+" of "+str(manager.nPlayers),100,100);
  textAlign(CENTER,CENTER);
  text("reset", width-70, 50);
  
  for(int i=0;i<bullets.size();i++){
    bullet b= bullets.get(i);
    if (b!=null) {
      if (b.life<=0){
        b.Detonate();
        bullets.remove(i); i--;
        continue;
      }
      b.update();
      manager.pointIn(b);
    }else{
      bullets.remove(i); i--;
    }
  }
  
  manager.update();
}
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
  
  public String toString(){
    return "["+str(x)+", "+str(y)+"]";
  }

  public void display() {
    stroke(0);
    strokeWeight(1);
    fill(255, 255*life/l0);
    ellipse(x, y, 10, 10);
  }
  
  public void checkWalls(){
    while (PointIn(x, y)) {
      x-=velocity.x;
      y-=velocity.y;
      life=0;
    }
  }
  
  public void Detonate(){
  }

  public void move() {
    x+=velocity.x;
    y+=velocity.y;
    checkWalls();
  }

  public void update() {
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

  public void display() {
    noStroke();
    fill(0, 255, 0);

    pushMatrix();
    translate(x, y);
    rotate(velocity.heading());
    rect(-5, -2, 30, 4);
    popMatrix();
  }

  public void Detonate() {
    for (float angle=0; angle<TWO_PI; angle+=PI/40) {
      for (float speed=3; speed>2.6f; speed*=0.9f) {
        bullets.add(new bullet(x, y, 20, PApplet.parseInt(10*mx), new PVector(speed*10*cos(angle), speed*10*sin(angle))));
      }
    }
    life=-1;
  }

  public void move() {
    velocity.limit(30);
    super.move();
  }
}
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

  public void update() {
    life-=6;
    display();
  }

  public void display() {
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

  public void add(float x, float y) {
    dx=PApplet.parseInt(random(40)-20);
    dy=PApplet.parseInt(random(40)-20);
    blood_spots.add(new spot(x+dx, y+dy, spotImgs[PApplet.parseInt(random(spotImgs.length))]));
  }

  public void display() {
    for (spot s : blood_spots) {
      s.display();
    }
  }

  public void update() {
    for (int i=0; i<blood_spots.size (); i++) {
      spot s=(spot) blood_spots.get(i);
      s.update();
      if (s.life<0) {
        blood_spots.remove(i--);
      }
    }
  }
  
  public void clear(){
    blood_spots.clear();
  }
}
class Button{
  boolean pressed = false;
  int baseColor, highlightColor;
  float x,y,lx,ly;
  Button(float ix, float iy, float ilx, float ily){
    x = ix;
    y = iy;
    lx = ilx;
    ly = ily;
    baseColor = color(102);
    highlightColor = color(204);
  }
  
  public void display(){
    noStroke();
    if(pointIn(mouseX,mouseY))
      fill(highlightColor);
    else
      fill(baseColor);
    rect(x,y,lx,ly);
  }
  
  public boolean pointIn(float mx, float my){
    if(mx >= x && mx <= x+lx && my >= y && my <= y+ly){
      return true;
    }
    return false;
  }
  
  public void update(){
    pressed = pointIn(mouseX, mouseY) && mousePressed;
    display();
  }
  
  public boolean pressed(){
    update();
    return pressed;
  }
}
public PImage blend_s(PImage a, PImage b){
  int w=a.width,
  h=a.height;
  b.resize(w,h);
  a.loadPixels();
  b.loadPixels();
  float f;
  for(int x=0;x<w;x++){
    for(int y=0;y<h;y++){
      f=noise(x/100.0f,y/100.0f);
      /*if(f>0.5)
       f=1;
      else
        f=0;
      //f=sqrt(f);*/
      int r,g,bl;
      
      r=PApplet.parseInt(f*red(a.pixels[x+y*w])
      +(1-f)*red(b.pixels[x+y*w]));
      g=PApplet.parseInt(f*green(a.pixels[x+y*w])
      +(1-f)*green(b.pixels[x+y*w]));
      bl=PApplet.parseInt(f*blue(a.pixels[x+y*w])
      +(1-f)*blue(b.pixels[x+y*w]));
      
      a.pixels[x+y*w]=color(r,g,bl);
    }
  }
  a.updatePixels();
  return a;
}
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
    lx=PApplet.parseInt(4*img.width*mx);
    ly=PApplet.parseInt(4*img.height*mx);
  }

  public void use() {
  }

  public void display() {
    image(img, x, y, lx, ly);
    fill(255);
    textSize(30*mx);
    textAlign(CENTER,BOTTOM);
    text(name, x+lx/2, y-ly/3);
  }

  public item get(int nx, int ny) {
    return new item(nx, ny, name);
  }
}

class medKit extends item {
  medKit(int ix, int iy) {
    super(ix, iy, "medKit");
  }

  public void use(colobok owner) {
    owner.hp=owner.maxhp;
  }

  public item get(int nx, int ny) {
    return new medKit(nx, ny);
  }
}

class weaponChanger extends item {
  int type;
  weaponChanger(int x, int y, int t) {
    super(x, y, "weapon"+t);
    type=t;
  }

  public void use(colobok owner) {
    owner.setWeapon(type);
  }

  public item get(int nx, int ny) {
    return new weaponChanger(nx, ny, type);
  }
}
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
  
  public String toString(){
    return "{\"x\":"+str(x)+", \"y\":"+str(y)+", \"hp\":"+str(hp)+", \"name\":\""+name+"\"}";
  }
    
  public void update(String info){
    String data = client.readString();
    if(data!=null){
      info = info+"\"you\":"+this.toString()+"}|";
      client.write(info);
      
      String[] commands = split(data, ';');
      for(int i=0;i < commands.length; i++){
        String com = commands[i]; 
        
        if(com.indexOf("move") != -1){
          String[][] args = matchAll(com, "\\d+\\.*\\d*");
          tx = PApplet.parseFloat(args[0][0]);
          ty = PApplet.parseFloat(args[1][0]);
        
        }else if(com.indexOf("shoot") != -1){
          String[][] args = matchAll(com, "\\d+\\.*\\d*");
          float angle = PApplet.parseFloat(args[0][0]);
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
  
  public void display(){
    super.display();
    fill(color(0,0,255));
    textSize(20);
    textAlign(CENTER, BOTTOM);
    text(name, x, y-r+4*mx);
  }
  
  public void onDeath(){
    client.write("you died");
  }
    
}

class colobok {
  float x, y, r, hp, speed, regeneration=0, 
    regtime, maxhp,mass=2.5f*mx;
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
  
  public String toString(){
    return "["+str(x)+", "+str(y)+", "+str(hp)+"]";
  }

  public void setWeapon(int type) {
    switch(type) {
    case 0:
      weapon=new weapon(this, 20, 20);
      break;
    case 1:
      weapon=new weapon(this, 1, 4);
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

  public boolean pointIn(float px, float py) {
    if (dist(px, py, x, y)<r) return true;
    else return false;
  }

  public void move() {
    inertia.mult(0.99f/mass);
    x+=velocity.x;
    y+=velocity.y;
    x+=inertia.x;
    y+=inertia.y;
    for(Polygon w:walls){
      while(w.intersect_circle(x,y,r)){
        x-=velocity.x+0.01f;
        y-=velocity.y+0.01f;
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
  
  public void shoot(float angle){
    weapon.shoot(angle);
  }

  public void moveTo(float px, float py) {
    velocity.x=px-x;
    velocity.y=py-y;
    velocity.limit(speed);
  }
  public void display() {
    noStroke();
    fill(175);
    ellipse(x, y, 2*r, 2*r);
    fill(256*(1-hp/maxhp), 256*hp/maxhp, 0);
    rect(x-r, y-r-10*mx, 2*r*hp/maxhp, 10*mx);
  }

  public void update() {
    if (regtime>0 && hp<maxhp) {
      hp+=regeneration;
      regtime-=1;
    }
    move();
    //if (hp<maxhp) blood.add(int(x), int(y));
    display();
    weapon.update();
  }

  public void onDeath() {
  }
}


class ConnectionManager{
  int status = 0; //0 - awayting players; 1 - ready 
  int port, nPlayers;
  Server server;
  BloodSystem blood;
  ArrayList<ClientPlayer> players;
  
  ConnectionManager(PApplet app, int port, int pl){
    this.port = port;
    nPlayers = pl;
    players = new ArrayList<ClientPlayer>();
    server = new Server(app, port);
    blood = new BloodSystem();
  }
  
  public void reset(){
    server.write("\'restart\'");
    status = 0;
    players.clear();
    blood.clear();
  }
  
  public void update(){
    if(status == 0){
      Client newClient = server.available();
      if(newClient != null){
        String hello = newClient.readString();
        if(hello.equals("hello")){
          newClient.write("you connected");
          ClientPlayer newPlayer = new ClientPlayer(newClient);
          players.add(newPlayer);
        }
      }
      if(players.size() >= nPlayers){
        server.write("start");
        status = 1;
      }
    }else{
      String info = "|{\"players\":" + players.toString() + ", \"bullets\":" + bullets.toString()+", ";
      for(ClientPlayer p: players){
        p.update(info);
      }
      clearDead();
    }
    //blood.update();
  }
  
  public void pointIn(bullet b){
    for(ClientPlayer p: players){
      if(p.pointIn(b.x,b.y)){
        p.hp -= b.dmg;
        b.life = 0;
        //blood.add(b.x,b.y);
      }
    }
  }
  
  public void clearDead(){
    for(int i=0; i < players.size(); i++){
      ClientPlayer p = (ClientPlayer) players.get(i);
      if(p.hp <= 0){
        p.onDeath();
        blood.add(p.x,p.y);
        players.remove(i);i--;
      }
    }
  }
  
}

public void setupWalls(){
  walls=new ArrayList<Polygon>();
  for(XML child:loadXML("map.xml").getChildren()){
    if(!child.getName().equals("polygon")){
      //println(child.getName());
    }else{
      loadPolygon(child);
    }
  }
}

public void displayWalls(){
  for(Polygon w:walls){
    w.display();
  }
}

// a is radian
public PVector intersect(float x0, float y0, float a0, float x1, float y1, float x2, float y2) {
  float b0 = y0 - tan(a0)*x0;
  float a1 = atan2(y2-y1, x2-x1);
  float b1 = y1-tan(a1)*x1;
  if (tan(a1) != tan(a0)) {
    float xi = (b0-b1)/(tan(a1)-tan(a0));
    float yi = tan(a0)*xi + b0;
    if (xi+0.0001f>=min(x1, x2) && xi-0.0001f<=max(x1, x2) //костыль
      && yi+0.0001f>=min(y1, y2) && yi-0.0001f<=max(y1, y2)) {
      if (dist(x0, y0, xi, yi)>dist(x0+cos(a0), y0+sin(a0), xi, yi)) {
        return new PVector(xi, yi);
      }
    }
  }
  return null;
}

public PVector RayCast(float x0, float y0, float a0) {
  PVector res = null, tmp;
  for (Polygon p : walls) {
    tmp=p.cast(x0, y0, a0);
    p.display();
    if (tmp!=null) {
      if (res==null) {
        res=tmp;
      } else {
        if (dist(x0, y0, res.x, res.y)>dist(x0, y0, tmp.x, tmp.y)) {
          res = tmp;
        }
      }
    }
  }
  return res;
}

public boolean PointIn(float x, float y) {
  boolean res=false;
  for (Polygon p : walls)
    res = res || p.pointIn(x, y);
  return res;
}

public int underLine(float x, float y, float x1, float y1, float x2, float y2) {
  int res = 0;
  if ( (x1!=x2) && (x>=min(x1, x2)) && (x<=max(x1, x2)) ) {
    float a=(y2-y1)/(x2-x1);
    float b=y1-a*x1;
    if (y>a*x+b) {
      res=1;
    }
  } else if (x1==x) {
    if (y>y1)
      res=1;
  } else if (x2==x) {
    if (y>y2)
      res=1;
  }

  return res;
}

class Polygon {
  int l;
  float[] xs, ys;
  Polygon(float[] x, float[] y) {
    xs = x;
    ys = y;
    l=x.length;
    if(l!=0)
    walls.add(this);
  }

  public void display() {
    stroke(0);
    strokeWeight(1);
    fill(255, 200);
    beginShape();
    for (int i=0; i<l+1; i++) {
      vertex(xs[i%l], ys[i%l]);
    }
    endShape();
  }

  // TODO change name
  public PVector cast(float x0, float y0, float a0) {
    PVector res = null, tmp;
    for (int i=0; i<l+1; i++) {
      //println(
      tmp = intersect(x0, y0, a0, xs[i%l], ys[i%l], xs[(i+1)%l], ys[(i+1)%l]);
      if (tmp!=null) {
        if (res==null) {
          res=tmp;
        } else {
          if (dist(x0, y0, res.x, res.y)>dist(x0, y0, tmp.x, tmp.y)) {
            res = tmp;
          }
        }
      }
    }
    return res;
  }

  public boolean pointIn(float x, float y) {
    int res=0;
    for (int i=0; i<l; i++) {
      res+=underLine(x, y, xs[i%l], ys[i%l], xs[(i+1)%l], ys[(i+1)%l]);
      if (x==xs[i%l] && y==ys[i%l]) {
        return true;
      }
    }
    if (res%2==1) {
      return true;
    } else {
      return false;
    }
  }
  
  public boolean intersect_circle(float x, float y, float r){
    boolean res=false;
    PVector a,b;
    a = new PVector();
    b = new PVector();
    for(int i=0;i<l+1;i++){
      a.x = x-xs[i%l];
      a.y = y-ys[i%l];
      b.x = xs[(i+1)%l]-xs[i%l];
      b.y = ys[(i+1)%l]-ys[i%l];
      float S = a.x*b.y-a.y*b.x;
      float d = abs(S)/b.mag();
      float x1 = a.dot(b)/b.mag();
      if ((d<r && x1>0 && x1<b.mag()) || dist(x,y,xs[i%l],ys[i%l]) < r) {
        res = true;
        break;
      }
    }
    return res || pointIn(x,y);
  }
  
}

public Polygon loadPolygon(XML xml) {
  XML[] v = xml.getChildren("vertex");
  int l=v.length;
  float[] xs = new float[l], ys = new float[l];
  for (int i=0; i<l; i++) {
    xs[i]=v[i].getFloat("x");
    ys[i]=v[i].getFloat("y");
  }
  return new Polygon(xs, ys);
}
class weapon {
  int dmg, blife=1000;
  float cooldown, t, speed=10, angle;
  colobok owner;

  weapon(colobok o, float icooldown, int idmg) {
    t=0;
    owner=o;
    cooldown=icooldown;
    dmg=idmg;
  }

  public void shoot(float angle) {
    this.angle = angle;
    if (t<=0) {
      bullets.add(new bullet(owner.x+(1+owner.r)*cos(angle), owner.y+(1+owner.r)*sin(angle), dmg, blife, new PVector(speed*cos(angle), speed*sin(angle))));
      t=cooldown;
    }
  }

  public void display() {
    strokeWeight(10*mx);
    stroke(50);
    line(owner.x, owner.y, owner.x+owner.r*cos(angle), owner.y+owner.r*sin(angle));
  }

  public void update() {
    t--;
    display();
  }
}

class shotgun extends weapon {
  shotgun(colobok o, float icooldown, int idmg) {
    super(o, icooldown, idmg);
  }

  public void shoot(float angle) {
    if (t<=0) {
      for (int i=0; i<10; i++) {
        float da=(random(1)-0.5f)*QUARTER_PI*0.3f;
        bullets.add(new bullet(owner.x+(1+owner.r)*cos(angle+da), 
          owner.y+(1+owner.r)*sin(angle+da), dmg, blife, 
          new PVector(speed*cos(angle+da), speed*sin(angle+da))));
      }
      t=cooldown;
    }
  }
}

class sWeapon extends weapon {
  sWeapon(colobok o, float icooldown, int idmg) {
    super(o, icooldown, idmg);
  }

  public void shoot(float angle) {
    if (t<=0) {
      for (angle=0; angle<TWO_PI; angle+=PI/20) {
        bullets.add(new bullet(owner.x+(1+owner.r)*cos(angle), owner.y+(1+owner.r)*sin(angle), dmg, blife, new PVector(speed*cos(angle), speed*sin(angle))));
        display();
      }
      t=cooldown;
    }
  }
  public void update() {
    t--;
  }
}

class rLauncher extends weapon {
  rLauncher(colobok o, float icooldown, int idmg) {
    super(o, icooldown, idmg);
  }
  public void shoot(float angle) {
      if (t<=0) {
        bullets.add(new rocket(owner.x+(10+owner.r)*cos(angle), owner.y+(10+owner.r)*sin(angle), dmg, new PVector(speed*cos(angle), speed*sin(angle))));
        t=cooldown;
      }
  }
}

class laser extends weapon {
  laser(colobok o, float icooldown, int dmg) {
    super(o, icooldown, dmg);
  }

  public void shoot(float angle) {
  }
}
  public void settings() {  size(displayWidth,displayHeight,P2D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "Processing_server" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
