package fathertoast.crust.common.api.impl.accessor.apocalypse;

import com.toast.apocalypse.api.plugin.ApocalypsePlugin;
import com.toast.apocalypse.api.plugin.IApocalypseApi;
import com.toast.apocalypse.api.plugin.IApocalypsePlugin;
import fathertoast.crust.common.api.impl.CrustApi;
import fathertoast.crust.common.core.Crust;

import javax.annotation.Nullable;

@ApocalypsePlugin( modId = CrustApi.MOD_ID )
@SuppressWarnings( "unused" )
public class CrustApocalypsePlugin implements IApocalypsePlugin {
    
    /** Instance of Apocalypse's API. */
    @Nullable
    public static IApocalypseApi api;
    
    @SuppressWarnings( "ConstantConditions" )
    @Override
    public void load( IApocalypseApi iApocalypseApi ) {
        api = iApocalypseApi;
        // Passes Apocalypse's difficulty accessor to Crust's difficulty accessor
        ((ApocalypseDifficultyAccessor) Crust.INSTANCE.API.getDifficultyAccessor()).setDifficultyProvider( api.getDifficultyProvider() );
    }
    
    @Override
    public String getPluginId() {
        return "crust_plugin";
    }
}
