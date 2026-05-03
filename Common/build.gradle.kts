plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-common")
}

dependencies {
    modCompileOnlyApi(sharedLibs.puzzleslib.common)
    modCompileOnlyApi(sharedLibs.neoforgedatapackextensions.common)
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
