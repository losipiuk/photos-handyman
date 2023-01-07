package net.losipiuk.photoshandyman;

import com.google.common.collect.ImmutableList;
import com.google.photos.library.v1.PhotosLibraryClient;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static net.losipiuk.photoshandyman.PhotosScopes.FULL_ACCESS;

public class DuplicatesCleaner
    implements Operation
{
    private final PhotosLibraryClient client;
    private final boolean dryRun;

    private DuplicatesCleaner(PhotosLibraryClient client, boolean dryRun)
    {
        this.client = requireNonNull(client, "client is null");
        this.dryRun = dryRun;
    }

    @Override
    public void run()
    {
        System.out.println("cleaning duplicates");
    }

    public static class Factory implements OperationFactory
    {
        @Override
        public String getName()
        {
            return "remove-duplicates";
        }

        @Override
        public Operation create(List<String> args)
        {
            PhotosLibraryClient client = PhotosLibraryClientFactory.createClient(ImmutableList.of(FULL_ACCESS));
            return new DuplicatesCleaner(client, true);
        }
    }
}
