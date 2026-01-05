plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-common")
}

dependencies {
    modCompileOnlyApi(libs.puzzleslib.common)
    modCompileOnlyApi(libs.neoforgedatapackextensions.common)
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
