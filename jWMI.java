package com.citumpe.ctpTools;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;


public class jWMI 
{
	private static final String CRLF = "\r\n";
	
	static Logger logger = Logger.getLogger("myLogger");
	/*
	 * 
	 * To get object class of the query
	 * 
	 * */
	private static String getVBScript(String wmiQueryStr, String wmiCommaSeparatedFieldName)
	{
		
		String vbs = "Dim oWMI : Set oWMI = GetObject(\"winmgmts:\")"+CRLF;
		vbs += "Dim classComponent : Set classComponent = oWMI.ExecQuery(\""+wmiQueryStr+"\")"+CRLF;
		vbs += "Dim obj, strData"+CRLF;
		vbs += "For Each obj in classComponent"+CRLF;
		String[] wmiFieldNameArray = wmiCommaSeparatedFieldName.split(",");
		for(int i = 0; i < wmiFieldNameArray.length; i++)
		{
			

			vbs += "  strData = strData & obj."+wmiFieldNameArray[i]+" & VBCrLf"+CRLF;
		}
		vbs += "Next"+CRLF;
		vbs += "wscript.echo strData"+CRLF;
		return vbs;
	}

	private static String getEnvVar(String envVarName) throws Exception
	{
		String varName = "%"+envVarName+"%";
		String envVarValue = execute(new String[] {"cmd.exe", "/C", "echo "+varName});
		if(envVarValue.equals(varName))
		{
			throw new Exception("Environment variable '"+envVarName+"' does not exist!");
		}
		return envVarValue;
	}
	
	/*
	 * Write the query in tmp file to execute it
	 * 
	 * */
	private static void writeStrToFile(String filename, String data) throws Exception
	{
		FileWriter output = new FileWriter(filename);
		output.write(data);
		output.flush();
		output.close();
		output = null;
	}
	

	public static String getWMIValue(String wmiQueryStr, String wmiCommaSeparatedFieldName) throws Exception
	{
		String vbScript = getVBScript(wmiQueryStr, wmiCommaSeparatedFieldName);
		String tmpDirName = getEnvVar("TEMP").trim();
		String tmpFileName = tmpDirName + File.separator + "jwmi.vbs";
		writeStrToFile(tmpFileName, vbScript);
		String output = execute(new String[] {"cmd.exe", "/C", "cscript.exe", tmpFileName});
		new File(tmpFileName).delete();
				
		return output.trim();
	}
	
	private static String execute(String[] cmdArray) throws Exception
	{
		Process process = Runtime.getRuntime().exec(cmdArray);
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String output = "";
		String line = "";
		while((line = input.readLine()) != null)
		{
			
			if(!line.contains("Microsoft") && !line.equals(""))
			{
				output += line +CRLF;
			}
		}
		process.destroy();
		process = null;
		return output.trim();
	}
	
	public static void executeDemoQueries()
	{
		try
		{
			System.out.println("Model: "+getWMIValue("Select * from Win32_ComputerSystem", "Model"));
			System.out.println("Name: "+getWMIValue("Select Name from Win32_ComputerSystem", "Name"));
			
			//System.out.println(getWMIValue("Select Description from Win32_PnPEntity", "Description"));
			//System.out.println(getWMIValue("Select Description, Manufacturer from Win32_PnPEntity", "Description,Manufacturer"));
			//System.out.println(getWMIValue("Select * from Win32_Service WHERE State = 'Stopped'", "Name"));
			//this will return everything since the field is incorrect and was not used to a filter
			//System.out.println(getWMIValue("Select * from Win32_Service", "Name"));
			//this will return nothing since there is no field specified
			//System.out.println(getWMIValue("Select Name from Win32_ComputerSystem", ""));
			
			System.out.println("Disk Drive DeviceID:"+getWMIValue("Select * from Win32_DiskDrive", "DeviceID"));
			System.out.println("Disk Drive Serial Number:"+getWMIValue("Select * from Win32_DiskDrive", "SerialNumber"));
			System.out.println("Disk Drive Model:"+getWMIValue("Select * from Win32_DiskDrive", "Model"));
			System.out.println("Disk Drive Manufacturere:"+getWMIValue("Select * from Win32_DiskDrive", "Manufacturer"));
			System.out.println("Video Controller Name:"+getWMIValue("Select * from Win32_VideoController Where DeviceID='VideoController1'","Name"));
			System.out.println("Network Name:"+getWMIValue("SELECT * FROM Win32_NetworkAdapter WHERE DeviceID='10' OR DeviceID = '11'","Name"));
			System.out.println("Network MACAddress:"+getWMIValue("SELECT * FROM Win32_NetworkAdapter WHERE DeviceID='10' OR DeviceID = '11'","MACAddress"));
		}
		catch(Exception e)
		{
			logger.log(Level.SEVERE, "Cannot reach the function body");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] argv)
	{
		Logger logger = Logger.getLogger("myLogger");
		try
		{
			logger.log(Level.INFO, "Starting to Get the data From WMI");
			//getWMIValue("Select * from Win32_ComputerSystem", "Model");
				executeDemoQueries();
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	

}
