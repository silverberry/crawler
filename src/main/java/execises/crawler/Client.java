package execises.crawler;

import akka.actor.*;
import java.net.URL;
import java.util.Set;
import akka.pattern.Patterns;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;

// Hint: Use http://www.example.com for testing.

public class Client {
    // This is a simple example for testing the crawler functionality from the command line.
    // It fires off a single request to the receptionist, specifying a URL to crawl, the
    // depth to which the links should be followed, and the timeout in milliseconds.
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        // Extract the command line arguments and print them out.
        String url = args[0]; // the first argument is the URL to crawl.
        int depth = Integer.parseInt(args[1]); // the second argument is the depth to which to follow the links.
        long millis = Long.parseLong(args[2]); // the third argument is the timeout in milliseconds.
        Duration timeout = Duration.create(millis, TimeUnit.MILLISECONDS);
        System.out.println("Crawling " + url + " to depth " + depth + " (timeout: " + millis + " milliseconds).");

        // Create an actor system and single receptionist actor.
        // Although the receptionist is designed to handle multiple request,
        // here we only fire a single request at the receptionist, after which it will be shut down.
        ActorSystem system = ActorSystem.create();

        // Note that system.actorOf returns an instance of ActorRef, not an instance of Receptionist.
        // This is the way that actors work. One cannot execute methods on the actors directly,
        // because no actual instance of the actor is accessible to the caller at any time.
        // Instead, actors can be accessed only by sending messages to them, and the
        // ActorRef instance serves as an "address" for such messages. Thus, for example,
        // one can send a message to an actor by invoking the ActorRef.tell method (the "fire-and-forget" mode)
        // or by calling Patterns.ask, in which case a future is returned, which becomes completed
        // when the actor sends back a message of its own; the message becomes the future's result.
        ActorRef receptionist = system.actorOf(Props.create(Receptionist.class));

        // Here, we send a Receptionist.Request message to the receptionist actor.
        // A future is returned, which is completed when the receptionist sends us
        // a message back, which will become the completed future's result.
        Future<Object> results = Patterns.ask(receptionist, new Receptionist.Request(new URL(url), depth, timeout), millis);

        // Wait for the future to be completed (within the specified timeout) and obtain the result.
        // Since we know that the receptionist is bound to send a Set<URL> back as its message, it's safe to cast.
        // Note that, in general, it would be better to compose the future with a Future.map, but since this is
        // a Scala future, it is more cumbersome to handle in Java; besides, we are in a one-shot main method,
        // so blocking on Await.result is acceptable.
        Set<URL> urls = (Set<URL>) Await.result(results, timeout);

        // Print out the returned URLs.
        for (URL next : urls) {
            System.out.println(next);
        }

        System.out.println("Done.");
        // Kill the receptionist actor by sending it a PoisonPill message, then shut the actor system down.
        // (Strictly speaking, system.terminate will take care of shutting down all actors, but it's good form.)
        receptionist.tell(PoisonPill.getInstance(), ActorRef.noSender());
        system.terminate();
    }
}
