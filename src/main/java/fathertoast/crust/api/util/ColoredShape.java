package fathertoast.crust.api.util;

import fathertoast.crust.api.lib.CrustMath;

public abstract class ColoredShape implements IDebugShape {
    
    protected float alpha;
    protected float red;
    protected float green;
    protected float blue;
    
    public ColoredShape( int color ) {
        setColor( color );
    }
    
    public ColoredShape( float a, float r, float g, float b ) {
        setColor( a, r, g, b );
    }

    public void setColor( float a, float r, float g, float b ) {
        alpha = a;
        red = r;
        green = g;
        blue = b;
    }

    public void setColor( int color ) {
        setColor(
                CrustMath.getAlpha( color ),
                CrustMath.getRed( color ),
                CrustMath.getGreen( color ),
                CrustMath.getBlue( color )
        );
    }
}