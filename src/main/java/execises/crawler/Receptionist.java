package execises.crawler;

import akka.actor.*;
import java.net.URL;
import scala.concurrent.duration.Duration;

// The receptionist actor receives requests to crawl URLs to specific depth with a given timeout.
// It works by spinning off a controller actor per request. The sender of the request expects a
// response message from the receptionist, containing a Set<URL>. However, this can be accomplished
// by the controller sending the response message on the receptionist's behalf (as the second argument
// in the ActorRef.tell method call), since the controller knows the sender's identity.
public class Receptionist extends UntypedActor {
    public void onReceive(Object message) {
        // The receptionist expects only one kind of messages: Request.
        if (message instanceof Request) {
            Request request = (Request) message;

            // Spin off the controller actor, passing on the sender's ActorRef as a constructor argument,
            // together with the timeout. Note that the sender doesn't have to be an actor to have an ActorRef.
            // In our implementation, the sender is actually the caller of Patterns.ask in the Client class.
            ActorRef controller = context().actorOf(Props.create(Controller.class, sender(), request.timeout));

            // Send the controller actor a message to start working on the task.
            // This is an example of how one can send messages to an actor.
            // The first argument of ActorRef.tell is the message itself.
            // The second argument is the ActorRef instance of the sender (in this case,
            // the receptionist actor itself, but one can send messages on another's behalf).
            controller.tell(new Controller.Task(request.url, request.depth), self());
        }
    }

    // Instances of this class serve as request messages that the receptionist expects to receive.
    static class Request {
        final URL url;
        final int depth;
        final Duration timeout;

        Request(URL url, int depth, Duration timeout) {
            this.url = url;
            this.depth = depth;
            this.timeout = timeout;
        }
    }
}
