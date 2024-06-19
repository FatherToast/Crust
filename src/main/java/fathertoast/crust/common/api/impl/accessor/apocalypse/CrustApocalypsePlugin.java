package fathertoast.crust.common.api.impl.accessor.apocalypse;

import com.toast.apocalypse.api.plugin.ApocalypsePlugin;
import com.toast.apocalypse.api.plugin.IApocalypseApi;
import com.toast.apocalypse.api.plugin.IApocalypsePlugin;
import fathertoast.crust.common.api.impl.CrustApi;
import fathertoast.crust.common.core.Crust;

import javax.annotation.Nullable;

@ApocalypsePlugin(modid = CrustApi.MOD_ID)
public class CrustApocalypsePlugin implements IApocalypsePlugin {

    /** Instance of Apocalypse's API. */
    @Nullable
    public static IApocalypseApi api;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void load(IApocalypseApi iApocalypseApi) {
        api = iApocalypseApi;
        // Passes Apocalypse's difficulty provider to Crust's difficulty accessor
        ((DifficultyAccessor) Crust.INSTANCE.apiInstance.getDifficultyAccessor()).setDifficultyProvider( api.getDifficultyProvider() );
    }

    @Override
    public String getPluginId() {
        return "crust_plugin";
    }
}
