package fathertoast.crust.api.util;

import fathertoast.crust.api.lib.CrustMath;

public abstract class ColoredShape implements IDebugShape {
    
    protected final float alpha;
    protected final float red;
    protected final float green;
    protected final float blue;
    
    public ColoredShape( int color ) {
        this( CrustMath.getAlpha( color ), CrustMath.getRed( color ), CrustMath.getGreen( color ), CrustMath.getBlue( color ) );
    }
    
    public ColoredShape( float a, float r, float g, float b ) {
        alpha = a;
        red = r;
        green = g;
        blue = b;
    }
}