package com.example.photograying;

import android.graphics.Bitmap;
import android.graphics.Color;

public class BitmapGrayer {
    private Bitmap originalBitmap;
    private float redCoeff;
    private float greenCoeff;
    private float blueCoeff;
    public BitmapGrayer(Bitmap bitmap, float newRedCoeff,
                        float newGreenCoeff, float newBlueCoeff ) {
        originalBitmap = bitmap;
        setRedCoeff( newRedCoeff );
        setGreenCoeff( newGreenCoeff );
        setBlueCoeff( newBlueCoeff );
    }
    public float getRedCoeff( ) {
        return redCoeff;
    }
    public float getGreenCoeff( ) {
        return greenCoeff;
    }
    public float getBlueCoeff( ) {
        return blueCoeff;
    }
    public void setRedCoeff( float newRedCoeff ) {
        if( newRedCoeff >= 0 && newRedCoeff <= 1 ) {
            if( greenCoeff + blueCoeff + newRedCoeff <= 1 )
                redCoeff = newRedCoeff;
            else
                redCoeff = 1 - greenCoeff - blueCoeff;
        }
    }
    public void setGreenCoeff( float newGreenCoeff ) {
        if( newGreenCoeff >= 0 && newGreenCoeff <= 1 ) {
            if( redCoeff + blueCoeff + newGreenCoeff <= 1 )
                greenCoeff = newGreenCoeff;
            else
                greenCoeff = 1 - redCoeff - blueCoeff;
        }
    }
    public void setBlueCoeff( float newBlueCoeff ) {
        if (newBlueCoeff >= 0 && newBlueCoeff <= 1) {
            if (redCoeff + greenCoeff + newBlueCoeff <= 1)
                blueCoeff = newBlueCoeff;
            else
                blueCoeff = 1 - redCoeff - greenCoeff;
        }
    }
    public Bitmap grayScale( ) {
        int width = originalBitmap.getWidth( );
        int height = originalBitmap.getHeight( );
        Bitmap.Config config = originalBitmap.getConfig( );
        Bitmap bitmap = Bitmap.createBitmap( width, height, config);
        for( int i = 0; i < width; i++ ) {
            for( int j = 0; j < height; j++ ) {
                int color = originalBitmap.getPixel( i, j );
                int shade = ( int ) ( redCoeff * Color.red( color )
                        + greenCoeff * Color.green( color )
                        + blueCoeff * Color.blue( color ) ) ;
                color = Color.argb( Color.alpha( color ), shade, shade, shade );
                bitmap.setPixel( i, j, color );
            }
        }
        return bitmap;
    }

}