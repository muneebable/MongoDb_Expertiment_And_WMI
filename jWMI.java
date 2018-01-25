package com.citumpe.ctpTools;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;


public class jWMI 
{
	private static final String CRLF = "\r\n";
	
	/**
	 * Generate a VBScript string capable of querying the desired WMI information.
	 * @param wmiQueryString the query string to be passed to the WMI sub-system.
	 * <br>i.e. "Select * from Win32_ComputerSystem"
	 * @param wmiCommaSeparatedFieldName a comma separated list of the WMI fields to be collected from the query results.
	 * <br>i.e. "Model"
	 * @return the vbscript string.
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
	
	/**
	 * Write the given data string to the given file
	 * @param filename the file to write the data to
	 * @param data a String ofdata to be written into the file
	 * @throws Exception if the output file cannot be written
	 * */
	private static void writeStrToFile(String filename, String data) throws Exception
	{
		FileWriter output = new FileWriter(filename);
		output.write(data);
		output.flush();
		output.close();
		output = null;
	}
	
	/**
	 * Get the given WMI value from the WMI subsystem on the local computer
	 * @param wmiQueryStr the query string as syntactically defined by the WMI reference
	 * @param wmiFieldName the field object that you want to get out of the query results
	 * @return the value
	 * @throws Exception if there is a problem obtaining the value
	 * */
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
	
	/**
	 * Execute the application with the given command line parameters.
	 * @param cmdArray an array of the command line params
	 * @return the output as gathered from stdout of the process
	 * @throws an Exception upon encountering a problem  
	 * */
	private static String execute(String[] cmdArray) throws Exception
	{
		Process process = Runtime.getRuntime().exec(cmdArray);
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String output = "";
		String line = "";
		while((line = input.readLine()) != null)
		{
			//need to filter out lines that don't contain our desired output
			if(!line.contains("Microsoft") && !line.equals(""))
			{
				output += line +CRLF;
			}
		}
		process.destroy();
		process = null;
		return output.trim();
	}
	
	public static void executeDemoQueries(String query)
	{
		try
		{
			System.out.println(getWMIValue("Select * from Win32_ComputerSystem", "Model"));
			System.out.println(getWMIValue("Select Name from Win32_ComputerSystem", "Name"));
			//System.out.println(getWMIValue("Select Description from Win32_PnPEntity", "Description"));
			//System.out.println(getWMIValue("Select Description, Manufacturer from Win32_PnPEntity", "Description,Manufacturer"));
			//System.out.println(getWMIValue("Select * from Win32_Service WHERE State = 'Stopped'", "Name"));
			//this will return everything since the field is incorrect and was not used to a filter
			//System.out.println(getWMIValue("Select * from Win32_Service", "Name"));
			//this will return nothing since there is no field specified
			System.out.println(getWMIValue("Select Name from Win32_ComputerSystem", ""));
			//this is a failing case where the Win32_Service class does not contain the 'Name' field
			System.out.println(getWMIValue("Select * from Win32_DiskDrive", "DeviceID,SerialNumber,PNPDeviceID,Model,Manufacturer,MediaType,FirmwareRevision,DeviceID,Partitions,Size"));
			//System.out.println(getWMIValue("Select * from Win32_VideoController Where DeviceID='VideoController1'","PNPDeviceID, DriverVersion"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] argv)
	{
		try
		{
			getWMIValue("Select * from Win32_ComputerSystem", "Model");
			//	executeDemoQueries(query);
			
			
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	

}
