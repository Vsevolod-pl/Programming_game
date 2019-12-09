PImage blend_s(PImage a, PImage b){
  int w=a.width,
  h=a.height;
  b.resize(w,h);
  a.loadPixels();
  b.loadPixels();
  float f;
  for(int x=0;x<w;x++){
    for(int y=0;y<h;y++){
      f=noise(x/100.0,y/100.0);
      /*if(f>0.5)
       f=1;
      else
        f=0;
      //f=sqrt(f);*/
      int r,g,bl;
      
      r=int(f*red(a.pixels[x+y*w])
      +(1-f)*red(b.pixels[x+y*w]));
      g=int(f*green(a.pixels[x+y*w])
      +(1-f)*green(b.pixels[x+y*w]));
      bl=int(f*blue(a.pixels[x+y*w])
      +(1-f)*blue(b.pixels[x+y*w]));
      
      a.pixels[x+y*w]=color(r,g,bl);
    }
  }
  a.updatePixels();
  return a;
}
