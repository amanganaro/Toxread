package insilico.readacross.controller;

import insilico.readacross.exception.RestOperationException;

@FunctionalInterface
public interface RestControllerCommand {
    Object get() throws RestOperationException;
}
