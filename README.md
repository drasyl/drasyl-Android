[<img src="https://docs.drasyl.org/master/assets/img/logo.svg" alt="drasyl" width="200"/>](https://drasyl.org)

[Website](https://drasyl.org) |
[Documentation](https://docs.drasyl.org) |
[Javadoc](https://api.drasyl.org)

# APK

You can download the current APK from here [app/release/app-release.apk](app/release/app-release.apk).

# Notes

If you want to write your Android project with drasyl, you have to be aware of a few things. 
drasyl uses the crypto library [sodium](https://libsodium.gitbook.io/). 
The drasyl Java version already contains pre-compiled binaries of this library for the most common platforms and architectures. 
However, android packages behave differently and need all their native libraries in the [libs](app/libs) folder. 
So don't forget to copy the [libs](app/libs) folder of this project for your own.

The Android support of drasyl is still experimental at the moment. 
Therefore you may have to include the snapshot repo from sonatype. 
You can simply copy the [settings.gradle](settings.gradle) file of this project 
or add the following to your file:

```
dependencyResolutionManagement {
    ...
    repositories {
        ...
        // Added maven SNAPSHOT repo for drasyl SNAPSHOT versions
        maven {
            url "https://oss.sonatype.org/content/repositories/snapshots/"
        }
        ...
    }
    ...
}
```

# License

This is free software under the terms of the [MIT License](LICENSE)
