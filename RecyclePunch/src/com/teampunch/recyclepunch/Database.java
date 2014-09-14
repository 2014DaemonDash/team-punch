package com.teampunch.recyclepunch;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;

public class Database
{	
	private ArrayList<Location> locations;
	
	public Database()
	{
		locations = new ArrayList<Location>();
	}
	
	public void loadData(Reader rd) throws IOException
	{
		try
		{
			BufferedReader brd = new BufferedReader(rd);
			
			int numBins = Integer.parseInt(brd.readLine());
			for (int i=0; i<numBins; i++)
			{
				Location loc = new Location();
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
			Location loc = new Location();
			loc.loadData(dis);
			locations.add(loc);
		}
	}
	
	public void saveData(OutputStream os) throws IOException
	{
		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(os));
		
		dos.writeInt(locations.size());
		for (Location loc : locations)
		{
			loc.saveData(dos);
		}
	}
	
	public class Location
	{
		public double x, y; //longitude, latitude, in degrees
		public byte type;
		
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
	}
}