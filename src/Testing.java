/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author lemark
 */

import java.applet.Applet;
import netscape.javascript.*;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Callback;
import com.sun.jna.ptr.*;
import com.sun.jna.Pointer;
import java.net.URL;
import java.net.URI;
import java.security.*;


public class Testing extends Applet { //******START OF TESTING CLASS*****   
    public interface TheSwitcher extends Library {
   
    // FOR TESTING    
    boolean testSystem();
    int testParameterString(String s);
    String testParameterReturnString(String s);
    String testReturnCStringFromCFString();
    int testSampleErrorCode();
    void testPointerParameter(IntByReference value);
    void test_enumerateInputArrayGetSize(IntByReference out_array_size);
    void test_enumerateInputArray(String[] out_input_name_array,long[] out_input_id_array,String[] out_input_port_type_array);
    public interface TheSwitcherCallback extends Callback{
        public void invoke();
    }
    
    void addJavaCallback(int watch_for_event,TheSwitcherCallback sigfunc);
    void raiseEvent(int raise_event);
    
    // FOR DEPLOYMENT
  
    boolean initSwitcher();
    void exitSwitcher();

    int connectToSwitcher(int switcherNumber,String ipAddress);
    String getSwitcherName(int switch_number);
    
    PointerByReference getSwitcherConnectToFailureCodesArray(IntByReference out_array_size);
  
    void enumerateInputArrayGetSize(IntByReference out_array_size);
    void enumerateInputArray(String[] out_input_name_array,long[] out_input_id_array,String[] out_input_port_type_array);

    void performCut(long source_input_id,long destination_output_id);
    void performCutByIndex(int src_index,int dest_index);
    void performAuxOutputSourceChange(long source_input_id,long aux_output_id);
    void performAuxOutputSourceChangeByIndex(int src_index,int dest_index);

    }   

    
    static TheSwitcher INSTANCE = null;
    
    static boolean connectionStatus = false;
    int[] connectErrorCodeArray;
    int connectionErrorCodeArraySize;
    
    String[] enumerateInputArrayRef;
    String[] enumerateInputPortTypeArrayRef;
    long[] enumerateInputIdArrayRef;
        
    
    @Override
    public void init()
    {
       System.out.println("INITIALIZATION - BEGIN");

       super.init();
       
       System.err.println("Check permission");
       AllPermission myPermission = new AllPermission();
       SecurityManager securityManager = System.getSecurityManager();
        if(securityManager != null)
        {
            try{
                securityManager.checkPermission(myPermission,securityManager.getSecurityContext());
            }catch(SecurityException ex)
            {
                System.out.println("Permission Exception");
            }
            
            System.err.println("Permission - OKAY!");
            myPermission = null;
            securityManager = null;
        }
        
        URL docbase_location = getDocumentBase();
        System.out.println("Document Base Location: " + docbase_location.toString());
        URL codebase_location = getCodeBase();
        System.out.println("Code Base Location: " + codebase_location.toString());
        String location = codebase_location.toString();
        location = location.replace("file:/","");
        location = location.replace("%20"," ");
        location = location + "Debug/lemsibatem.dll";
        System.out.println("Location: " + location);
        System.out.println("Loading library.");
        Testing.INSTANCE = (TheSwitcher) Native.loadLibrary(location, TheSwitcher.class);
                    
        //docbase_location = codebase_location = null;

        if(Testing.INSTANCE.initSwitcher() == true)
        {
            System.out.println("Library loaded!");

            // TESTING ENUMERATIONS
            
            IntByReference input_array_size_ref = new IntByReference();
            Testing.INSTANCE.test_enumerateInputArrayGetSize(input_array_size_ref);
            int input_array_size= input_array_size_ref.getValue();
            input_array_size_ref = null;
            
            enumerateInputArrayRef = new String[input_array_size];
            enumerateInputPortTypeArrayRef = new String[input_array_size];
            enumerateInputIdArrayRef = new long[input_array_size];
            
            Testing.INSTANCE.test_enumerateInputArray(enumerateInputArrayRef,enumerateInputIdArrayRef,enumerateInputPortTypeArrayRef);
        
           for(int x=0;x<input_array_size;x++)
           {
               System.out.println("Enumerate Inputs: " + enumerateInputArrayRef[0] + "ID: " + enumerateInputIdArrayRef[x] + " Name: " + enumerateInputPortTypeArrayRef[x]);
           }
            
            
            // TESTING CALLBACK FUNCTION
            System.out.println("Initializing Callback Function...");
            TheSwitcher.TheSwitcherCallback c = new TheSwitcher.TheSwitcherCallback(){
            public void invoke(){ System.out.println("Callback - SUCCESS!");}};
            System.out.println("Installing Callback Function...");
            Testing.INSTANCE.addJavaCallback(33,c);
            System.out.println("Testing by raising an Event for Callback Function...");
            Testing.INSTANCE.raiseEvent(33);
            
            // TESTING POINTER PARAMETER
            IntByReference empty_value= new IntByReference();
            empty_value.setValue(0);
            System.out.println("Testing Pointer Parameter... Value before: " + empty_value.getValue());
            Testing.INSTANCE.testPointerParameter(empty_value);
            System.out.println("Testing Pointer Parameter... Value after: " + empty_value.getValue());
        
            // INITIALIZATION START: CONNECTION ERROR CODES ARRAY
            System.out.println("Initializing Error and Enumeration codes array...");
            IntByReference error_code_array_size_ref = new IntByReference();
            PointerByReference errorCodeArrayReference = this.INSTANCE.getSwitcherConnectToFailureCodesArray(error_code_array_size_ref);
            Pointer errorCodeArrayPointer = errorCodeArrayReference.getPointer();
            connectionErrorCodeArraySize = error_code_array_size_ref.getValue();
            connectErrorCodeArray = errorCodeArrayPointer.getIntArray(0,connectionErrorCodeArraySize+1);
            error_code_array_size_ref.setPointer(null);
            error_code_array_size_ref = null;
            
            System.out.println("Initializing Error and Enumeration codes array... SUCCESS!");
            System.out.println("Displaying contents of array... (array size: " + connectionErrorCodeArraySize);
              for(int x=0;x<connectionErrorCodeArraySize;x++)
              {
                  System.err.println("Codes " + x + ": " + connectErrorCodeArray[x]);
              }
              
            System.out.println("Displaying contents of array... SUCCESS!");

        }else System.out.println("initSwitcher() unsuccessful!");

        System.out.println("INITIALIZATION - SUCCESS!");
    }
    
    @Override
    public void start()
    {
       System.out.println("Start() called");
    }
    
    @Override
    public void destroy()
    {
        System.err.println("EXITING SWITCHER. CLEANING UP.");
        Testing.INSTANCE.exitSwitcher();
    }
    
    public String javaCheckIsTheSwitcherWorking(int test_index)
    {
            System.err.println("Entering javaCheckIsTheSwitcherWorking. ");
            String result = "";          
            final int testIndex = test_index;    
            result = AccessController.doPrivileged(new PrivilegedAction<String>(){
                 public String run(){
                 
                 String name = Testing.INSTANCE.testReturnCStringFromCFString();
                 System.err.println("Name of Returned String: " + name);
                 
                 System.err.println("Testing final variabl: " + testIndex);
                 int error = Testing.INSTANCE.testSampleErrorCode();
                 System.out.println("Sample Error code (Native): " + error);
                 
                 String sys_result = ""+(Testing.INSTANCE.testSystem());
                 try{
                 int test = 1;
                 test = Testing.INSTANCE.testParameterString(new String("Hello World!"));
                 if(test == 0) System.out.println("Test: testParameterString - SUCCESSFUL!");
                 else System.out.println("Test: testParameterString - FAIL!");
                 }catch(SecurityException ex){System.out.println("Test: testParameterString - EXCEPTION!");}
                 
                 try{
                 String test_string = Testing.INSTANCE.testParameterReturnString(new String("Welcome Back!"));
                 System.out.println("Test: testParameterReturnString - SUCCESSFULL!" + " String returned: " + test_string);
                 }catch(SecurityException ex){System.out.println("Test: testParameterReturnString - EXCEPTION!");}
                  
                 return sys_result;
                 }
                 });
           
            return result;
    }

  
   public void javaPerformSwitchByCut(int src_index, int dest_index)
   { 
       final int src = src_index;
       final int dest = dest_index;
       AccessController.doPrivileged(new PrivilegedAction(){
          public Object run(){
            Testing.INSTANCE.performCut(enumerateInputIdArrayRef[src],enumerateInputIdArrayRef[dest]);
            return null;
          }
      });
   }
   
   public void javaPerformSwitchByCutIndex(int src_index,int dest_index)
   {       
       final int src = src_index;
       final int dest = dest_index;
   
        AccessController.doPrivileged(new PrivilegedAction(){
          public Object run(){
            Testing.INSTANCE.performCutByIndex(src,dest);
            return null;
          }
      });
   }
   
   public void javaPerformSwitchBySetSource(int src_index,int dest_index)
   {
       final int src = src_index;
       final int dest = dest_index;
   
        AccessController.doPrivileged(new PrivilegedAction(){
          public Object run(){
            Testing.INSTANCE.performAuxOutputSourceChange(enumerateInputIdArrayRef[src],enumerateInputIdArrayRef[dest]);
            return null;
          }
      });
   }
   
   public void javaPerformSwitchBySetSourceIndex(int src_index,int dest_index)
   {
       final int src = src_index;
       final int dest = dest_index;
   
        AccessController.doPrivileged(new PrivilegedAction(){
          public Object run(){
            Testing.INSTANCE.performAuxOutputSourceChangeByIndex(src,dest);
            return null;
          }
      });
   
   }
   
   public boolean javaSwitcherIsConnected()
   {
    return Testing.connectionStatus;
   }

  public String javaConnectToSwitcher(String switcher_ip_address)
  {
      System.err.println("Connecting to ip address: " + switcher_ip_address);
      final String ip = switcher_ip_address;
      return AccessController.doPrivileged(new PrivilegedAction<String>(){  
                 public String run(){
      
      int error = Testing.INSTANCE.connectToSwitcher(1,ip);
      if(error == -2)
          System.err.println("COULD NOT ACQUIRE MIX BLOCK");
      if(error == -1)
      {
          System.err.println("SWITCHER CONNECTED!!!");
          Testing.connectionStatus = true;
          String name = Testing.INSTANCE.getSwitcherName(1);
          return "YOUR ARE CONNECTED TO -- " + name;
      }
      
      String errorcode = errorCodeInterpreter(error);
      System.err.println("Connect result: " + errorcode);
                 
      return errorcode;}
      });
  }
  
  public void javaEnumerateInputArray()
  {
      AccessController.doPrivileged(new PrivilegedAction(){
          public Object run(){
            IntByReference input_array_size_ref = new IntByReference();
            Testing.INSTANCE.enumerateInputArrayGetSize(input_array_size_ref);
            int input_array_size = input_array_size_ref.getValue();
            
            enumerateInputArrayRef = new String[input_array_size];
            enumerateInputPortTypeArrayRef = new String[input_array_size];
            enumerateInputIdArrayRef = new long[input_array_size];
            
            Testing.INSTANCE.enumerateInputArray(enumerateInputArrayRef,enumerateInputIdArrayRef,enumerateInputPortTypeArrayRef);
                        
            input_array_size_ref = null;

            return null;
          }
      });
   }
  
   public String[] javaGetEnumeratedInputNamesArray()
   {
       return enumerateInputArrayRef;
   }
   
   public String[] javaGetEnumeratedInputPortTypesArray()
   {
       return enumerateInputPortTypeArrayRef;
   }
   
   public long[] javaGetEnumeratedInputIdsArray()
   {
       return enumerateInputIdArrayRef;
   }

  
  public String errorCodeInterpreter(int error_code)
  {
     int errorSwitchCaseNumber=0;
     
     for(int x=0;x<connectionErrorCodeArraySize;x++)
      {
           if(this.connectErrorCodeArray[x] == error_code)
               errorSwitchCaseNumber = x;
      }
 
     switch(errorSwitchCaseNumber)
     {
        case 0: return "Failure CorruptData";
        case 1: return "Failure Incompatible Firmware";
        case 2: return "Failure No Response";
        case 3: return "Failure State Sync";
        case 4: return "Failure State Sync Timed Out";
        default: break;
     }
     
     return "No Errors Found";
  }
  
} //******END OF TESTING CLASS*****

