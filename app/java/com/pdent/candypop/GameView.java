package com.pdent.candypop;
import android.content.Context;
import android.graphics.*;
import android.view.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashSet;

public class GameView extends View {
 private static final int S=8, E=-1;
 private final int[] colors={Color.rgb(255,65,90),Color.rgb(255,190,35),Color.rgb(55,190,255),Color.rgb(95,220,105),Color.rgb(190,105,255),Color.rgb(255,105,190)};
 private final int[][] b=new int[S][S];
 private final Random rng=new Random();
 private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
 private final RectF box=new RectF();
 private int score=0,moves=30,selR=-1,selC=-1;
 private boolean over=false,busy=false;
 private float cell,left,top,startX,startY;
 public GameView(Context c){super(c);setLayerType(View.LAYER_TYPE_SOFTWARE,null);newGame();}
 private void newGame(){
  score=0;moves=30;over=false;busy=false;selR=selC=-1;
  for(int r=0;r<S;r++)for(int c=0;c<S;c++){
   int v; do{v=rng.nextInt(6);}while((c>1&&b[r][c-1]==v&&b[r][c-2]==v)||(r>1&&b[r-1][c]==v&&b[r-2][c]==v));
   b[r][c]=v;
  } invalidate();
 }
 @Override protected void onDraw(Canvas cv){
  super.onDraw(cv);float w=getWidth(),h=getHeight();cv.drawColor(Color.rgb(22,17,48));
  p.setShader(null);p.setColor(Color.rgb(43,31,78));box.set(12,12,w-12,h-12);cv.drawRoundRect(box,26,26,p);
  p.setTypeface(Typeface.create("sans-serif-black",1));p.setTextAlign(Paint.Align.CENTER);p.setColor(Color.WHITE);p.setTextSize(Math.min(w*.085f,40));cv.drawText("CANDY POP",w/2,62,p);
  p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextSize(18);p.setColor(Color.rgb(255,218,110));cv.drawText("SCORE  "+score+"       MOVES  "+moves,w/2,98,p);
  cell=Math.min((w-34)/8f,(h-185)/8f);float size=cell*8;left=(w-size)/2;top=Math.max(116,(h-size)/2+20);
  box.set(left-5,top-5,left+size+5,top+size+5);p.setColor(Color.rgb(65,49,104));cv.drawRoundRect(box,15,15,p);
  for(int r=0;r<8;r++)for(int c=0;c<8;c++){
   float x=left+c*cell,y=top+r*cell;box.set(x+2,y+2,x+cell-2,y+cell-2);p.setShader(null);p.setColor(Color.rgb(38,29,67));cv.drawRoundRect(box,8,8,p);
   if(b[r][c]!=E)drawCandy(cv,r,c,x,y);
   if(r==selR&&c==selC){p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);p.setColor(Color.WHITE);box.set(x+3,y+3,x+cell-3,y+cell-3);cv.drawRoundRect(box,9,9,p);p.setStyle(Paint.Style.FILL);}
  }
  if(over){
   p.setShader(null);p.setColor(0xCC100B25);cv.drawRect(0,0,w,h,p);p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.create("sans-serif-black",1));p.setTextSize(36);p.setColor(Color.WHITE);cv.drawText("GAME OVER",w/2,h/2-30,p);
   p.setTextSize(20);p.setColor(Color.rgb(255,218,110));cv.drawText("Final score: "+score,w/2,h/2+8,p);
   box.set(w/2-105,h/2+38,w/2+105,h/2+98);p.setColor(Color.rgb(255,90,145));cv.drawRoundRect(box,18,18,p);p.setColor(Color.WHITE);cv.drawText("PLAY AGAIN",w/2,h/2+76,p);
  }else{p.setShader(null);p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.DEFAULT);p.setTextSize(13);p.setColor(Color.rgb(190,177,220));cv.drawText("SWIPE TO MATCH 3 OR MORE",w/2,Math.min(h-22,top+size+28),p);}
 }
 private void drawCandy(Canvas cv,int r,int c,float x,float y){
  float cx=x+cell/2,cy=y+cell/2,rad=cell*.37f;int color=colors[b[r][c]];
  p.setShader(null);p.setColor(0x66000000);cv.drawCircle(cx+1,cy+3,rad+1,p);
  p.setShader(new RadialGradient(cx-rad*.35f,cy-rad*.42f,rad*1.55f,new int[]{light(color),color,dark(color)},new float[]{0,.55f,1},Shader.TileMode.CLAMP));
  cv.drawCircle(cx,cy,rad,p);p.setShader(null);p.setColor(0x99FFFFFF);cv.drawOval(cx-rad*.55f,cy-rad*.68f,cx+rad*.12f,cy-rad*.35f,p);
  p.setColor(0x44FFFFFF);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1);cv.drawCircle(cx,cy,rad-1,p);p.setStyle(Paint.Style.FILL);
 }
 private int light(int c){return Color.rgb(Math.min(255,Color.red(c)+65),Math.min(255,Color.green(c)+65),Math.min(255,Color.blue(c)+65));}
 private int dark(int c){return Color.rgb(Color.red(c)*2/3,Color.green(c)*2/3,Color.blue(c)*2/3);}
 private HashSet<Integer> matches(){
  HashSet<Integer> hit=new HashSet<Integer>();
  for(int r=0;r<8;r++){int c=0;while(c<8){int v=b[r][c],e=c+1;while(v!=E&&e<8&&b[r][e]==v)e++;if(v!=E&&e-c>=3)for(int k=c;k<e;k++)hit.add(r*8+k);c=e;}}
  for(int c=0;c<8;c++){int r=0;while(r<8){int v=b[r][c],e=r+1;while(v!=E&&e<8&&b[e][c]==v)e++;if(v!=E&&e-r>=3)for(int k=r;k<e;k++)hit.add(k*8+c);r=e;}}
  return hit;
 }
 private void settle(){
  int guard=0;HashSet<Integer> hit=matches();
  while(!hit.isEmpty()&&guard++<30){
   score+=hit.size()*10;
   for(Integer id:hit)b[id/8][id%8]=E;
   for(int c=0;c<8;c++){int write=7;for(int r=7;r>=0;r--)if(b[r][c]!=E)b[write--][c]=b[r][c];while(write>=0)b[write--][c]=rng.nextInt(6);}
   hit=matches();
  }
  if(moves<=0)over=true;busy=false;invalidate();
 }
 private void swap(int r1,int c1,int r2,int c2){
  if(over||busy||r2<0||r2>7||c2<0||c2>7)return;
  int t=b[r1][c1];b[r1][c1]=b[r2][c2];b[r2][c2]=t;
  if(matches().isEmpty()){t=b[r1][c1];b[r1][c1]=b[r2][c2];b[r2][c2]=t;}
  else{moves--;settle();}
  selR=selC=-1;invalidate();
 }
 @Override public boolean onTouchEvent(MotionEvent e){
  float x=e.getX(),y=e.getY();
  if(e.getAction()==MotionEvent.ACTION_DOWN){startX=x;startY=y;return true;}
  if(e.getAction()==MotionEvent.ACTION_UP){
   if(over){if(y>getHeight()/2+25&&y<getHeight()/2+120)newGame();return true;}
   int c=(int)((startX-left)/cell),r=(int)((startY-top)/cell);
   if(r<0||r>7||c<0||c>7)return true;
   float dx=x-startX,dy=y-startY;
   if(Math.abs(dx)+Math.abs(dy)>18){
    if(Math.abs(dx)>Math.abs(dy))swap(r,c,r,c+(dx>0?1:-1));
    else swap(r,c,r+(dy>0?1:-1),c);
   }else{selR=r;selC=c;invalidate();}
   return true;
  }return true;
 }
}
