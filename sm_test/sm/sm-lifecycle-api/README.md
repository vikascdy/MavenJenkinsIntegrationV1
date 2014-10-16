# ServiceManager Lifecycle Events

There are multiple types of Lifecycle events. Some are simple scripts that need to be run to do things such as run SQL
scripts against a DB.

More advanced tools are sometimes required that need access to most of an applications resources including services.
This API is to solve these problems. For example, an application on installation may need to make calls to other services
or applications running in the cluster.

This API is designed to expose the same API available from services, into an API that can be used for these events.

## Terminology

The terms _lifecycle event_ and _lifecycle action_ have different meanings. A _lifecycle event_ refers to a particular
app-level event (`preInstall`, `postInstall`, `upgrade`, etc.), whereas a _lifecycle action_ refers to some action that
is part of, or triggered by, a lifecycle event. So, for example, an app's `preInstall` lifecycle event may contain two
lifecycle actions: setting up the database and downloading some required libraries. 

## The _AbstractLifecycleAction_ Class

All Java-based lifecycle-event-triggered actions are implemented as subclasses of `AbstractLifecycleAction`:

    public abstract class AbstractLifecycleAction {
      public abstract void onEvent(Map<String, Object> properties);
    }

When the associated event occurs, `onEvent` is called. If `onEvent` returns without throwing an exception, the action is
considered complete; if it throws a `RuntimeException`, that exception will be reported as part of the lifecycle event.

## The _@LifecycleAction_ Annotation

An `AbstractLifecycleAction` must be annotated with `@LifecycleAction`; the relationship between the class and
annotation is the same as `AbstractService` and `@Service`, `AbstractCommandHandler` and `@CommandHandler`, etc., except
that lifecycle actions do not need to be split into interfaces and implementations.

Here is an example of a fully-annotated lifecycle action:

    @LifecycleAction(
      name = "Test Action",
      event = LifecycleEvent.PreInstall,
      properties = {
        @Property(name="a", type=String.class),
        @Property(name="b", type=String.class)})
    public class TestAction extends AbstractLifecycleAction {
      @Override public void onEvent(Map<String, Object> properties) {
        System.out.println("a: " + properties.get("a"));
        System.out.println("b: " + properties.get("b"));
      }
    }

  * `name` is an optional property. It is the name of the action, which may be used for logging/debugging purposes.
  * `event` is a required property. It is an item from the `LifecycleEvent` enum, which specifies the kind of event that
    triggers this action.
    
    // TODO: The concept of Event or execution order, may change and needs to be reviewed.
    
  * `properties` is an optional property. It is an array of `@Property` annotations which provides an extra 
    layer of error checking: when the action is triggered, an exception will be thrown if the `properties` argument to
    `onEvent` does not contain the given properties with the given types.

## Caveats

Given that the format of the properties list for any given event is likely to be very consistent, using a Map of
properties might be unnecessarily cumbersome. It may be more typesafe and useful to have a case class or POJO for each
event type, containing the properties associated with that event.