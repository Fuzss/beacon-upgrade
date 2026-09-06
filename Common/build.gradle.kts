plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-common")
}

dependencies {
    modCompileOnlyApi(sharedLibs.puzzleslib.common)
    compileOnlyApi(sharedLibs.neoforgedatapackextensions.common)
}

multiloader {
    mixins {
        mixin(
            "BeaconBlockEntityMixin",
            "FoodDataMixin",
            "PatrolSpawnerMixin",
            "PlayerMixin",
            "WanderingTraderSpawnerMixin"
        )
    }
}
