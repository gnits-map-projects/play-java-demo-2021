package controllers;

import play.Logger;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class AsyncController extends Controller {
    private final static Logger.ALogger LOGGER = Logger.of(AsyncController.class);

    private HttpExecutionContext executionContext;

    @Inject
    public AsyncController(HttpExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    public CompletionStage<Result> answer() {
        LOGGER.info("I am calculating answer to Life, Universe and Everything....");
        CompletionStage<Result> result = calculateResponse()
                .thenApplyAsync(answer -> {
                    return ok("answer was " + answer);
                }, executionContext.current());
        return result;
    }

    private CompletionStage<String> calculateResponse() {
        return CompletableFuture.completedFuture("42");
    }

    public CompletionStage<Result> action() {
        LOGGER.info("Before intPromise");
        CompletionStage<Integer> intPromise = CompletableFuture.supplyAsync(() -> {
            LOGGER.info("inside intPromise");
            return 1000;
        });
        LOGGER.info("After intPromise");

        LOGGER.info("Before stringPromise");
        CompletionStage<Result> stringPromise = intPromise.thenApply(i -> {
            LOGGER.info("inside stringPromise");
            return ok("Got " + i);
        });
        LOGGER.info("After intPromise");
        return stringPromise;
    }
}
