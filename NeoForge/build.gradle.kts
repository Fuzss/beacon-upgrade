plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-neoforge")
}

dependencies {
    modApi(sharedLibs.puzzleslib.neoforge)
    api(sharedLibs.neoforgedatapackextensions.neoforge)
    include(sharedLibs.neoforgedatapackextensions.neoforge)
}

multiloader {
    mixins {
        clientMixin("BlockStateModelLoaderNeoForgeMixin")
    }
}
