[ ![Download](https://api.bintray.com/packages/arnis71/maven/konductor-arch/images/download.svg) ](https://bintray.com/arnis71/maven/konductor-arch/_latestVersion)

# Konductor architecture

## What is this?

This is an experimental framework that provides tools for building an app with reactive/data-binding methodology. It heavily relies on Conductor and Anko Layouts libraries.

## Why use this?

This framework is all about perfomance 

Anko Layouts:
 - no xml, no inflation, which is very costly
 - no findViewById... even under the hood!
 
 Conductor:
 - 1 activity, no fragments, all views
 - gets rid of convoluted android lifecycles
 - less memmory consumption becuase all navigaition is done with the views
 
 Architecture:
 - borrows main principles from MVVM
 - bind data once and it updates every time your layout has changed or data has been updated
 - ui and data reusability
 - reactive at its nature
 - data binding without code generation and tricky xml stuff

## Installation

To include konductor and/or konductor-arch module into your project provide Gradle dependecy
```gradle
implementation "com.arnis:konductor-arch:$latestVersion"
```

## Building blocks of Konductor architecture

#### KonductorActivity

This is a base class that your MaincActivity should extend. The only method you have to override is the one to provide your root ViewKontroller.

#### ViewKontroller

This is a `View` part of the architecture and it should only define the layout and bind to the provider. It should not have any logic inside it. For this class you have to provide the `TAG` for conductor transactions and onLayout method that describes your layout. Also there is an option to bind the DataFlowProvider. To get the data flowing into your layout you should call `flow<DATA>()` and pass in the lambda that will update your UI every time the layout is changed or data has been updated.

#### DataFlowProvider

This is a `Presenter/ViewModel` type of layer which is responisble for gathering the data (usually from Repositories) and wrap it in DataFlow which later provides data for the ViewKontroller. Providers are registered in Application class and remain in memmory as singletones. They can attach to multiple ViewKontrollers and share data between them. It automaticaly tracks the ViewKontroller lifecycle and clears all the data when needed.

#### DataFlow

This is reactive data holder that provides data to the **ViewKontroller**. It is active during the ViewKontroller lifecycle and updates the data whenever the source is changing it or when the layout requests it. There is 3 main types of DataFlow classes:

### DataFlow

Basic data type which is used for data source that can be wrapped inside a lambda.

### DeferredDataFlow

Same as previous but automatically unwraps the Deffered via kotlin coroutines and provides the value asynchronously.

### DirectDataFlow

Used for data sources that can't be stored inside a lambda (created elsewhere). Stores the cached value that has been provided by Provider and flows it to ViewKontroller upon request.

## Usage and code samples

Coming soon...

## TODO
- figure out all the possible implementations
- stabilize the api
