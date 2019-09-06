package com.bc.memorytest;

import com.badlogic.gdx.math.Interpolation;

public class Tween
{
    public float time;
    public float duration;

    public Tween(float duration)
    {
        this.time = 0;
        this.duration = duration;
    }

    public void update(float deltaTime)
    {
        time += deltaTime;
    }

    public int apply(int start, int end, Interpolation interpolationType)
    {
        return (int)interpolationType.apply(start, end, time / duration);
    }
    
    public float apply(float start, float end, Interpolation interpolationType)
    {
        return interpolationType.apply(start, end, time / duration);
    }
}