item[] typeItems=new item[7];
ArrayList<bullet> bullets = new ArrayList<bullet>();
ArrayList<item> items = new ArrayList<item>();
ArrayList<Polygon> walls;
PImage background;
ConnectionManager manager;
Button resetButton;
float width_, height_;
int port, nPlayers;

void loadProperties(){
  XML properties = loadXML("properties.xml");
  nPlayers = properties.getInt("players");
  XML serverInfo = properties.getChild("server");
  port = serverInfo.getInt("port");
  height_ = properties.getFloat("height");
  width_ = properties.getFloat("width"); 
}

void setup() {
  size(displayWidth,displayHeight,P2D);
  mx=max(width,height)/1280.0;
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

void reset(){
  bullets.clear();
  manager.reset();
  items.clear();
}

void draw() {
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
