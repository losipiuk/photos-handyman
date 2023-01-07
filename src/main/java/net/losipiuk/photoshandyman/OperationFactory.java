package net.losipiuk.photoshandyman;

import java.util.List;

public interface OperationFactory
{
    String getName();
    Operation create(List<String> args);
}
