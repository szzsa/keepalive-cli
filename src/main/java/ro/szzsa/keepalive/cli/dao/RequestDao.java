package ro.szzsa.keepalive.cli.dao;

import java.util.List;

import ro.szzsa.keepalive.model.Request;

/**
 *
 */
public interface RequestDao {

    List<Request> getAll();
}
