import java.io.*;

//Classe construtora dos generos
public class Genero {
	private int idGenero;
	private String nomeGenero;

	public Genero() {
	}//end Genero()

	/*
	 * Construtor da classe
	 * @param nome do genero
	 * @return Instancia de genero criada com parametros selecionados
	 * */
	public Genero(String nomeGenero) {
		this.nomeGenero = nomeGenero;
	}//end Genero()

	public void setNomeGenero(String nomeGenero) {
		this.nomeGenero = nomeGenero;
	}

	public void setIDGenero(int idGenero){
		this.idGenero = idGenero;
	}

	public String getNomeGenero () {
		return this.nomeGenero;			
	}

	public int getID() {
		return this.idGenero;
	}

	/**
	 * Retorna um vetor de bytes(registro) do Genero corrente
	 **/	
	public byte[] getByteArray() throws IOException {
		ByteArrayOutputStream generos = new ByteArrayOutputStream();
		DataOutputStream saida = new DataOutputStream(generos);

		saida.writeInt(this.idGenero);
		saida.writeUTF(this.nomeGenero);

		return generos.toByteArray();
	}//end getByteArray()

	/**
	 * Recebe um vetor de bytes com o genero e seta no Genero corrente
	 * @param vetor de bytes com informacoes de um genero do arquivo
	 **/
	public void setByteArray(byte[] bytes) throws IOException {
		ByteArrayInputStream generos = new ByteArrayInputStream(bytes);
		DataInputStream entrada = new DataInputStream(generos);

		setIDGenero(entrada.readInt());
		setNomeGenero(entrada.readUTF());

	}//end setByteArray()
}//end Genero
