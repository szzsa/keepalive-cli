package ro.szzsa.keepalive.cli.dao.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import ro.szzsa.keepalive.cli.dao.RequestDao;
import ro.szzsa.keepalive.model.Request;

/**
 *
 */
public class RequestDaoCsvImpl implements RequestDao {

    private final String file;

    @Override
    public List<Request> getAll() {
        List<Request> requests = new ArrayList<>();
        for (String line : readLines()) {
            Request request = new Request();
            request.setName(line.split(",")[0]);
            request.setUrl(line.split(",")[1]);
            requests.add(request);
        }
        return requests;
    }

    public RequestDaoCsvImpl(String file) {
        this.file = file;
    }

    private List<String> readLines() {
        List<String> lines = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(file))) {
            stream.forEach(lines::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
