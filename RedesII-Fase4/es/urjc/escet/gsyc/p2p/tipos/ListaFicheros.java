package es.urjc.escet.gsyc.p2p.tipos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class ListaFicheros implements Serializable{

  private ArrayList<DescriptorFichero> listaFicheros;
  
  public ListaFicheros(ArrayList<DescriptorFichero> listaFicheros){
    if(listaFicheros == null)
    listaFicheros = new ArrayList<DescriptorFichero>();
    else
    this.listaFicheros = listaFicheros;
    
    Collections.sort(this.listaFicheros);
    }
  
  public int getNumFicheros(){
    return this.listaFicheros.size();
    }
  
  public DescriptorFichero getFichero(int pos){
    if(pos < 0 || pos >= this.listaFicheros.size())
    return null;
    
    return this.listaFicheros.get(pos);
    }
}