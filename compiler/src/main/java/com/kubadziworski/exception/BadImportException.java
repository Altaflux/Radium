package com.kubadziworski.exception;


import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class BadImportException extends RuntimeException {

    public BadImportException(List<String> packageImport) {
        super("Invalid import for: \n" + StringUtils.join(packageImport, "\n"));
    }
}
