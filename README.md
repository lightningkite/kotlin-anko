# kotlin-anko

This repository is Android specific, and is *not* cross-platform.  For a good example of use, take a look at [kotlin-components-starter](https://github.com/UnknownJoe796/kotlin-components-starter).

This repository contains a slew of things that we did not have from [Anko](https://github.com/Kotlin/anko).

## Anko

This framework is based on Anko, a UI building library that uses Kotlin DSL instead of XML.  
This allows your layout and associated code to be mixed, allowing greater cohesion in your apps.  
While you should separate out business logic from display logic, now the display logic can actually  
be where the layout is.

Note that Anko is still based on Android views, so everything should still be compatible.

## Overview

There are such a large number of functions, you're better off exploring the repository.  It's all designed to be easily read.  All of them, however, are Android specific.  Some of the really important points:

`View.lifecycle` - This gives lifecycle capabilities to Android views.  Lifecycles are objects that allow listening to the creation and destruction of things.  These are really useful with the observable package we've created; the package that helps in using the two together is [kotlin-anko-observable](https://github.com/lightningkite/kotlin-anko-observable).  I highly recommend it.

Async - This gives a way to tell the [kotlin-core](https://github.com/lightningkite/kotlin-core) package what the UI thread is and how to post something to it.  This is automatically enabled if also using [kotlin-anko-view-controllers](https://github.com/lightningkite/kotlin-anko-view-controllers), which I highly recommend.
