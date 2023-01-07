package net.losipiuk.photoshandyman;

import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public final class PhotosHandyman
{
    private PhotosHandyman() {}

    public static void main(String[] args)
    {
        List<OperationFactory> operatorFactories = ImmutableList.of(
                new DuplicatesCleaner.Factory());

        if (args.length == 0) {
            throw new IllegalArgumentException("must specify operation");
        }
        String operationName = args[0];

        Operation operation = null;
        for (OperationFactory operatorFactory : operatorFactories) {
            if (operatorFactory.getName().equals(operationName)) {
                operation = operatorFactory.create(Arrays.asList(args));
                break;
            }
        }

        if (operation == null) {
            throw new IllegalArgumentException("unknown operation '%s'".formatted(operationName));
        }
        operation.run();
    }
}
