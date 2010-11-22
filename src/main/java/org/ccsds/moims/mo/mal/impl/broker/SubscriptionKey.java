/* ----------------------------------------------------------------------------
 * (C) 2010      European Space Agency
 *               European Space Operations Centre
 *               Darmstadt Germany
 * ----------------------------------------------------------------------------
 * System       : CCSDS MO MAL Implementation
 * Author       : cooper_sf
 *
 * ----------------------------------------------------------------------------
 */
package org.ccsds.moims.mo.mal.impl.broker;

import org.ccsds.moims.mo.mal.impl.util.StructureHelper;
import org.ccsds.moims.mo.mal.structures.DomainIdentifier;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityRequest;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.MessageHeader;

/**
 * Simple class that represents a MAL subscription.
 */
public final class SubscriptionKey implements Comparable
{
  /**
   * Match all constant.
   */
  public static final String ALL_ID = "*";
  private static final int HASH_MAGIC_NUMBER = 47;
  private final String domain;
  private final boolean andSubDomains;
  private final String area;
  private final String service;
  private final String operation;
  private final String key1;
  private final String key2;
  private final String key3;
  private final String key4;

  /**
   * Constructor.
   * @param lst Entity key.
   */
  public SubscriptionKey(MessageHeader hdr, EntityRequest rqst, EntityKey lst)
  {
    super();

    String tmpDomain = "";
    boolean tmpAndSubDomains = false;

    DomainIdentifier mdomain = hdr.getDomain();
    DomainIdentifier sdomain = rqst.getSubDomain();
    if ((null != mdomain) || (null != sdomain))
    {
      StringBuilder buf = new StringBuilder();
      if ((null != mdomain) && (0 < mdomain.size()))
      {
        buf.append(StructureHelper.domainToString(mdomain));
      }

      if ((null != sdomain) && (0 < sdomain.size()))
      {
        int i = 0;
        int e = sdomain.size();
        while (i < e)
        {
          String id = String.valueOf((Identifier) sdomain.get(i));
          if (!ALL_ID.equals(id))
          {
            if (0 < buf.length())
            {
              buf.append('.');
            }

            buf.append(id);
          }
          else
          {
            tmpAndSubDomains = true;
          }

          ++i;
        }
      }

      tmpDomain = buf.toString();
    }

    this.domain = tmpDomain;
    this.andSubDomains = tmpAndSubDomains;
    this.area = getIdValueOrWildcard(hdr.getArea(), rqst.isAllAreas());
    this.service = getIdValueOrWildcard(hdr.getService(), rqst.isAllServices());
    this.operation = getIdValueOrWildcard(hdr.getOperation(), rqst.isAllOperations());
    this.key1 = getIdValue(lst.getFirstSubKey());
    this.key2 = getIdValue(lst.getSecondSubKey());
    this.key3 = getIdValue(lst.getThirdSubKey());
    this.key4 = getIdValue(lst.getFourthSubKey());
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    final SubscriptionKey other = (SubscriptionKey) obj;
    if ((this.key1 == null) ? (other.key1 != null) : !this.key1.equals(other.key1))
    {
      return false;
    }
    if ((this.key2 == null) ? (other.key2 != null) : !this.key2.equals(other.key2))
    {
      return false;
    }
    if ((this.key3 == null) ? (other.key3 != null) : !this.key3.equals(other.key3))
    {
      return false;
    }
    if ((this.key4 == null) ? (other.key4 != null) : !this.key4.equals(other.key4))
    {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = HASH_MAGIC_NUMBER * hash + (this.key1 != null ? this.key1.hashCode() : 0);
    hash = HASH_MAGIC_NUMBER * hash + (this.key2 != null ? this.key2.hashCode() : 0);
    hash = HASH_MAGIC_NUMBER * hash + (this.key3 != null ? this.key3.hashCode() : 0);
    hash = HASH_MAGIC_NUMBER * hash + (this.key4 != null ? this.key4.hashCode() : 0);
    return hash;
  }

  @Override
  public int compareTo(Object o)
  {
    SubscriptionKey rhs = (SubscriptionKey) o;
    int rv = compareSubkey(this.key1, rhs.key1);
    if (0 == rv)
    {
      rv = compareSubkey(this.key2, rhs.key2);
      if (0 == rv)
      {
        rv = compareSubkey(this.key3, rhs.key3);
        if (0 == rv)
        {
          rv = compareSubkey(this.key4, rhs.key4);
        }
      }
    }

    return rv;
  }

  /**
   * Returns true if this key matches supplied argument taking into account wildcards.
   * @param rhs Key to match against.
   * @return True if matches.
   */
  public boolean matches(UpdateKey rhs)
  {
    boolean matched = rhs.domain.startsWith(this.domain);

    if (matched)
    {
      if ((this.domain.length() < rhs.domain.length()))
      {
        matched = this.andSubDomains;
      }

      if (matched)
      {
        matched = matchedSubkey(area, rhs.area);
        if (matched)
        {
          matched = matchedSubkey(service, rhs.service);
          if (matched)
          {
            matched = matchedSubkey(operation, rhs.operation);
            if (matched)
            {
              matched = matchedSubkey(key1, rhs.key1);
              if (matched)
              {
                matched = matchedSubkey(key2, rhs.key2);
                if (matched)
                {
                  matched = matchedSubkey(key3, rhs.key3);
                  if (matched)
                  {
                    matched = matchedSubkey(key4, rhs.key4);
                  }
                }
              }
            }
          }
        }
      }
    }

    return matched;
  }

  private int compareSubkey(String myKeyPart, String theirKeyPart)
  {
    if ((null == myKeyPart) || (null == theirKeyPart))
    {
      if ((null != myKeyPart) || (null != theirKeyPart))
      {
        if (null == myKeyPart)
        {
          return -1;
        }

        return 1;
      }
    }
    else
    {
      if (!myKeyPart.equals(theirKeyPart))
      {
        return myKeyPart.compareTo(theirKeyPart);
      }
    }

    return 0;
  }

  private boolean matchedSubkey(String myKeyPart, String theirKeyPart)
  {
    if (ALL_ID.equals(myKeyPart) || ALL_ID.equals(theirKeyPart))
    {
      return true;
    }

    if ((null == myKeyPart) || (null == theirKeyPart))
    {
      if ((null == myKeyPart) && (null == theirKeyPart))
      {
        return true;
      }

      return false;
    }

    return myKeyPart.equals(theirKeyPart);
  }

  private static String getIdValueOrWildcard(Identifier id, boolean isWildcard)
  {
    if (isWildcard)
    {
      return ALL_ID;
    }

    return getIdValue(id);
  }

  private static String getIdValue(Identifier id)
  {
    if ((null != id) && (null != id.getValue()))
    {
      return id.getValue();
    }

    return null;
  }

  @Override
  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    buf.append('[');
    buf.append(this.domain);
    if(this.andSubDomains)
    {
      buf.append(".*");
    }
    buf.append(':');
    buf.append(this.area);
    buf.append(':');
    buf.append(this.service);
    buf.append(':');
    buf.append(this.operation);
    buf.append(':');
    buf.append(this.key1);
    buf.append('.');
    buf.append(this.key2);
    buf.append('.');
    buf.append(this.key3);
    buf.append('.');
    buf.append(this.key4);
    buf.append(']');
    return buf.toString();
  }
}