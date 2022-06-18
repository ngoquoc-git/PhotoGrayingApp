package com.example.photograying;

public class MathRounding {
    public static float keepTwoDigits( float f ) {
        int n = ( int ) ( 100 * f );
        return ( ( float ) n ) / 100;
    }
}