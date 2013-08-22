package es.urjc.escet.gsyc.p2p.tipos;

import java.io.Serializable;

public class DescriptorFichero implements Serializable, Comparable {

  private String nombreFichero;
  private long bytesFichero;
  private long ultimaModificacion;

  public int compareTo(Object other){
    DescriptorFichero df = (DescriptorFichero)other;
    return nombreFichero.compareTo(df.nombreFichero);
    }
  
  public DescriptorFichero (String nombreFichero, long bytesFichero, long ultimaModificcacion){
    this.nombreFichero = nombreFichero;
    this.bytesFichero = bytesFichero;
    this.ultimaModificacion = ultimaModificcacion;
    }
  
  public String getNombreFichero(){
    return this.nombreFichero;
    }
  public long getBytesFichero(){
    return this.bytesFichero;
    }
  public long getUltimaModificacion(){
    return this.ultimaModificacion;
    }
  
}