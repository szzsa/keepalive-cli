package ro.szzsa.keepalive.cli;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ro.szzsa.keepalive.KeepAliveTask;
import ro.szzsa.keepalive.RequestProvider;
import ro.szzsa.keepalive.ResponseInterpreter;
import ro.szzsa.keepalive.cli.dao.RequestDao;
import ro.szzsa.keepalive.cli.dao.impl.RequestDaoCsvImpl;
import ro.szzsa.keepalive.model.Request;

/**
 *
 */
public class CommandLineInterface {

    private static final int DEFAULT_INITIAL_DELAY = 10;

    private static final int DEFAULT_DELAY = 600;

    private static final String DEFAULT_FILE_NAME = "config.csv";

    private int initialDelay = DEFAULT_INITIAL_DELAY;

    private int delay = DEFAULT_DELAY;

    private String file;

    public void run() {
        RequestDao dao = new RequestDaoCsvImpl(file);
        List<Request> requests = dao.getAll();
        if (requests == null || requests.isEmpty()) {
            return;
        }
        ExecutorService executorService = Executors.newFixedThreadPool(requests.size());
        for (Request request : requests) {
            executorService.execute(() -> {
                RequestProvider requestProvider = () -> request;
                ResponseInterpreter responseInterpreter = response -> System.out.println(
                    new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(System.currentTimeMillis()) + " - " +
                    request.getName() + ": " +
                    (response.isSuccess() ? "Success" : "Fail (" + response.getStatusCode() + ")"));

                new KeepAliveTask(initialDelay, delay, requestProvider, responseInterpreter).start();
            });
        }
        executorService.shutdown();
    }

    public static void main(String[] args) throws URISyntaxException {
        getCommandLineInterface(args).run();
    }

    private static CommandLineInterface getCommandLineInterface(String[] args) throws URISyntaxException {
        CommandLineInterface cli = new CommandLineInterface();
        cli.setFile(Paths.get(System.getProperty("user.dir"),
                              "..",
                              "config",
                              DEFAULT_FILE_NAME).toString());
        if (args == null) {
            return cli;
        }
        for (String arg : args) {
            if (arg.contains("=")) {
                String key = arg.split("=")[0];
                String value = arg.split("=")[1];
                switch (key) {
                    case "-Dfile":
                        cli.setFile(value);
                        break;
                    case "-DinitialDelay":
                        cli.setInitialDelay(Integer.parseInt(value));
                        break;
                    case "-Ddelay":
                        cli.setDelay(Integer.parseInt(value));
                        break;
                }
            }
        }
        return cli;
    }

    public void setInitialDelay(int initialDelay) {
        this.initialDelay = initialDelay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
