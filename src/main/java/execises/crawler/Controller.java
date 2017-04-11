package execises.crawler;

import akka.Done;
import java.util.*;
import akka.actor.*;
import java.net.URL;
import scala.concurrent.duration.Duration;

// A controller actor is created by the receptionist to serve a single crawl request.
// It starts by spinning off a worker actor for the original URL, then waits for
// messages from the worker actors as they discover other URLs to follow.
// For each new discovered URL (one not encountered before), the controller
// spins a new worker actor, provided that the maximal depth of crawling is respected.
// When a worker actor is done, it sends a Done message back to the controller.
// The controller runs until no more active worker actors remain, at which point
// it must send back the collected URLs as an instance of Set<URL>. Since the
// controller is given an ActorRef instance of the client that expects the results,
// the controller can (and should) send the results back to the client (NOT to the
// receptionist), but on the receptionist's behalf.
public class Controller extends UntypedActor {
    private final ActorRef client;
    private Set<URL> cache = new HashSet<>();
    private Set<ActorRef> workers = new HashSet<>();

    // The controller actor remembers the client that
    // originally sent the crawl request to the receptionist.
    Controller(ActorRef client, Duration timeout) {
        this.client = client;
        context().setReceiveTimeout(timeout);
    }

    public void onReceive(Object message) throws Exception {
        if (message instanceof Task) {
            // The message is either the original URL with the maximal depth
            // or one of the discovered URLs with a decremented depth.
            // Hint: Spin off a worker actor and send it a Fetch message
            // for it to start working.
            Task task = (Task) message;

            // TODO

        } else if (message instanceof Done) {
            // The sender of this message is a worker actor that is done with its task.
            // Hint: The ActorRef of the sender can be obtained by calling sender().

            // TODO

        } else if (message instanceof ReceiveTimeout) {
            // Clean up and report what was already found. For this exercise, do not attempt recovery.
            // Hint: a worker actor can be stopped by sending it a Done message.

            // TODO

        }
    }

    // Spin off a new worker actor for the given task and add it to the set of active workers.
    private ActorRef assignWorker(Task task) {
        ActorRef worker = context().actorOf(Props.create(Worker.class, task.url, task.depth));
        workers.add(worker);
        return worker;
    }

    // Send the cache back to the client on the receptionist's behalf.
    // Note that the receptionist actor is the controller actor's parent.
    // Hint: a reference to it can be obtained by calling context().parent().
    private void reportAndStop() {

        // TODO

        // Stop yourself.
        context().stop(self());
    }

    // Instances of this class serve as task messages that the controller
    // actor receives for the original and any discovered URL.
    static class Task {
        final URL url;
        final int depth;

        Task(URL url, int depth) {
            this.url = url;
            this.depth = depth;
        }
    }
}
