# crawler

This is an exercise for coding a simple web crawler using Akka actors.

The purpose is, given an initial URL of an HTML page, retrieve its body and detect any links in it, follow those links to do the same--and so on, until a specified depth.

The skeleton code provides a general framework, with helper code, and hints. A single Receptionist actor is designed to serve an arbitrary number of requests. For each request, the Receptionist spins off one Controller actor, which manages crawling of the given starting page. The Controller spins off Worker actors for the initial URL and for each URL discovered by other Worker actors. A Worker actor sends each discovered URL back to its parent Controller actor.

The following method is of paramount importance in filling in the TODO sections:

```akka.actor.ActorRef.tell(Object message, ActorRef sender)```

Here, the "this" ActorRef instance on which the method is called references the recipient actor. The "sender" argument references the sender. The "message" argument is the message. Note that "self()" can be called on an actor to obtain an ActorRef reference to oneself.

Note that each actor runs in a separate thread (from the thread pool coming with the actor system), so you don't have to worry about synchronizing the operations inside each actor. Exchanging messages between the actors is thread-safe.

See also: [Akka Documentation on ActorRef](http://doc.akka.io/api/akka/2.5/akka/actor/ActorRef.html)
