/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apppsi;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
//import evoparsons.rmishared.BrokerInterfaceForEAs;
import org.problets.lib.comm.rmi.BrokerInterfaceForProblet;
import org.problets.lib.comm.rmi.ParsonsEvaluation;
import org.problets.lib.comm.rmi.ParsonsPuzzle;
import org.problets.lib.comm.rmi.ParsonsRelayClientInterface;
import org.problets.lib.comm.rmi.ParsonsRelayServerInterface;

public class ParsonsRelayClient implements ParsonsRelayClientInterface
{
	private static final long serialVersionUID = 1L;
	private Object client = null;
	private Object value = null; 
	//private Method callback;
	private Object parent;
	
	public ParsonsRelayClient() throws RemoteException, NotBoundException, MalformedURLException
	{ 
		super();
		//callback = null;
		parent = null;
	}
		
	/*public ParsonsRelayClient(Method m, Object p) throws RemoteException
	{ 
		super();
		callback = m;
		parent = p;
	}*/
	
        @Override
	public long getStudentID(String login, String serverHostName, int serverPort) throws RemoteException
    {
    	log(String.format("Calling remote method %s at %s","getStudentID", serverHostName));
		
		long retValue = 0;
		
            try {
                initClient(serverHostName, serverPort, "ParsonsBroker", 10);
            } catch (NotBoundException ex) {
                Logger.getLogger(ParsonsRelayClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                Logger.getLogger(ParsonsRelayClient.class.getName()).log(Level.SEVERE, null, ex);
            }
		BrokerInterfaceForProblet thisClient = (BrokerInterfaceForProblet)client; 
		
		
		return (long)retValue;
    }
  
	
        @Override
    public ParsonsPuzzle getParsonsPuzzle(long userID, String serverHostName, int serverPort) throws RemoteException
	{
    	log(String.format("Calling remote method %s at %s/%s","getParsonsPuzzle", serverHostName, "ParsonsBroker"));
		
		Object retValue = null;
		
		while (retValue == null)
		{
			
                try {
                    initClient(serverHostName, serverPort, "ParsonsBroker", 10);
                } catch (NotBoundException ex) {
                    Logger.getLogger(ParsonsRelayClient.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(ParsonsRelayClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                            BrokerInterfaceForProblet thisClient = (BrokerInterfaceForProblet)client;
                            
                            
                                //thisClient.registerForCallback(this);
                                retValue = thisClient.getParsonsPuzzle(userID);
                            
                            
                        
			
		}

		return (ParsonsPuzzle)retValue;
	}
    
    private void waitOrExit()
    {
		try
		{
			Thread.sleep(5000);
		}
		catch (InterruptedException e)
		{
			System.exit(0);
		}
    }

        @Override
	public ParsonsPuzzle getParsonsPuzzle(long userID,String serverHostName, int serverPort, String proxyHostName, int proxyPort) throws RemoteException
	{
		log(String.format("Calling remote method %s at %s via proxy %s", "getParsonsPuzzle", serverHostName, proxyHostName));

		Object retValue = null;

		while (retValue == null)
		{
                    try {
                        initClient(proxyHostName, proxyPort, "ParsonsRelayServer", 10);
                    } catch (NotBoundException ex) {
                        Logger.getLogger(ParsonsRelayClient.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(ParsonsRelayClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
			ParsonsRelayServerInterface thisClient = (ParsonsRelayServerInterface) client;			

			try
			{
				//thisClient.registerForCallback(this);
				retValue = thisClient.getParsonsPuzzle(userID, serverHostName, serverPort);
			}
			catch (RemoteException e)
			{
				log("Remote Exception in ParsonsRelayClient getParsonsPuzzle w/proxy");
			}
			catch (Exception e)
			{
				log("Unknown error in ParsonsRelayClient getParsonsPuzzle w/proxy");
				//e.printStackTrace();
			}
		}

		return (ParsonsPuzzle) retValue;
	}
    
        @Override
    public void setParsonsEvaluation(ParsonsEvaluation data, String serverHostName, int serverPort) throws RemoteException
    {
    	log(String.format("Calling remote method %s at %s/%s","setParsonsEvaluation", serverHostName, "ParsonsBroker"));
		
    	boolean success = false;
		
		while (!success)
		{
			try
                        {
                            initClient(serverHostName, serverPort, "ParsonsBroker", 10);
                            BrokerInterfaceForProblet thisClient = (BrokerInterfaceForProblet)client;                           
                              //thisClient.registerForCallback(this);
                                thisClient.setParsonsEvaluation(data);
                                success = true;
                        }
			catch (NotBoundException ex)
			{
				Logger.getLogger(ParsonsRelayClient.class.getName()).log(Level.SEVERE, null, ex);
			} catch (MalformedURLException ex) {
                    Logger.getLogger(ParsonsRelayClient.class.getName()).log(Level.SEVERE, null, ex);
                }
		}
    }
    
    
//not to be used as relay is not in use
    
        @Override
    public void setParsonsEvaluation(ParsonsEvaluation data, String serverHostName, int serverPort, String proxyHostName, int proxyPort) throws RemoteException
    {
    	log(String.format("Calling remote method %s at %s via proxy %s","setParsonsEvaluation", serverHostName, proxyHostName));
		
    	boolean success = false;
		
		while(!success)
		{
                try {
                    initClient(proxyHostName, proxyPort, "ParsonsRelayServer", 10);
                    ParsonsRelayServerInterface thisClient = (ParsonsRelayServerInterface)client;
                    
                    
                    //thisClient.registerForCallback(this);
                    thisClient.setParsonsEvaluation(data, serverHostName, serverPort);
                    success = true;
                } catch (NotBoundException ex) {
                    Logger.getLogger(ParsonsRelayClient.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(ParsonsRelayClient.class.getName()).log(Level.SEVERE, null, ex);
                }
			
		}
    }  
	
	private void initClient(String hostName, int portNum, String serviceName, int maxConnectAttempts) throws MalformedURLException, RemoteException, NotBoundException
	{
		int connectAttempts = 0;
		client = null;
		
		log(String.format("initClient called with %s/%s", hostName, serviceName));
		
		while(client == null && connectAttempts < maxConnectAttempts)
                {               
				Registry r = LocateRegistry.getRegistry(hostName,portNum);
				connectAttempts++;
				client = (Object)r.lookup(serviceName);
				log(String.format("Found serviceName: %s", serviceName));
		}
	}
	
	private void log(String msg)
	{
		System.out.print("[ParsonsRelayClient]\t");
		System.out.println(msg);
	}
        
        //not to be used as relay is not in use
    @Override
    public long getStudentID(String string, String string1, int i, String string2, int i1) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
