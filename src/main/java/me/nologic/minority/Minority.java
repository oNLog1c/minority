package me.nologic.minority;

import me.nologic.minority.feature.FuriousMonstersExampleFeature;

public final class Minority extends MinorityExtension {

    @Override
    public void onEnable() {
        this.getConfigurationWizard().generate(FuriousMonstersExampleFeature.class);
    }

}