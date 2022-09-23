package fathertoast.crust.api.impl;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.IRegistryHelper;

public final class CrustApi implements ICrustApi {

    private final IRegistryHelper registryHelper;

    public CrustApi() {
        registryHelper = new RegistryHelper();
    }

    @Override
    public IRegistryHelper getRegistryHelper() {
        return registryHelper;
    }
}
