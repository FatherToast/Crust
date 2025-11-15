package fathertoast.crust.test.common.block;

import fathertoast.crust.api.util.BoxShape;
import fathertoast.crust.api.util.IDebugShape;
import fathertoast.crust.api.util.IDebugShapeProvider;
import fathertoast.crust.api.util.shape.CircleShape;
import fathertoast.crust.api.util.shape.CylinderShape;
import fathertoast.crust.api.util.shape.QuadShape;
import fathertoast.crust.api.util.shape.SphereShape;
import fathertoast.crust.test.common.TestCrustObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TestBlockEntity extends BlockEntity implements IDebugShapeProvider {
    
    public TestBlockEntity( BlockPos pos, BlockState state ) {
        super( TestCrustObjects.Obj.TEST_BE.get(), pos, state );
    }
    
    
    // ---- IDebugShapeProvider Implementation ---- //
    
    private List<IDebugShape> debugShapes;
    
    @Nullable
    @Override // IDebugShapeProvider
    public List<IDebugShape> getDebugShapes() {
        if( debugShapes == null ) {
            // Legacy junk
            //debugShapes = IDebugShapeProvider.fromBBs( new AABB( getBlockPos() ).inflate( 1.0, 1.0, 1.0 ) );
            
            debugShapes = new ArrayList<>();
            //putBoxes( debugShapes );
            //putQuads( debugShapes );
            //putCircles( debugShapes );
            putSpheres( debugShapes );
            //putCylinders( debugShapes );
        }
        return debugShapes;
    }
    
    private static void putBoxes( List<IDebugShape> debugShapes ) {
        debugShapes.add( new BoxShape( 3.0, 5.0 ).withColor( 0x7FFF00FF ) );
    }
    
    private static void putQuads( List<IDebugShape> debugShapes ) {
        debugShapes.add( new QuadShape( Direction.Axis.X, 2.0F, 2.0F ).withColor( 0xFF0000 ) );
        debugShapes.add( new QuadShape( Direction.Axis.Y, 2.0F, 2.0F ).withColor( 0x00FF00 ) );
        debugShapes.add( new QuadShape( Direction.Axis.Z, 2.0F, 2.0F ).withColor( 0x0000FF ) );
    }
    
    private static void putCircles( List<IDebugShape> debugShapes ) {
        debugShapes.add( new CircleShape( Direction.Axis.X, 1.0F ).withColor( 0xFF0000 ) );
        debugShapes.add( new CircleShape( Direction.Axis.Y, 1.0F ).withColor( 0x00FF00 ) );
        debugShapes.add( new CircleShape( Direction.Axis.Z, 1.0F ).withColor( 0x0000FF ) );
        
        debugShapes.add( new CircleShape( Direction.NORTH, 5.0F ).withColor( 0x00FFFF ) );
        debugShapes.add( new CircleShape( Direction.UP, 16.0F ).withColor( 0xFF00FF ) );
        debugShapes.add( new CircleShape( Direction.DOWN, 32.0F ).withColor( 0xFFFF00 ) );
    }
    
    private static void putSpheres( List<IDebugShape> debugShapes ) {
        debugShapes.add( new SphereShape( Direction.Axis.X, 1.0F ).withColor( 0xFF0000 ) );
        debugShapes.add( new SphereShape( Direction.Axis.Y, 3.0F ).withColor( 0x00FF00 ) );
        debugShapes.add( new SphereShape( Direction.Axis.Z, 9.0F ).withColor( 0x0000FF ) );
        
        debugShapes.add( new SphereShape( 16.0F ).withColor( 0xFF00FF ) );
        debugShapes.add( new SphereShape( Direction.DOWN, 32.0F ).withColor( 0xFFFF00 ) );
    }
    
    private static void putCylinders( List<IDebugShape> debugShapes ) {
        debugShapes.add( new CylinderShape( Direction.Axis.X, 1.0F, 2.0F ).withColor( 0xFF0000 ) );
        debugShapes.add( new CylinderShape( Direction.Axis.Y, 16.0F, 3.0F ).withColor( 0x00FF00 ) );
        debugShapes.add( new CylinderShape( Direction.Axis.Z, 1.5F, 32.0F ).withColor( 0x0000FF ) );
    }
}