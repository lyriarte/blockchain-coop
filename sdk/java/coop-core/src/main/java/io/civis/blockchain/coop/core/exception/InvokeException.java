package io.civis.blockchain.coop.core.exception;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import java.util.List;

public class InvokeException extends Exception {

    public InvokeException(List<String> errors) {
        super(Joiner.on(";").join(Sets.newHashSet(errors)));
    }

    public InvokeException(Exception e) {
        super(e);
    }

}
