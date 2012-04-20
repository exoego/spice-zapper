package eu.flatwhite.zapper.internal;

import java.util.HashMap;
import java.util.Map;

import eu.flatwhite.zapper.hash.Hash;
import eu.flatwhite.zapper.hash.HashAlgorithmIdentifier;
import eu.flatwhite.zapper.hash.Hashed;

public abstract class AbstractHashedRange
    extends AbstractRange
    implements Hashed
{
    private final Map<HashAlgorithmIdentifier, Hash> hashMap;

    protected AbstractHashedRange( final long offset, final long length, final Hash... hashes )
    {
        super( offset, length );
        this.hashMap = new HashMap<HashAlgorithmIdentifier, Hash>( hashes.length );
        for ( Hash hash : hashes )
        {
            hashMap.put( hash.getHashAlgorithmIdentifier(), hash );
        }
    }

    @Override
    public Hash getHash( final HashAlgorithmIdentifier hashAlgorithmIdentifier )
    {
        return hashMap.get( hashAlgorithmIdentifier );
    }

    // ==

    @Override
    public String toString()
    {
        return super.toString() + "(hashMap=" + hashMap + ")";
    }
}
