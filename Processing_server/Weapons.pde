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

  void shoot(float angle) {
    this.angle = angle;
    if (t<=0) {
      bullets.add(new bullet(owner.x+(1+owner.r)*cos(angle), owner.y+(1+owner.r)*sin(angle), dmg, blife, new PVector(speed*cos(angle), speed*sin(angle))));
      t=cooldown;
    }
  }

  void display() {
    strokeWeight(10*mx);
    stroke(50);
    line(owner.x, owner.y, owner.x+owner.r*cos(angle), owner.y+owner.r*sin(angle));
  }

  void update() {
    t--;
    display();
  }
}

class shotgun extends weapon {
  shotgun(colobok o, float icooldown, int idmg) {
    super(o, icooldown, idmg);
  }

  void shoot(float angle) {
    if (t<=0) {
      for (int i=0; i<10; i++) {
        float da=(random(1)-0.5)*QUARTER_PI*0.3;
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

  void shoot(float angle) {
    if (t<=0) {
      for (angle=0; angle<TWO_PI; angle+=PI/20) {
        bullets.add(new bullet(owner.x+(1+owner.r)*cos(angle), owner.y+(1+owner.r)*sin(angle), dmg, blife, new PVector(speed*cos(angle), speed*sin(angle))));
        display();
      }
      t=cooldown;
    }
  }
  void update() {
    t--;
  }
}

class rLauncher extends weapon {
  rLauncher(colobok o, float icooldown, int idmg) {
    super(o, icooldown, idmg);
  }
  void shoot(float angle) {
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

  void shoot(float angle) {
  }
}
