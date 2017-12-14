[ ![Download](https://api.bintray.com/packages/arnis71/maven/konductor-arch/images/download.svg) ](https://bintray.com/arnis71/maven/konductor-arch/_latestVersion)

# Konductor

This is a modified version of Conductor (https://github.com/bluelinelabs/Conductor) which currently being ported to Kotlin. Along with `konductor` module there is an experimental `konductor-arch` module that provides tools for building an app with reactive/data-binding methodology.

# Installation

To include konductor and/or konductor-arch module into your project provide Gradle dependecies
```gradle
implementation "com.arnis:konductor:$latestVersion"
implementation "com.arnis:konductor-arch:$latestVersion"
```

# Konductor

### KonductorActivity

Your project should only have 1 activity that extends from `KonductorActivity`.

Further info in development...


# Konductor architecture

These are 3 main components in this module based on which you should build your app. Also it implies that you build your layout in **anko** instead of xml. Xml layouts support is in the works.


### ViewKontroller

This is a `View` part of the architecture and it should only define the layout and connection to the abstraction layer. It should not have any logic inside it and it should route all the events to the abstraction layer.

Important notes:
  - view that routes events to the abstraction should have an id

### Abstraction

This is a `Presenter/ViewModel` type of layer which is responisble for handling all the interactions with the `View` and updating the bindings. It also stores all the active DataFlows and provides the api to register and dispatch updates to it.

### DataFlow

This is reactive data holder that binds to the **ViewKontroller** and receive updates when needed. It is active during the ViewKontroller lifecycle and updates the data whenever the source is changing it or after user generated events.
