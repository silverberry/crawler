package execises.crawler;

import java.io.*;
import akka.Done;
import java.net.URL;
import org.asynchttpclient.*;
import akka.actor.UntypedActor;
import java.net.MalformedURLException;
import javax.swing.text.html.parser.ParserDelegator;

// The worker actor processes a single URL, detecting any links that it may have in its HTML body.
// It initiates an HTTP GET using an asynchronous HTTP client, when it receives a Fetch message.
// Since this is done asynchronously, it must notify itself when the operation is done by sending
// itself the HTTP Response as a message. It then parses the HTML body, sending back to its
// parent actor (the controller) any discovered URLs in the form of Controller.Task messages.
public class Worker extends UntypedActor {
    private final AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
    private final URL url;
    private final int depth;
    private final URL domain;

    Worker(URL url, int depth) throws MalformedURLException {
        this.url = url;
        this.depth = depth;
        // Hint: Determining the URL's domain may be helpful if there are any relative URLs within.
        this.domain = // TODO
    }

    public void onReceive(Object message) throws Exception {
        if (message instanceof Fetch) {
            // When a Fetch message is received, the HTTP client retrieves the HTML body for the URL.
            asyncHttpClient.prepareGet(url.toString()).execute(new AsyncCompletionHandler<Response>() {
                @Override
                public Response onCompleted(Response response) throws Exception {
                    // Hint: Send the response to self for HTML parsing.

                    // TODO: Write what must happen when the HTTP operation is complete.

                }

                @Override
                public void onThrowable(Throwable t) {
                    // Something wrong happened.
                    stop();
                }
            });
        } else if (message instanceof Response) {
            // When a Response message is received, parse it as HTML, looking for embedded URLs.
            // Send the properly formed Controller.Task messages back to the controller for any discovered URLs.
            // Hint: The controller actor is the worker's parent, so call context().parent().
            String body = ((Response) message).getResponseBody();
            BufferedReader reader = new BufferedReader(new StringReader(body));
            new ParserDelegator().parse(reader, new Parser(link -> {

                // TODO: Write the callback.

            }), true);

            // Hint: Decide whether the worker actor should stop itself at this point
            // or be stopped by the controller by sending it a Done message (see below).

            // TODO

        } else if (message instanceof Done) {
            stop();
        }
    }

    // When the worker actor is done working, this must be called.
    private void stop() {
        try {
            // Release the network resources.
            asyncHttpClient.close();
        } catch (IOException e) {
            // Ignore the exception, for the purposes of this exercise.
        } finally {
            // Send the controller actor a Done message.
            context().parent().tell(Done.getInstance(), self());
            // Stop yourself.
            context().stop(self());
        }
    }

    // The only instance of this class is a message
    // that must be sent to start a worker actor working.
    static class Fetch {
        static final Fetch FETCH = new Fetch();
        private Fetch() {}
    }
}
