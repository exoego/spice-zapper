/*
 * Copyright (c) 2007-2014 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.spice.zapper.fs;

import java.io.File;
import java.io.IOException;

import org.sonatype.spice.zapper.hash.Hash;
import org.sonatype.spice.zapper.hash.HashAlgorithm;
import org.sonatype.spice.zapper.hash.HashUtils;
import org.sonatype.spice.zapper.hash.Sha1HashAlgorithm;
import org.sonatype.spice.zapper.internal.Check;

public class MavenHashStrategy
    implements HashStrategy
{
  private final HashAlgorithm hashAlgorithm;

  public MavenHashStrategy(final HashAlgorithm hashAlgorithm) {
    this.hashAlgorithm = Check.notNull(hashAlgorithm, HashAlgorithm.class);
    if (!Sha1HashAlgorithm.ID.equals(hashAlgorithm.getIdentifier())) {
      throw new IllegalArgumentException("Maven repository layout supports SHA1 hashes only!");
    }
  }

  public HashAlgorithm getHashAlgorithm() {
    return hashAlgorithm;
  }

  public Hash getHashFor(final File file)
      throws IOException
  {
    return HashUtils.getDigest(Check.notNull(hashAlgorithm, HashAlgorithm.class),
        Check.notNull(file, File.class));
  }
}
