package eu.flatwhite.zapper;

import java.io.File;

import org.junit.Test;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Realm;
import com.ning.http.client.Realm.AuthScheme;

import eu.flatwhite.zapper.client.ahc.AhcClient;
import eu.flatwhite.zapper.fs.DirectoryIOSource;
import eu.flatwhite.zapper.internal.ParametersImpl;

public class ClientTest
{
    @Test
    public void upload()
        throws Exception
    {
        final Parameters parameters = new ParametersImpl();

        final Realm realm =
            new Realm.RealmBuilder().setPrincipal( "admin" ).setPassword( "admin123" ).setUsePreemptiveAuth( true ).setScheme(
                AuthScheme.BASIC ).build();
        final AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder().setRealm( realm ).build();
        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient( config );
        final Client client =
            new AhcClient( parameters, "http://localhost:8081/nexus/content/repositories/thirdparty/", asyncHttpClient );
        final IOSourceListable directory =
            new DirectoryIOSource( new File( "/Users/cstamas/Worx/flatwhite/zapper/target/classes" ) );

        try
        {
            client.upload( directory );
        }
        finally
        {
            client.close();
        }
    }
}
