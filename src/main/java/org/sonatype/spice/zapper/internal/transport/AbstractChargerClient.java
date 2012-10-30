package org.sonatype.spice.zapper.internal.transport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.sonatype.sisu.charger.ChargeFuture;
import org.sonatype.sisu.charger.Charger;
import org.sonatype.sisu.charger.ExceptionHandler;
import org.sonatype.sisu.charger.internal.AllArrivedChargeStrategy;
import org.sonatype.sisu.charger.internal.DefaultCharger;
import org.sonatype.spice.zapper.Parameters;
import org.sonatype.spice.zapper.internal.PayloadSupplier;
import org.sonatype.spice.zapper.internal.Protocol;
import org.sonatype.spice.zapper.internal.Transfer;

/**
 * Client using "charger" that handles multi-thread invocations. Obviously, this is not needed if the actual underlying
 * transport is asynchronous for example.
 * 
 * @author cstamas
 */
public abstract class AbstractChargerClient<T extends AbstractChargerTrack>
    extends AbstractClient<T>
{
    private final Charger charger;

    private final SimpleCallableExecutor executor;

    public AbstractChargerClient( final Parameters parameters, final String remoteUrl )
    {
        super( parameters, remoteUrl );
        this.charger = new DefaultCharger();
        this.executor = new SimpleCallableExecutor( parameters.getMaximumTrackCount() );
    }

    public void close()
    {
        executor.shutdown();
    }

    @Override
    protected void doUpload( final Transfer transfer, final Protocol protocol, final int trackCount )
        throws IOException
    {
        final PayloadSupplier payloadSupplier = transfer.getPayloadSupplier();
        final List<Callable<State>> tracks = new ArrayList<Callable<State>>( trackCount );
        for ( int i = 0; i < trackCount; i++ )
        {
            tracks.add( createCallable( transfer.getNextTrackIdentifier(), transfer, protocol, payloadSupplier ) );
        }

        final ChargeFuture<State> chargeFuture =
            charger.submit( tracks, getExceptionHandler(), new AllArrivedChargeStrategy<State>(), executor );

        try
        {
            chargeFuture.getResult();
        }
        catch ( IOException e )
        {
            if ( e.getCause() == null )
            {
                final IOException ee = new IOException( "IO failure" );
                ee.initCause( e );
                throw ee;
            }
            throw e;
        }
        catch ( Exception e )
        {
            final IOException ee = new IOException( "IO failure" );
            ee.initCause( e );
            throw ee;
        }
    }

    // ==

    protected abstract Callable<State> createCallable( final TrackIdentifier trackIdentifier, final Transfer transfer,
                                                       final Protocol protocol, final PayloadSupplier payloadSupplier );

    protected static ExceptionHandler NON_HANDLING_EXCEPTION_HANDLER = new ExceptionHandler()
    {
        public boolean handle( Exception ex )
        {
            return false;
        }
    };

    protected ExceptionHandler getExceptionHandler()
    {
        return NON_HANDLING_EXCEPTION_HANDLER;
    }
}
