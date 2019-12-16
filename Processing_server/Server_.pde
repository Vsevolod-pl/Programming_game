import processing.net.*;

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
  
  void reset(){
    server.write("\'restart\'");
    status = 0;
    players.clear();
    blood.clear();
  }
  
  void update(){
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
  
  void pointIn(bullet b){
    for(ClientPlayer p: players){
      if(p.pointIn(b.x,b.y)){
        p.hp -= b.dmg;
        b.life = 0;
        //blood.add(b.x,b.y);
      }
    }
  }
  
  void clearDead(){
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
