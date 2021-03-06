package repository;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import domain.Candidat;
import domain.Sectie;

@SuppressWarnings("unused")
public class SerializableIO {
	
	public static void serializeCandidat(String fName, ArrayList<Candidat> list)
	{
		ObjectOutputStream os=null;
		try {
			os=new ObjectOutputStream(new FileOutputStream(fName));
			os.writeObject(list);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (os!=null){
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Candidat>  deserializeCandidat(String fName)
	{
		ArrayList<Candidat> list=null;
		ObjectInputStream os=null;
		try {
			os=new ObjectInputStream(new FileInputStream(fName));
			list=new ArrayList<>();
			try {
				list=(ArrayList<Candidat>)os.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if (os!=null){
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return list;
	}
	
	public static void serializeSectie(String fName, ArrayList<Sectie> list)
	{
		ObjectOutputStream os1=null;
		try {
			os1=new ObjectOutputStream(new FileOutputStream(fName));
			os1.writeObject(list);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (os1!=null)
			{
				try {
					os1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Sectie>  deserializeSectie(String fName)
	{
		ArrayList<Sectie> list=null;
		ObjectInputStream os1=null;
		try {
			os1=new ObjectInputStream(new FileInputStream(fName));
			list=new ArrayList<>();
			try {
				list=(ArrayList<Sectie>)os1.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if (os1!=null){
				try {
					os1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
}