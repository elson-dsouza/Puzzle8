package com.google.engedu.puzzle8;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Random;

public class PuzzleBoardView extends View {
    public static final int NUM_SHUFFLE_STEPS = 40;
    private Activity activity;
    private PuzzleBoard puzzleBoard;
    private ArrayList<PuzzleBoard> animation;
    private Random random = new Random();

    public PuzzleBoardView(Context context) {
        super(context);
        activity = (Activity) context;
        animation = null;
    }

    public void initialize(Bitmap imageBitmap, View parent) {
        int width = getWidth();
        puzzleBoard = new PuzzleBoard(imageBitmap, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (puzzleBoard != null) {
            if (animation != null && animation.size() > 0) {
                puzzleBoard = animation.remove(0);
                puzzleBoard.draw(canvas);
                if (animation.size() == 0) {
                    animation = null;
                    puzzleBoard.reset();
                    Toast toast = Toast.makeText(activity, "Solved! ", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    this.postInvalidateDelayed(700);
                }
            } else {
                puzzleBoard.draw(canvas);
            }
        }
    }

    public void shuffle() {
        if (animation == null && puzzleBoard != null) {
            Random r=new Random();
            PuzzleBoard randomBoard=puzzleBoard;
            for(int i=0;i<NUM_SHUFFLE_STEPS;i++){
                ArrayList<PuzzleBoard> neighbours=randomBoard.neighbours();
                randomBoard=neighbours.get(r.nextInt(neighbours.size()));
            }
            puzzleBoard=randomBoard;
            invalidate();
            puzzleBoard.logit();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animation == null && puzzleBoard != null) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (puzzleBoard.click(event.getX(), event.getY())) {
                        invalidate();
                        if (puzzleBoard.resolved()) {
                            Toast toast = Toast.makeText(activity, "Congratulations!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        return true;
                    }
            }
        }
        return super.onTouchEvent(event);
    }

    public void solve() {
        PriorityQueue<PuzzleBoard> queue = new PriorityQueue<>(10, new Comparator<PuzzleBoard>() {
            @Override
            public int compare(PuzzleBoard lhs, PuzzleBoard rhs) {
                if(lhs.dist() == rhs.dist())
                {
                    return lhs.compareStates(rhs);
                }
                else if(lhs.priority()<rhs.priority())
                    return -1;
                else
                    return  1;
            }
        });
        ArrayList<PuzzleBoard> nextStates,prevStates = new ArrayList<>();
        puzzleBoard.reset();
        PuzzleBoard min = new PuzzleBoard(puzzleBoard);
        min.reset();
        queue.add(min);
        while(!queue.isEmpty() && !min.resolved()){
            min = queue.poll();
            prevStates.add(min);
            if(!min.resolved())
            {
                nextStates = min.neighbours();
                for (PuzzleBoard state:nextStates) {
                    if(prevStates.contains(state))
                        continue;
                    else{
                        queue.add(state);
                    }

                }
            }
            else {
                ArrayList<PuzzleBoard> sequence = new ArrayList<>();
                sequence.add(min);
                while(min.getPreviousBoard()!=null)
                {
                    sequence.add(min.getPreviousBoard());
                    min = min.getPreviousBoard();
                }
                sequence.add(min);
                Collections.reverse(sequence);
                animation = (ArrayList<PuzzleBoard>)sequence.clone();
                break;
            }
        }
        invalidate();

    }
}
