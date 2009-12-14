/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ccsds.moims.mo.mal.impl.broker;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.broker.MALBrokerBinding;
import org.ccsds.moims.mo.mal.impl.util.MALClose;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 *
 * @author cooper_sf
 */
public class MALBrokerBindingTransportWrapper extends MALClose implements MALBrokerBinding
{
  private final MALBrokerBinding transportDelegate;

  public MALBrokerBindingTransportWrapper(MALClose parent, MALBrokerBinding transportDelegate)
  {
    super(parent);

    this.transportDelegate = transportDelegate;
  }

  @Override
  public void activate() throws MALException
  {
    transportDelegate.activate();
  }

  @Override
  public void close() throws MALException
  {
    transportDelegate.close();
  }

  @Override
  public Blob getAuthenticationId()
  {
    return transportDelegate.getAuthenticationId();
  }

  @Override
  public URI getURI()
  {
    return transportDelegate.getURI();
  }

  @Override
  public boolean isMALLevelBroker()
  {
    return transportDelegate.isMALLevelBroker();
  }
}