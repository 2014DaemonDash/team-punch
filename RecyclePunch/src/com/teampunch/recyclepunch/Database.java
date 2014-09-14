package com.teampunch.recyclepunch;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

public class Database
{	
	private ArrayList<DatabaseLocation> locations;
	
	public Database()
	{
		locations = new ArrayList<DatabaseLocation>();
	}
	
	public static Database loadDataFromStorage(Context context)
	{
		Database db = new Database();
        try
        {
			db.loadData(context.openFileInput("data"));
		}
        catch (FileNotFoundException e)
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        catch (IOException e)
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return db;
	}
	
	public static void loadDataFromWeb(Context context)
	{
		try
        {
			URL url = new URL("https://raw.githubusercontent.com/2014DaemonDash/team-punch/master/RecyclePunch/data");
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try
			{
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				Database db = new Database();
				db.loadData(new InputStreamReader(in));
				FileOutputStream fos = context.openFileOutput("data", Context.MODE_PRIVATE);
				db.saveData(fos);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				urlConnection.disconnect();
			}
        }
        catch (MalformedURLException e)
        {
			e.printStackTrace();
		}
        catch (IOException e)
        {
        	e.printStackTrace();
        }
	}
	
	public void loadData(Reader rd) throws IOException
	{
		try
		{
			BufferedReader brd = new BufferedReader(rd);
			
			int numBins = Integer.parseInt(brd.readLine());
			for (int i=0; i<numBins; i++)
			{
				DatabaseLocation loc = new DatabaseLocation();
				loc.loadData(brd);
				locations.add(loc);
			}
		}
		catch (NumberFormatException e)
		{
			
		}
	}
	
	public void loadData(InputStream is) throws IOException
	{
		DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
		
		int numBins = dis.readInt();
		for (int i=0; i<numBins; i++)
		{
			DatabaseLocation loc = new DatabaseLocation();
			loc.loadData(dis);
			locations.add(loc);
		}
	}
	
	public void saveData(OutputStream os) throws IOException
	{
		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(os));
		
		dos.writeInt(locations.size());
		for (DatabaseLocation loc : locations)
		{
			loc.saveData(dos);
		}
		
		dos.close();
	}
	
	public ArrayList<DatabaseLocation> getLocations(){
		return locations;
	}
	
	public class DatabaseLocation
	{
		private double x, y; //longitude, latitude, in degrees
		private byte type;
		
		public void loadData(BufferedReader brd) throws IOException, NumberFormatException
		{
			x = Double.parseDouble(brd.readLine());
			y = Double.parseDouble(brd.readLine());
			type = Byte.parseByte(brd.readLine());
		}
		
		public void loadData(DataInputStream dis) throws IOException
		{
			x = dis.readDouble();
			y = dis.readDouble();
			type = dis.readByte();
		}
		
		public void saveData(DataOutputStream dos) throws IOException
		{
			dos.writeDouble(x);
			dos.writeDouble(y);
			dos.writeByte(type);
		}
		
		public LatLng toCoord(){
			return new LatLng(x, y);
		}
		
		public double getX(){ return x; }
		public double getY(){ return y; }
		public byte getType(){ return type; }
		public void setX(double x){ this.x = x; }
		public void setY(double y){ this.y = y; }
		public void setType(byte type){ this.type = type; }
	}
}
